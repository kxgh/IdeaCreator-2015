package com.example.kristian.ideacreator;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kristian.ideacreator.DbTablesInterfaces.HobbyIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.IdeasPrototype;
import com.example.kristian.ideacreator.DbTablesInterfaces.ProjectIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.TravelIdeas;

public class ActivityAddIdea extends AppCompatActivity {


    private int obscurityNumber = 0;
    private TextView obscTxt;



    private Category ctgSelected = Category.PROJECT;
    private RadioButton ctgRadioP;
    private RadioButton ctgRadioH;
    private RadioButton ctgRadioT;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("ctgSelected", ctgSelected);
        outState.putInt("obscurityNumber", obscurityNumber);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_add_idea);

        if(savedInstanceState!=null) {
            obscurityNumber = savedInstanceState.getInt("obscurityNumber");
            ctgSelected = savedInstanceState.getSerializable("ctgSelected") != null ? (Category) savedInstanceState.getSerializable("ctgSelected") : Category.PROJECT;
        }


        updatePrefix();
        obscTxt = (TextView) findViewById(R.id.addIdeaObsc);
        changeAndUpdateSelectedObscurity(obscurityNumber);
        SeekBar obscSeekBar = (SeekBar) findViewById(R.id.addIdeaSeekbar);
        if(obscSeekBar!=null){
            obscSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    changeAndUpdateSelectedObscurity(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        ctgRadioP = (RadioButton) findViewById(R.id.addIdeaRadioProject);
        ctgRadioH = (RadioButton) findViewById(R.id.addIdeaRadioHobby);
        ctgRadioT = (RadioButton) findViewById(R.id.addIdeaRadioTravel);

        ctgRadioP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ctgRadioP.setChecked(true);
                ctgRadioH.setChecked(false);
                ctgRadioT.setChecked(false);
                ctgSelected = Category.PROJECT;
                updatePrefix();
            }
        });

        ctgRadioH.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ctgRadioP.setChecked(false);
                ctgRadioH.setChecked(true);
                ctgRadioT.setChecked(false);
                ctgSelected = Category.HOBBY;
                updatePrefix();
            }
        });

        ctgRadioT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ctgRadioP.setChecked(false);
                ctgRadioH.setChecked(false);
                ctgRadioT.setChecked(true);
                ctgSelected = Category.TRAVEL;
                updatePrefix();
            }
        });





    }

    void updatePrefix(){
        TextView prefTxt = (TextView) findViewById(R.id.addIdeaPrefix);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String name = sharedPref.getString(getResources().getString(R.string.stgName), getResources().getString(R.string.stgNameDfl));
        if (prefTxt != null) {
            prefTxt.setText(name+Shared.getPrefix(ctgSelected));
        }
    }

    private void changeAndUpdateSelectedObscurity(int progress){

        this.obscurityNumber = progress;
        if (obscTxt != null) {
            obscTxt.setText(""+this.obscurityNumber);

            if (obscurityNumber >= 9) {
                obscTxt.setTextColor(getResources().getColor(R.color.obscMax));
            } else
            if (obscurityNumber >= 7) {
                obscTxt.setTextColor(getResources().getColor(R.color.obscHigh));
            }else
            if (obscurityNumber >= 4) {
                obscTxt.setTextColor(getResources().getColor(R.color.obscMid));
            }else
            if (obscurityNumber >= 2) {
                obscTxt.setTextColor(getResources().getColor(R.color.obscLow));
            }else
            if (obscurityNumber >= 0) {
                obscTxt.setTextColor(getResources().getColor(R.color.obscMin));
            }

        }
    }

    public void addIdeaButtonClicked(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean ask = sharedPref.getBoolean(getResources().getString(R.string.stgAsk),false);
        if(ask==false){
            saveAdded();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Adding idea")
                .setMessage("Really add this "+Shared.getCategoryFormatted(ctgSelected)+" idea of "+ obscurityNumber +" obscurity to the set of generable ideas?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveAdded();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void saveAdded(){
        EditText editText = (EditText) findViewById(R.id.addIdeaEditText);
        String ideaText = null;
        if (editText == null) {
            return;
        }
        ideaText = editText.getText().toString();
        if(ideaText.length()<3){
            Toast.makeText(this, "Idea too short!", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = ProjectIdeas.Idea.CONTENT_URI;
        if(ctgSelected  == Category.HOBBY)
            uri = HobbyIdeas.Idea.CONTENT_URI;
        if(ctgSelected  == Category.TRAVEL)
            uri = TravelIdeas.Idea.CONTENT_URI;
        ContentValues cv = new ContentValues();
        cv.put(IdeasPrototype.IDEA, ideaText);
        cv.put(IdeasPrototype.OBSCURITY, obscurityNumber);
        AsyncTaskAddIdea async = new AsyncTaskAddIdea();
        async.execute(uri,cv);
        /*Uri insert = getContentResolver().insert(uri, cv);
        if(insert==null){
            Toast.makeText(this, "Error adding idea!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Idea added successfully", Toast.LENGTH_LONG).show();
        }*/
    }
    void saveAddedAsyncResponse(boolean success){
        if(success){
            Toast.makeText(this, "Idea added successfully", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Error adding idea!", Toast.LENGTH_SHORT).show();
        }
    }
    class AsyncTaskAddIdea extends AsyncTask<Object,Void,Boolean>{

        private Uri result = null;

        @Override
        protected Boolean doInBackground(Object... params) {
            this.result = getContentResolver().insert((Uri)params[0],(ContentValues) params[1]);
            return result!=null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            saveAddedAsyncResponse(aBoolean);
        }
    }
}
