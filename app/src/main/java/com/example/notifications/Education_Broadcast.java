package com.example.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Education_Broadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        System.out.println("Education notification");

        Intent activityChange = new Intent(context, Education_Activity.class);
        activityChange.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pend = PendingIntent.getActivity(context, 0, activityChange, PendingIntent.FLAG_IMMUTABLE);

        // specifies how the notification will look
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyConor")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Educational Resources")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pend)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("There are a number of documented health risks associated with excessive smartphone usage - Information about them can be accessed here."));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());

    }
}
