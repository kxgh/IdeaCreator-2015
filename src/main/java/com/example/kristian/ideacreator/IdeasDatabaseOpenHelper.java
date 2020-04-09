package com.example.kristian.ideacreator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kristian.ideacreator.DbTablesInterfaces.HobbyIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.PacksInfo;
import com.example.kristian.ideacreator.DbTablesInterfaces.ProjectIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;
import com.example.kristian.ideacreator.DbTablesInterfaces.TravelIdeas;

/**
 * Created by student on 31. 3. 2016.
 */
public class IdeasDatabaseOpenHelper extends SQLiteOpenHelper {

    private Context mainContext;

    public IdeasDatabaseOpenHelper(Context context) {
        //super(context, name, factory, version);

        super(context, "ideacreator", null, 1); // version je lubovolne cislo verzia databazy
        mainContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) { // ak este neni vytvorena db tak android sam toto spusti

        Log.i(Shared.LogDb, "Creating tables...");
        String sql;

        sql = "CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY AUTOINCREMENT," + "%s TEXT," + "%s INTEGER, %s INTEGER" + ")";
        db.execSQL(String.format(sql, HobbyIdeas.Idea.TABLE_NAME, HobbyIdeas.Idea._ID, HobbyIdeas.Idea.IDEA, HobbyIdeas.Idea.OBSCURITY, HobbyIdeas.Idea.PACKID));

        sql = "CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY AUTOINCREMENT," + "%s TEXT," + "%s INTEGER, %s INTEGER" + ")";
        db.execSQL(String.format(sql, TravelIdeas.Idea.TABLE_NAME, TravelIdeas.Idea._ID, TravelIdeas.Idea.IDEA, TravelIdeas.Idea.OBSCURITY, TravelIdeas.Idea.PACKID));

        sql = "CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY AUTOINCREMENT," + "%s TEXT," + "%s INTEGER, %s INTEGER" + ")";
        db.execSQL(String.format(sql, ProjectIdeas.Idea.TABLE_NAME, ProjectIdeas.Idea._ID, ProjectIdeas.Idea.IDEA, ProjectIdeas.Idea.OBSCURITY,ProjectIdeas.Idea.PACKID));

        sql = "CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY AUTOINCREMENT," + "%s TEXT," + "%s INTEGER, %s TEXT, %s INTEGER, %s INTEGER" + ")";
        db.execSQL(String.format(sql, SavedIdeas.Idea.TABLE_NAME, SavedIdeas.Idea._ID, SavedIdeas.Idea.IDEA, SavedIdeas.Idea.OBSCURITY, SavedIdeas.Idea.CATEGORY,SavedIdeas.Idea.ISCOMPLETED , SavedIdeas.Idea.DATETIME));

        sql = "CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY AUTOINCREMENT," + "%s INTEGER," + "%s TEXT, %s TEXT, %s INTEGER" + ")";
        db.execSQL(String.format(sql,PacksInfo.Pack.TABLE_NAME,PacksInfo.Pack._ID, PacksInfo.Pack.PACKID, PacksInfo.Pack.PACKNAME, PacksInfo.Pack.PACKURL, PacksInfo.Pack.ISINSTALLED));


        /*sql = "CREATE TABLE %s (" + "%s INTEGER PRIMARY KEY AUTOINCREMENT," + "%s INTEGER)";
        db.execSQL(String.format(sql, SettingsKeys.Idea.TABLE_NAME, SettingsKeys.Idea._ID, SettingsKeys.Idea.UPDATING));
        //sql = "UPDATE TABLE %s SET %s='1'";
        ContentValues contentValues = new ContentValues();
        contentValues.put(SettingsKeys.Idea.UPDATING,1);
        db.insert(SettingsKeys.Idea.TABLE_NAME,null,contentValues);
        //db.execSQL(String.format(sql, SettingsKeys.Idea.TABLE_NAME, SettingsKeys.Idea.UPDATING));*/

        Log.i(Shared.LogDb, "Done");

        insertDefaultIdeas(mainContext);

    }

    private void insertDefaultIdeas(Context context) {
        AsyncTaskRollbackAndInsertFromFile asyncTaskRollbackAndInsertFromFile = new AsyncTaskRollbackAndInsertFromFile(context);
        Shared.setIsUpdating(true);
        asyncTaskRollbackAndInsertFromFile.execute();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // ak upgrade na novu verziu tak ze co ma ponechat atd

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
