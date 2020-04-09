package com.example.kristian.ideacreator;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;
import com.vstechlab.easyfonts.EasyFonts;


public class ActivityMainScreen extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listViewMain;
    private SimpleCursorAdapter listViewMain_adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);


        /*SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int ask = sharedPref.getInt("",false);
        Toast.makeText(this, ""+ask, Toast.LENGTH_SHORT).show();*/

        /*
        * Content provider hack, dont remove
         */
        getContentResolver().call(SavedIdeas.Idea.CONTENT_URI,"mock",null,null);

        TextView logotextView = (TextView) findViewById(R.id.topBarAppName);
        if (logotextView != null) {
            logotextView.setTypeface(EasyFonts.walkwayUltraBold(this));//logotextView.setTypeface(EasyFonts.tangerineBold(this));
        }

        BinderSavedIdeasListView customBinder = new BinderSavedIdeasListView();
        String[] from = {SavedIdeas.Idea.IDEA,SavedIdeas.Idea.OBSCURITY,SavedIdeas.Idea.CATEGORY,SavedIdeas.Idea.DATETIME, SavedIdeas.Idea.ISCOMPLETED};
        listViewMain = (ListView) findViewById(R.id.listViewSavedIdeasMain);
        int[] to = {R.id.savedIdeaTextMain,R.id.savedIdeaObscurityMain,R.id.savedIdeaCategoryIconMain,R.id.savedIdeaCompletedTextMain,R.id.savedIdeaCompletedIconMain};
        listViewMain_adapter = new SimpleCursorAdapter(this, R.layout.savedidea_main, null, from, to, 0);
        listViewMain_adapter.setViewBinder(customBinder);
        listViewMain.setAdapter(listViewMain_adapter);
        getLoaderManager().initLoader(Shared.LOADER_MAIN, Bundle.EMPTY,this );
        getLoaderManager().initLoader(Shared.LOADER_CHECKER, Bundle.EMPTY,this );




    }


    public void newIdeaClicked(View view) {
            Intent myIntent = new Intent(this, ActivityCreationScreen.class);
            this.startActivity(myIntent);
    }

    public void browseIdeasClicked(View view) {
        Intent intent = new Intent(this,ActivityBrowseIdeas.class);
        this.startActivity(intent);
    }




    public void printDebug(View view) {
        //Log.i(Shared.LogDebug, "Selected category: "+Shared.categorySelected+"");
        Log.i(Shared.LogDebug, "Is updating?: "+Shared.checkIfUpdating());
        Log.i(Shared.LogDebug, "Is dbhelper null?: "+dbHelperFactory.INSTANCE.checkIsNull());
    }


    public void settings(View view) {
        Intent intent = new Intent(this,ActivitySettings.class);
        this.startActivity(intent);
        /*
        ImageView imageView = (ImageView) findViewById(R.id.btnSettings);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.move);
        animation.start();
        imageView.startAnimation(animation);
        animation.cancel();*/
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader a = new CursorLoader(this);
        a.setUri(SavedIdeas.Idea.CONTENT_URI);
        a.setSelection(SavedIdeas.Idea.ISCOMPLETED+" = ?");
        a.setSelectionArgs(new String[]{"1"});
        a.setSortOrder(SavedIdeas.Idea.DATETIME+" DESC");
        Log.i(Shared.LogDb, "Done oncreate in main, returning");
        return a;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(Shared.LogDebug, "Swapping data in main screen...");
        if(loader.getId()== Shared.LOADER_MAIN && listViewMain_adapter!=null) {
            listViewMain_adapter.swapCursor(data);
        }
        Log.i(Shared.LogDebug, "Done swapping data in mainscreen...");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader!=null)
            loader.reset();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(Shared.LOADER_MAIN,Bundle.EMPTY,this);
    }

    /* @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this);
        Uri uri = SavedIdeas.Idea.CONTENT_URI;
        cursorLoader.setUri(uri);
        Log.i(Shared.LogDb, "Done oncreate in browse, returning");
        return cursorLoader;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(Shared.LogDebug, "Swapping data in main screen...");
        if(loader.getId()==0 && listViewMain_adapter!=null) {
            listViewMain_adapter.swapCursor(data);
        }
        Log.i(Shared.LogDebug, "Done swapping data in mainscreen...");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }*/
}
