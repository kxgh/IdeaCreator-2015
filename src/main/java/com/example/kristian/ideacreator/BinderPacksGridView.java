package com.example.kristian.ideacreator;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kristian.ideacreator.DbTablesInterfaces.PacksInfo;
import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;

import java.text.SimpleDateFormat;

/**
 * Created by Kristian on 27/05/2016.
 */
public class BinderPacksGridView implements SimpleCursorAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        int viewId = view.getId();
        if (viewId == R.id.packsGridPackName) {
            String packName = cursor.getString(cursor.getColumnIndex(PacksInfo.Pack.PACKNAME));
            ((TextView) view).setText(packName);
            return true;
        }
        if (viewId == R.id.packsGridPackInstalled) {
            String packIsInstalled = cursor.getString(cursor.getColumnIndex(PacksInfo.Pack.ISINSTALLED));
            String packIsInstalledResult;
            TextView txt = (TextView) view;
            if (packIsInstalled.equals(""+Shared.Pack.PACK_INSTALLED)) {
                packIsInstalledResult = view.getResources().getString(R.string.packsAlreadyInstalled);
                txt.setTextColor(view.getResources().getColor(R.color.colorOk));
            } else {
                if(packIsInstalled.equals(""+Shared.Pack.PACK_CURRENTLY_INSTALLING)){
                    packIsInstalledResult = view.getResources().getString(R.string.packsCurrentlyInstalling);
                    txt.setTextColor(view.getResources().getColor(R.color.colorWarn));
                }else {
                    packIsInstalledResult = view.getResources().getString(R.string.packsNotInstalled);
                    txt.setTextColor(view.getResources().getColor(R.color.colorNotOk));
                }
            }
            txt.setText(packIsInstalledResult);
            return true;
        }
        return false;
    }
}
