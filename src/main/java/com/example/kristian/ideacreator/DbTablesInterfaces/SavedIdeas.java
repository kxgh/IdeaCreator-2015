package com.example.kristian.ideacreator.DbTablesInterfaces;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.kristian.ideacreator.Shared;

/**
 * Created by Kristian on 18/05/2016.
 */
public interface SavedIdeas {
    public interface Idea extends BaseColumns { //basecolumns kvoli idcku
        public static final String TABLE_NAME = "savedIdeas";
        public static final String IDEA = IdeasPrototype.IDEA;
        public static final String OBSCURITY = IdeasPrototype.OBSCURITY;
        public static final String CATEGORY = "category";
        public static final String DATETIME = "datetime"; // timestamp of time idea has been marked as completed
        public static final String ISCOMPLETED = "completed";
        public static final Uri CONTENT_URI = Shared.getTableUri(TABLE_NAME);
    }
}
