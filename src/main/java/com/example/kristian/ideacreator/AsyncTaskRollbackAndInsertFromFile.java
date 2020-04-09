package com.example.kristian.ideacreator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.kristian.ideacreator.DbTablesInterfaces.HobbyIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.PacksInfo;
import com.example.kristian.ideacreator.DbTablesInterfaces.ProjectIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.TravelIdeas;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class AsyncTaskRollbackAndInsertFromFile extends AsyncTask<Void,Void,Long> {


        Context mainContext;

        public AsyncTaskRollbackAndInsertFromFile(Context context) {
            mainContext = context;
        }

        @Override
        protected Long doInBackground(Void... params) {
            mainContext.getContentResolver().delete(PacksInfo.Pack.CONTENT_URI,null,null);
            mainContext.getContentResolver().delete(SavedIdeas.Idea.CONTENT_URI,null,null);
            mainContext.getContentResolver().delete(ProjectIdeas.Idea.CONTENT_URI,null,null);
            mainContext.getContentResolver().delete(HobbyIdeas.Idea.CONTENT_URI,null,null);
            mainContext.getContentResolver().delete(TravelIdeas.Idea.CONTENT_URI,null,null);


            InputStream is = null;
            Shared.setIsUpdating(true);
            try {
                is = mainContext.getAssets().open("defaultDB.dat");
            } catch (Exception e) {
                Log.e(Shared.LogDb, "Exception in async insert from file "+e.getMessage());
            }
            if(mainContext == null) {
                Log.e(Shared.LogDb, "Null context in async insert from file");
                return -1L;
            }
            /*IdeasDatabaseOpenHelper dbHelper = dbHelperFactory.INSTANCE.getDbHelperInstance(mainContext);
            SQLiteDatabase db = dbHelper.getReadableDatabase();*/
            Log.i(Shared.LogDb,"Opening buffered reader for "+is.toString());

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Log.i(Shared.LogDb,"Done opening, now Reading file...");
            String line;
            Long count = 0L;
            try {
                while((line = reader.readLine())!=null){
                    ContentValues contentValues = new ContentValues();
                    Scanner sc = new Scanner(line);
                    //Log.i(Shared.LogDb,line);
                    sc.useDelimiter(Shared.fileDelimiter);
                    String ctg = sc.next();
                    byte obsc = sc.nextByte();
                    String ideaa = sc.next();
                    //Log.i(Shared.LogDb,ideaa);
                    //detect -ing
                    if(ctg.equals(Shared.fileCtgHobby))
                    if(ideaa.charAt(ideaa.length()-1)=='g' && ideaa.charAt(ideaa.length()-2)=='n' && ideaa.charAt(ideaa.length()-3)=='i'){
                        ideaa = "try "+ideaa;
                    }
                    // Log.i(Shared.LogDb,"Success ing replace");
                    if(ctg.equals(Shared.fileCtgProject)){
                        contentValues.put(ProjectIdeas.Idea.IDEA,ideaa);
                        contentValues.put(ProjectIdeas.Idea.OBSCURITY,obsc);
                        contentValues.put(ProjectIdeas.Idea.PACKID,-1);
                        mainContext.getContentResolver().insert(ProjectIdeas.Idea.CONTENT_URI,contentValues);
                        //db.insert(ProjectIdeas.Idea.TABLE_NAME,null,contentValues);
                    } else if(ctg.equals(Shared.fileCtgHobby)){
                        contentValues.put(HobbyIdeas.Idea.IDEA,ideaa);
                        contentValues.put(HobbyIdeas.Idea.OBSCURITY,obsc);
                        contentValues.put(HobbyIdeas.Idea.PACKID,-1);
                        mainContext.getContentResolver().insert(HobbyIdeas.Idea.CONTENT_URI,contentValues);
                        //db.insert(HobbyIdeas.Idea.TABLE_NAME,null,contentValues);
                    } else if(ctg.equals(Shared.fileCtgTravel)){
                        contentValues.put(TravelIdeas.Idea.IDEA,ideaa);
                        contentValues.put(TravelIdeas.Idea.OBSCURITY,obsc);
                        contentValues.put(TravelIdeas.Idea.PACKID,-1);
                        mainContext.getContentResolver().insert(TravelIdeas.Idea.CONTENT_URI,contentValues);
                        //db.insert(TravelIdeas.Idea.TABLE_NAME,null,contentValues);
                    }
                    count++;
                    if(count%100==0){
                        Log.i(Shared.LogDb,line);
                    }
                    sc.close();
                }
                /*String sql = "UPDATE TABLE %s SET %s='0'";
                db.execSQL(String.format(sql, SettingsKeys.Idea.TABLE_NAME, SettingsKeys.Idea.UPDATING));*/
                reader.close();

            } catch (Exception e) {
                Log.e(Shared.LogDb,"Chyba pri citani suboru v backgrounde "+e.getMessage());
                Shared.setIsUpdating(false);
                return count;
            } finally {
                Log.i(Shared.LogDb, "Done reading file");
            }
            return count;
        }

        @Override
        protected void onPostExecute(Long success) {
            Shared.setIsUpdating(false);
            String toastMsg = mainContext.getResources().getString(R.string.toastDbUpdateCompleted);
            toastMsg = (String.format(toastMsg,success));
            Toast toast = Toast.makeText(mainContext, toastMsg, Toast.LENGTH_LONG);
            toast.show();
            mainContext.startService(new Intent(mainContext, UpdateNotificationService.class));

        }
    }