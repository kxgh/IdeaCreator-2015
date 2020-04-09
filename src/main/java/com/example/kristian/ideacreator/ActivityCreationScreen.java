package com.example.kristian.ideacreator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.rangebar.RangeBar;
import com.example.kristian.ideacreator.DbTablesInterfaces.HobbyIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.ProjectIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.TravelIdeas;

public class ActivityCreationScreen extends AppCompatActivity{

    private byte currentObscMin;
    private byte currentObscMax;

    private AsyncIdeaGenerator asyncIdeaGenerator;
    private AsyncIdeaSaver asyncIdeaSaver;

    private IdeaObject generatedIdea;
    private boolean creationSuccess;

    private RangeBar rngObscurity;
    private TextView selectedObscTextView;

    private Category ctgSelected;


    private void setCtgSelected(int position) {
        if (position == 0) {
            ctgSelected = Category.PROJECT;
        } else if (position == 1) {
            ctgSelected = Category.HOBBY;
        } else ctgSelected = Category.TRAVEL;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("ctgSelected",ctgSelected);
        outState.putByte("currentObscMin",currentObscMin);
        outState.putByte("currentObscMax",currentObscMax);
        outState.putBoolean("creationSuccess",creationSuccess);
        outState.putSerializable("generatedIdea",generatedIdea);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_screen);

        if(savedInstanceState!=null){
            ctgSelected = savedInstanceState.getSerializable("ctgSelected")!=null ? (Category) savedInstanceState.getSerializable("ctgSelected") : Category.PROJECT;
            currentObscMin = savedInstanceState.getByte("currentObscMin");
            currentObscMax = savedInstanceState.getByte("currentObscMax");
            creationSuccess = savedInstanceState.getBoolean("creationSuccess");
            generatedIdea = (IdeaObject) savedInstanceState.getSerializable("generatedIdea");
            if(generatedIdea==null){
                // nothing, continue
            } else{
                showIdeaCreatedLayout(creationSuccess);
            }
        }

        selectedObscTextView = (TextView) findViewById(R.id.txtObscSelected);

