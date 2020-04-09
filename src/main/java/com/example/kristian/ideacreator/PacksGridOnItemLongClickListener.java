package com.example.kristian.ideacreator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kristian.ideacreator.DbTablesInterfaces.HobbyIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.IdeasPrototype;
import com.example.kristian.ideacreator.DbTablesInterfaces.PacksInfo;
import com.example.kristian.ideacreator.DbTablesInterfaces.ProjectIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.TravelIdeas;

/**
 * Created by Kristian on 27/05/2016.
 */
public class PacksGridOnItemLongClickListener implements AdapterView.OnItemLongClickListener {




    public PacksGridOnItemLongClickListener() {
    }


    private void downLoadPack(Bundle args, View view) {
        long id = args.getLong("id");
        if (id != 0) {
            AsyncTaskPackDownloader async = new AsyncTaskPackDownloader(view.getContext());

            async.execute(id);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final View localView = view;
        final Long localId = id;
        final Bundle args = new Bundle();
        final AdapterView<?> localParent = parent;
        args.putLong("id", localId);


        AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
        builder.setTitle("Select action");
        builder.setItems(new CharSequence[]{"Install pack", "Uninstall pack", "Cancel"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    new AlertDialog.Builder(localParent.getContext())
                            .setTitle("Download pack")
                            .setMessage("Really download this pack?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TextView txtInstalledview = (TextView) localView.findViewById(R.id.packsGridPackInstalled);
                                    String txtInstalled = txtInstalledview.getText().toString();
                                    if(txtInstalled.equals(localView.getResources().getString(R.string.packsAlreadyInstalled))){
                                        Toast.makeText(localView.getContext(), "The pack is already installed", Toast.LENGTH_SHORT).show();
                                        return;
                                    }


                                    if (Shared.idsOfPacksCurrentlyInstalling.contains(localId)) {
                                        Toast.makeText(localView.getContext(), "This pack seems to be already downloading...", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(localView.getContext(), "Starting download...", Toast.LENGTH_SHORT).show();
                                        Shared.idsOfPacksCurrentlyInstalling.add(localId);
                                        downLoadPack(args, localView);
                                    }

                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
                if(which == 1){
                    new AlertDialog.Builder(localParent.getContext())
                            .setTitle("Uninstall pack")
                            .setMessage("Really uninstall this pack?")
                            .setPositiveButton("Uninstall", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(Shared.checkIfUpdating()==false) {
                                        uninstallPack(args, localView);
                                    }else{
                                        Toast.makeText(localView.getContext(), "Unable to do so at the time. Try again later", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }
        });
        builder.show();








        return false;
    }

    private void uninstallPack(Bundle args,View view) {
        AsyncTaskPackUninstaller asyncUninstaller = new AsyncTaskPackUninstaller(view.getContext());
        asyncUninstaller.execute(args);
    }

    class AsyncTaskPackUninstaller extends AsyncTask<Bundle,Void,Void>{
        Context c;

        public AsyncTaskPackUninstaller(Context c) {
            this.c = c;
        }

        @Override
        protected Void doInBackground(Bundle... params) {

            c.getContentResolver().delete(ProjectIdeas.Idea.CONTENT_URI, IdeasPrototype.PACKID+"=?",new String[]{params[0].getLong("id")+""});
            c.getContentResolver().delete(HobbyIdeas.Idea.CONTENT_URI, IdeasPrototype.PACKID+"=?",new String[]{params[0].getLong("id")+""});
            c.getContentResolver().delete(TravelIdeas.Idea.CONTENT_URI, IdeasPrototype.PACKID+"=?",new String[]{params[0].getLong("id")+""});
            c.getContentResolver().call(PacksInfo.Pack.CONTENT_URI, "setPackMode",""+Shared.Pack.PACK_NOT_INSTALLED,params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void voi) {
            Toast.makeText(c, "Pack uninstalled.", Toast.LENGTH_SHORT).show();
            //spa.notifyDataSetChanged();
        }
    }

    class AsyncTaskPackDownloader extends AsyncTask<Long, Void, Integer> {
        Context c;

        public AsyncTaskPackDownloader(Context c) {
            this.c = c;
        }

        @Override
        protected Integer doInBackground(Long... params) {
            Log.i(Shared.LogDb , "AsynctaskPACKdownloader starting...");
            Bundle b = new Bundle();
            b.putLong("id", params[0]);
            c.getContentResolver().call(PacksInfo.Pack.CONTENT_URI, "setPackMode",""+Shared.Pack.PACK_CURRENTLY_INSTALLING,b);

            Bundle downloadPackResult = c.getContentResolver().call(PacksInfo.Pack.CONTENT_URI, "downloadPack", null, b);
            Shared.idsOfPacksCurrentlyInstalling.remove(params[0].longValue());

            if (downloadPackResult == null) {
                c.getContentResolver().call(PacksInfo.Pack.CONTENT_URI, "setPackMode", "" + Shared.Pack.PACK_NOT_INSTALLED, b);
                return -1;
            }
            boolean success = downloadPackResult.getBoolean("result");

            if(success && downloadPackResult.getInt("count")>0) {
                c.getContentResolver().call(PacksInfo.Pack.CONTENT_URI, "setPackMode", "" + Shared.Pack.PACK_INSTALLED, b);
                return downloadPackResult.getInt("count");
            }
            c.getContentResolver().call(PacksInfo.Pack.CONTENT_URI, "setPackMode", "" + Shared.Pack.PACK_NOT_INSTALLED, b);
            return downloadPackResult.getInt("count");
        }

        @Override
        protected void onPostExecute(Integer aInt) {
            if (aInt == 0) {
                Toast.makeText(c, "Error downloading pack.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (aInt == -1) {
                Toast.makeText(c, "Error downloading pack. Can device connect to the internet?", Toast.LENGTH_LONG).show();
                // TODO c.getContentResolver().call(PacksInfo.Pack.CONTENT_URI, "setPackMode", "" + Shared.Pack.PACK_NOT_INSTALLED, );
                return;
            }
            Toast.makeText(c, "Pack successfully installed. Added " + aInt + " new ideas!", Toast.LENGTH_LONG).show();
            Intent serviceIntent = new Intent(c,UpdateNotificationService.class);
            serviceIntent.putExtra("count", aInt);
            c.startService(serviceIntent);
        }
    }


}
