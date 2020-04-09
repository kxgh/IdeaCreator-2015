package com.example.kristian.ideacreator;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Kristian on 20/05/2016.
 */
public class UpdateNotificationService extends IntentService {

    String title;
    String desc;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateNotificationService() {
        super("UpdateNotificationService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent!=null){
            // http://stackoverflow.com/questions/30150191/android-notification-not-shown
            Bundle extras = intent.getExtras();
            if(extras!=null){
                if(extras.getInt("count")!=0){
                    int count = extras.getInt("count");

                    String txt = getResources().getString(R.string.notifPackInstalled);
                    txt = String.format(txt, count);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new NotificationCompat.Builder(this).setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText(txt).setSmallIcon(android.R.drawable.btn_star_big_off).build();
                    notificationManager.notify(0,notification);

                    return;
                }
            }


            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this).setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.notifDbUpdateCompleted)).setSmallIcon(android.R.drawable.ic_input_add).build();
            notificationManager.notify(0,notification);

        } else Log.e(Shared.LogNotif, "Intent null in update notification");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
