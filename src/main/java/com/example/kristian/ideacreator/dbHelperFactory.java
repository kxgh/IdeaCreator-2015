package com.example.kristian.ideacreator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

/**
 * Created by Kristian on 20/05/2016.
 */
public enum dbHelperFactory {
    INSTANCE;

    private IdeasDatabaseOpenHelper dbHelperInstance;

    public IdeasDatabaseOpenHelper getDbHelperInstance(Context context){
        if (dbHelperInstance == null) {
            Log.i(Shared.LogDebug,"Creating new db helper instance");
            dbHelperInstance = new IdeasDatabaseOpenHelper(context.getApplicationContext());
        }
        return dbHelperInstance;
    }

    public boolean checkIsNull(){
        return dbHelperInstance==null;
    }

}
