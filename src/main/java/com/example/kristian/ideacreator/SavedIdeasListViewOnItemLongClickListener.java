package com.example.kristian.ideacreator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kristian.ideacreator.DbTablesInterfaces.HobbyIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.ProjectIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.TravelIdeas;

import java.util.LinkedList;

/**
 * Created by Kristian on 27/05/2016.
 */
public class SavedIdeasListViewOnItemLongClickListener implements AdapterView.OnItemLongClickListener {


    private final FragmentSlidingTabsBrowseSaved.SamplePagerAdapter spa;


    public SavedIdeasListViewOnItemLongClickListener(FragmentSlidingTabsBrowseSaved.SamplePagerAdapter samplePagerAdapter) {
        this.spa = samplePagerAdapter;
    }

    void markRealized(View view, Bundle args){
        AsyncTaskIdeaMark asyncMark = new AsyncTaskIdeaMark(view.getContext());
        asyncMark.execute(args);

    }
    private void removeIdea(View view, Bundle args) {
        AsyncTaskIdeaRemove asyncRemove = new AsyncTaskIdeaRemove(view.getContext());
        asyncRemove.execute(args);

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final View localView = view;
        final Long localId = id;
       /* int loaderId = 0;
        String tableName = "";
        if(parent.getId()==R.id.listViewSavedIdeasP)
            loaderId = 0; else
        if(parent.getId()==R.id.listViewSavedIdeasH)
            loaderId = 1; else
        if(parent.getId()==R.id.listViewSavedIdeasT)
            loaderId = 2;
        final int finalLoaderId = loaderId;*/
        final Bundle args = new Bundle();
        args.putLong("id",localId);

        AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
        builder.setTitle("Select action");
        builder.setItems(new CharSequence[]{"Mark idea as realized", "Remove from list"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    markRealized(localView,args);
                }
                if(which == 1){
                    removeIdea(localView,args);
                }
            }
        });
        builder.show();

        return false;
    }

    class AsyncTaskIdeaRemove extends AsyncTask<Bundle,Void,Integer> {
        Context c;

        public AsyncTaskIdeaRemove(Context c) {
            this.c = c;
        }

        @Override
        protected Integer doInBackground(Bundle... params) {

            Bundle result = c.getContentResolver().call(SavedIdeas.Idea.CONTENT_URI,"removeSaved",null,params[0]);
            int updated = result != null ? result.getInt("removed") : 0;
            return updated;
        }

        @Override
        protected void onPostExecute(Integer updated) {
            if(updated == 0)
                Toast.makeText(c,"Error removing idea. No idea removed",Toast.LENGTH_LONG).show();
            if(updated == 1)
                Toast.makeText(c,"Idea removed",Toast.LENGTH_LONG).show();
            if(updated > 1)
                Toast.makeText(c,"Error: "+updated+" ideas removed.",Toast.LENGTH_LONG).show();
            if(spa!=null)
                spa.notifyDataSetChanged();
        }
    }

    class AsyncTaskIdeaMark extends AsyncTask<Bundle,Void,Integer>{
        Context c;

        public AsyncTaskIdeaMark(Context c) {
            this.c = c;
        }

        @Override
        protected Integer doInBackground(Bundle... params) {
            Bundle result = c.getContentResolver().call(SavedIdeas.Idea.CONTENT_URI,"markRealized",null,params[0]);
            if(result==null){
                return 0;
            }

            return result.getInt("updated");
        }

        @Override
        protected void onPostExecute(Integer updated) {
            if(updated < 1)
                Toast.makeText(c,"Error marking idea. No ideas updated",Toast.LENGTH_LONG).show();
            if(updated == 1)
                Toast.makeText(c,"Idea marked",Toast.LENGTH_LONG).show();
            if(updated > 1)
                Toast.makeText(c,"Error: "+updated+" ideas marked.",Toast.LENGTH_LONG).show();
            if(spa!=null)
                spa.notifyDataSetChanged();
        }
    }

}
