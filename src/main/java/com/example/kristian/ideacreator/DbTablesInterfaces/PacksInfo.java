package com.example.kristian.ideacreator.DbTablesInterfaces;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.kristian.ideacreator.Shared;

/**
 * Created by student on 31. 3. 2016.
 */
public interface PacksInfo {

    public interface Pack extends BaseColumns{ //basecolumns kvoli idcku _id
        public static final String TABLE_NAME = "packsInfo";
        public static final String PACKID= "packid";
        public static final String PACKNAME = "packname";
        public static final String PACKURL = "packurl";
        public static final String ISINSTALLED = "packisinstalled";
        public static final Uri CONTENT_URI = Shared.getTableUri(TABLE_NAME);
    }
}
