package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private Context mContext;
    private NotificationManager mManager;
    private static final int NOTIFICATION_ID = 0;

    private static final String channelId = "notification_channel";

    public NotificationHandler(Context context){
        this.mContext = context;
        this.mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }

        NotificationChannel channel = new NotificationChannel(channelId, "Job Notification", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.GREEN);
        channel.setDescription("Notification from JobCenter");
        this.mManager.createNotificationChannel(channel);
    }

    public void send(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                .setContentTitle("JobCenter")
                .setContentText(message)
                .setSmallIcon(com.google.android.material.R.drawable.notification_icon_background);


        this.mManager.notify(NOTIFICATION_ID, builder.build());

    }
}
