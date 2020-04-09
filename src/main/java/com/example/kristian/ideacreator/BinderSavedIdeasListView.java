package com.example.kristian.ideacreator;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.kristian.ideacreator.DbTablesInterfaces.SavedIdeas;

import java.text.SimpleDateFormat;

/**
 * Created by Kristian on 27/05/2016.
 */
public class BinderSavedIdeasListView implements SimpleCursorAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        int viewId = view.getId();
        if(viewId == R.id.savedIdeaCategoryIconMain || viewId == R.id.savedIdeaCategoryIcon || viewId == R.id.savedIdeaCategoryIcon2 || viewId == R.id.savedIdeaCategoryIcon3 || viewId==R.id.savedIdeaCategoryIcon4){
            String ctg = cursor.getString(cursor.getColumnIndex(SavedIdeas.Idea.CATEGORY));
            if(ctg.equals(Category.PROJECT.toString())) {
                ((ImageView) view).setImageResource(R.drawable.ic_assignment_black_48dp);
            }
            if(ctg.equals(Category.HOBBY.toString())) {
                ((ImageView) view).setImageResource(R.drawable.ic_rowing_black_48dp);
            }
            if(ctg.equals(Category.TRAVEL.toString())) {
                ((ImageView) view).setImageResource(R.drawable.ic_flight_takeoff_black_48dp);
            }
            return true;
        }
        if(viewId == R.id.savedIdeaCompletedIconMain || viewId == R.id.savedIdeaCompletedIcon || viewId == R.id.savedIdeaCompletedIcon2 || viewId == R.id.savedIdeaCompletedIcon3 || viewId ==R.id.savedIdeaCompletedIcon4){
            String completed = cursor.getString(cursor.getColumnIndex(SavedIdeas.Idea.ISCOMPLETED));
            if(completed.equals("0")){
                return false;
                //((ImageView) view).setImageResource(R.drawable.ic_highlight_off_black_48dp);
            }else{
                ((ImageView) view).setImageResource(R.drawable.ic_check_circle_black_48dp);
            }
            return true;

        }
        if(viewId == R.id.savedIdeaObscurityMain || viewId == R.id.savedIdeaObscurity || viewId == R.id.savedIdeaObscurity2 || viewId == R.id.savedIdeaObscurity3 ||viewId == R.id.savedIdeaObscurity4){

            String obsc = cursor.getString(cursor.getColumnIndex(SavedIdeas.Idea.OBSCURITY));
            TextView txtV = (TextView) view;
            txtV.setText(obsc);
            try {
                byte obscNumber = Byte.parseByte(obsc);
                if(obscNumber>=9){
                    txtV.setTextColor(view.getResources().getColor(R.color.obscMax));
                    return true;
                }
                if(obscNumber>=7){
                    txtV.setTextColor(view.getResources().getColor(R.color.obscHigh));
                    return true;
                }
                if(obscNumber>=4){
                    txtV.setTextColor(view.getResources().getColor(R.color.obscMid));
                    return true;
                }
                if(obscNumber>=2){
                    txtV.setTextColor(view.getResources().getColor(R.color.obscLow));
                    return true;
                }
                if(obscNumber>=0){
                    txtV.setTextColor(view.getResources().getColor(R.color.obscMin));
                    return true;
                }
            } catch (NumberFormatException e){
                Log.e(Shared.LogDebug, "In custom binder: parsing obscurity text to number threw exception. "+e.getMessage());
                return true;
            }
            return true;
        }
        if(viewId == R.id.savedIdeaCompletedTextMain || viewId == R.id.savedIdeaCompletedText || viewId == R.id.savedIdeaCompletedText2 || viewId == R.id.savedIdeaCompletedText3 || viewId==R.id.savedIdeaCompletedText4){
            String complDate = cursor.getString(cursor.getColumnIndex(SavedIdeas.Idea.DATETIME));
            TextView txtV = (TextView) view;


            if(complDate.equals("0")){
                txtV.setText(view.getResources().getString(R.string.savedIdeasNotRealizedMsg));
                return true;
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, MMM dd, yyyy ");
            String formatted = dateFormat.format(System.currentTimeMillis());
            String msg = view.getResources().getString(R.string.savedIdeasRealizedDatetime);
            msg = String.format(msg,formatted);
            txtV.setText(msg);
            return true;
        }

        return false;
    }
}
