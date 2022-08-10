package com.example.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


// broadcast reciever is used to send notifications even if app is closed
// needs to be specified in android manifest
public class HealthWarning_Broadcast extends BroadcastReceiver {

    // is the code that is run when the notification is pushed
    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        NotificationSetter ns = new NotificationSetter();
        ns.setHealthAlarmFromNoti(alarmManager, context);

            String output = "Consider one of these outdoor activities - HI SOUTHGATE :)";

            System.out.println("HEALTH WARNING");

        Intent activityChange = new Intent(context, MainActivity.class);
        activityChange.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pend = PendingIntent.getActivity(context, 0, activityChange, PendingIntent.FLAG_IMMUTABLE);

            // specifies how the notification will look
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyConor")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("It may be time to put down your phone!")
                    .setContentText(output)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pend)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.notify(200, builder.build());
        }
    }