        rngObscurity = (RangeBar) findViewById(R.id.rngObscurity);
        rngObscurity.setTickCount(11);
        /*rngObscurity.setBarColor(R.color.colorPrimary);
        rngObscurity.setThumbColorNormal(R.color.colorPrimary);
        rngObscurity.setThumbColorPressed(R.color.colorAccent);
        rngObscurity.setConnectingLineColor(R.color.colorAccent);*/
        rngObscurity.setBarWeight(5);
        rngObscurity.setConnectingLineWeight(15);
        //rngObscurity.setRight(5);
        currentObscMin = (byte) rngObscurity.getLeftIndex();
        currentObscMax = (byte) rngObscurity.getRightIndex();
        selectedObscTextView.setText(getResources().getString(R.string.creation_selectedObscurityText) + " from " + currentObscMin + " to " + currentObscMax + ": " + getComment(currentObscMin, currentObscMax));
        rngObscurity.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1) {
                currentObscMin = (byte) i;
                currentObscMax = (byte) i1;
                if (selectedObscTextView != null) {
                    String comment = getComment(currentObscMin, currentObscMax);
                    if (currentObscMin != currentObscMax)
                        selectedObscTextView.setText(getResources().getString(R.string.creation_selectedObscurityText) + " from " + currentObscMin + " to " + currentObscMax + ": " + comment);
                    else
                        selectedObscTextView.setText(getResources().getString(R.string.creation_selectedObscurityText) + " " + currentObscMin + ": " + comment);
                }

            }
        });

        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/

        Spinner spnIdeaCategory = (Spinner) findViewById(R.id.spnIdeaCategory);
        ArrayAdapter<CharSequence> spnIdeaCategoryAdapter = ArrayAdapter.createFromResource(this, R.array.ideaCategories, android.R.layout.simple_spinner_item);
        if (spnIdeaCategory != null) {
            spnIdeaCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setCtgSelected(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }


        spnIdeaCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spnIdeaCategory != null) {
            spnIdeaCategory.setAdapter(spnIdeaCategoryAdapter);
        } else Log.e(Shared.LogDb, "Nepodarilo sa naplnit category spinner");
    }

    private String getComment(int i, int j) {
        if (i + j >= 18)
            return "MADMAN";
        if (i + j >= 15)
            return "good luck";
        if (i + j >= 12)
            return "brave";
        if (i + j >= 9)
            return "challenging";
        if (i + j >= 6)
            return "casual";
        if (i + j >= 3)
            return "safe";
        if (i + j >= 0)
            return "coward";
        return "";
    }

    private void saveIdea(){
        asyncIdeaSaver = new AsyncIdeaSaver(ActivityCreationScreen.this);
        asyncIdeaSaver.execute(generatedIdea);
    }


    private void showIdeaCreatedLayout(boolean creationSuccess){
        ImageView creationBulb = (ImageView) findViewById(R.id.creationBulb);
        TextView createdIdea = (TextView) findViewById(R.id.createdIdeaText);
        LinearLayout createdIdeaLayout = (LinearLayout) findViewById(R.id.createdIdeaLayout);

        creationBulb.setImageResource(R.drawable.logo3);


        if(!creationSuccess){
            Log.i(Shared.LogDebug, "Erorr creating Idea. Received null from asynctask");
            createdIdeaLayout.setVisibility(View.INVISIBLE);
        }
        else {
            Animation animation = AnimationUtils.loadAnimation(this,R.anim.fadein);//android.R.anim.fade_in);
            animation.setRepeatCount(Animation.INFINITE);
            createdIdea.startAnimation(animation);
            animation.setRepeatCount(0);

            createdIdea.setText(this.generatedIdea.getText());
            createdIdeaLayout.setVisibility(View.VISIBLE);
        }
    }
    private void showIdeaLoadingLayout(){
        ImageView creationBulb = (ImageView) findViewById(R.id.creationBulb);
        LinearLayout createdIdeaLayout = (LinearLayout) findViewById(R.id.createdIdeaLayout);

        creationBulb.setImageResource(R.drawable.wait);
        createdIdeaLayout.setVisibility(View.INVISIBLE);
    }


    public void saveIdeaClicked(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean ask = sharedPref.getBoolean(getResources().getString(R.string.stgAsk),false);

        if(asyncIdeaSaver!=null){
            if(asyncIdeaSaver.getStatus().equals(AsyncTask.Status.RUNNING)) {
                Log.i(Shared.LogDebug,"Not saving idea. Saving already in progress.");
                return;
            }
        }
        if(ask){
        new AlertDialog.Builder(this)
                .setTitle("Save idea")
                .setMessage("Really save idea?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveIdea();
                    }
                })
                .setNegativeButton("No", null)
                .show();}
        else{
            saveIdea();
        }

    }

    public void createIdea(View view) {
        if(asyncIdeaGenerator!=null){
            if(asyncIdeaGenerator.getStatus().equals(AsyncTask.Status.RUNNING)) {
                Log.i(Shared.LogDebug,"Not creating new idea. Creation already in progress.");
                return;
            }
        }

        if (!Shared.checkIfUpdating()) {

            showIdeaLoadingLayout();

            asyncIdeaGenerator = new AsyncIdeaGenerator(ctgSelected,this.currentObscMin,this.currentObscMax);
            asyncIdeaGenerator.execute(this);
        } else {
            Toast toast = Toast.makeText(this, getResources().getString(R.string.dbBeingUpdatedMsg), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void ideaCreated(IdeaObject ideaObject) {
        Log.i(Shared.LogDb, "Ideacreated method called");
        creationSuccess = !(ideaObject==null);
        if(ideaObject==null){
            Toast.makeText(this,getResources().getString(R.string.creation_noSuchIdea),Toast.LENGTH_LONG).show();
            showIdeaCreatedLayout(false);
            return;
        }
        this.generatedIdea = ideaObject;
        showIdeaCreatedLayout(true);

    }


    protected class AsyncIdeaSaver extends AsyncTask<IdeaObject, Void, Void> {

        private Context context;
        private boolean success;

        public AsyncIdeaSaver(Context context) {
            this.context = context;
            success=false;
        }

        @Override
        protected Void doInBackground(IdeaObject... params) {
            Bundle ideaBundle = new Bundle();
            ideaBundle.putSerializable("idea",params[0]);
            Bundle result = getContentResolver().call(SavedIdeas.Idea.CONTENT_URI,"saveIdea",null,ideaBundle);
            success = result != null && result.getBoolean("success");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(success)
                Toast.makeText(context, getResources().getString(R.string.toastIdeaSaved), Toast.LENGTH_SHORT).show();
            else Toast.makeText(context, getResources().getString(R.string.toastIdeaSavingFailure), Toast.LENGTH_SHORT).show();
        }
    }

    protected class AsyncIdeaGenerator extends AsyncTask<Context, Void, Void> {
        private Category ctg;
        private byte obscMin;
        private byte obscMax;
        private IdeaObject ideaObject;

        public AsyncIdeaGenerator(Category ctg, byte obscMin, byte obscMax) {
            this.ctg = ctg;
            this.obscMin = obscMin;
            this.obscMax = obscMax;
        }


        @Override
        protected Void doInBackground(Context... params) {

            if (ctg == null) {
                Log.e(Shared.LogDebug, "Erorr creating Idea. Received null category");
                return null;
            }
            if(params[0]==null){
                Log.e(Shared.LogDebug, "Erorr creating Idea. Received null context");
                return null;
            }
            Uri contentUri = ProjectIdeas.Idea.CONTENT_URI;
            if(ctg==Category.TRAVEL)
                contentUri= TravelIdeas.Idea.CONTENT_URI;
            if(ctg==Category.HOBBY)
                contentUri= HobbyIdeas.Idea.CONTENT_URI;
            if(ctg==Category.PROJECT)
                contentUri= ProjectIdeas.Idea.CONTENT_URI;

            Bundle bundle = new Bundle();
            bundle.putByte("obscMin",obscMin);
            bundle.putByte("obscMax",obscMax);
            Bundle generateIdea = getContentResolver().call(contentUri, "generateIdea", ctg.toString(), bundle);
            this.ideaObject = (IdeaObject) generateIdea.getSerializable("idea"); //new IdeaObject(generateIdea.getLong("id"),Shared.stringToCategory(generateIdea.getString("category")),generateIdea.getString("idea"));
            if(this.ideaObject==null)
                return null;
            String name = "You";
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            name = sharedPref.getString(getResources().getString(R.string.stgName), getResources().getString(R.string.stgNameDfl));
            name = ",,"+name;
            this.ideaObject.setText(name+this.ideaObject
                    .getText());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Log.i(Shared.LogDb, "Generator async task finished");
            ideaCreated(ideaObject);
        }
    }

}
