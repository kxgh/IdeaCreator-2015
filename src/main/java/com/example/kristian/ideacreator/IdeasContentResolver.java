package com.example.kristian.ideacreator;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Kristian on 23/05/2016.
 */
public class IdeasContentResolver extends ContentResolver {
    public IdeasContentResolver(Context context) {
        super(context);
    }
}
