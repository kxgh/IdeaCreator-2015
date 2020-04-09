package com.example.kristian.ideacreator.DbTablesInterfaces;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.kristian.ideacreator.Shared;

/**
 * Created by Kristian on 18/05/2016.
 */
public interface TravelIdeas {
    public interface Idea extends BaseColumns { //basecolumns kvoli idcku
        public static final String TABLE_NAME = "travelIdeas";
        public static final String IDEA = IdeasPrototype.IDEA;
        public static final String OBSCURITY = IdeasPrototype.OBSCURITY;
        public static final String PACKID = IdeasPrototype.PACKID;
        public static final Uri CONTENT_URI = Shared.getTableUri(TABLE_NAME);
    }
}
