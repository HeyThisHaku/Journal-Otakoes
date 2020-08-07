package com.weaboo.jurnalotaku;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.weaboo.jurnalotaku.R;

public class NontifcationHelper {
    public static void displayNotif(Context context,String channelID,String title,String text){
        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.send_btn)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


        // Gets an instance of the NotificationManager service//
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
    }

}
