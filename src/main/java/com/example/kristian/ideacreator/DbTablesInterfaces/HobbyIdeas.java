package com.example.kristian.ideacreator.DbTablesInterfaces;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.kristian.ideacreator.Shared;

/**
 * Created by student on 31. 3. 2016.
 */
public interface HobbyIdeas {

    public interface Idea extends BaseColumns{ //basecolumns kvoli idcku _id
        public static final String TABLE_NAME = "hobbyIdeas";
        public static final String IDEA = IdeasPrototype.IDEA;
        public static final String OBSCURITY = IdeasPrototype.OBSCURITY;
        public static final String PACKID = IdeasPrototype.PACKID;
        public static final Uri CONTENT_URI = Shared.getTableUri(TABLE_NAME);
    }
}
