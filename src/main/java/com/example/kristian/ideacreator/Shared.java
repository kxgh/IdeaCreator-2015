package com.example.kristian.ideacreator;

import static android.content.ContentResolver.SCHEME_CONTENT;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * Created by Kristian on 18/05/2016.
 */
public class Shared {
    public static final String LogDb = "Database";
    public static final String LogNotif = "Notifications";
    public static final String LogDebug= "Debug";

    public static final String fileDelimiter = ";";
    public static final String fileCtgHobby = "h";
    public static final String fileCtgTravel = "t";
    public static final String fileCtgProject = "p";

    public static final int LOADER_MAIN= 4;
    public static final int LOADER_ID = 5;
    public static final int LOADER_CHECKER = 6;

    public interface Pack{
        public static final int PACK_NOT_INSTALLED = -1;
        public static final int PACK_CURRENTLY_INSTALLING = 0;
        public static final int PACK_INSTALLED = 1;

    }

    public static AtomicBoolean isUpdating = new AtomicBoolean(false);
    public static CopyOnWriteArraySet<Long> idsOfPacksCurrentlyInstalling = new CopyOnWriteArraySet<>();


    public static final String AUTHORITY = "com.example.kristian.ideacreator.provider.IdeasContentProvider";


    public static Uri getTableUri(String tableName){
        Uri uri = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(tableName)
                .build();
        return uri;
    }
    public static Uri getTableUriWithParams(String tableName, LinkedList<String> paramKeys, LinkedList<String> paramValues){
        Uri.Builder b = new Uri.Builder();
        b.scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(tableName);
        for(int i=0;i<paramKeys.size();i++){
            b.appendQueryParameter(paramKeys.get(i),paramValues.get(i));
        }

        return b.build();
    }


    public static void setIsUpdating(boolean b){
        isUpdating.set(b);
    }

    public static Category stringToCategory(String s){
        if(s.equals(Category.HOBBY.toString()))
            return Category.HOBBY;
        if(s.equals(Category.PROJECT.toString()))
            return Category.PROJECT;
        if(s.equals(Category.TRAVEL.toString()))
            return Category.TRAVEL;
        return null;
    }

    public static boolean checkIfUpdating(){
        boolean a = isUpdating.get();
        boolean b = !idsOfPacksCurrentlyInstalling.isEmpty();
        return a || b;
        //return isUpdating.get();

        /*IdeasDatabaseOpenHelper ideasDatabaseOpenHelper = dbHelperFactory.INSTANCE.getDbHelperInstance(context);
        SQLiteDatabase db = ideasDatabaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM hobbyIdeas", null);

        String data = "";
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            data = cursor.getString(cursor.getColumnIndex(SettingsKeys.Idea.UPDATING));
            Log.i(Shared.LogDb, data);
        }

        if(data.equals("1"))
            return true;
        return false;*/
    }


    public static String getPrefix(Category c){


        if(c == Category.HOBBY)
            return " should ";
        if(c == Category.PROJECT)
            return " should make a project on ";
        if(c == Category.TRAVEL)
            return " should travel to/go see ";
        return "ERROR";
    }
    public static String getCategoryFormatted(Category c){
        if(c == Category.HOBBY)
            return "Hobby";
        if(c == Category.PROJECT)
            return "Project";
        if(c == Category.TRAVEL)
            return "Travel";
        return "ERROR";
    }

    public static String getPacksServerUrl() {
        return "http://s.ics.upjs.sk/~kvrastiak/skola/packInfo";
    }
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }
}
