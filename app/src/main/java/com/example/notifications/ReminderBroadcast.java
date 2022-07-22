package com.example.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.*;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


// broadcast reciever is used to send notifications even if app is closed
// needs to be specified in android manifest
public class ReminderBroadcast extends BroadcastReceiver {

    // is the code that is run when the notification is pushed
    @Override
    public void onReceive(Context context, Intent intent) {

        String reminder1 = "Hey this is a reminder";
        String reminder2 = "Hey this is a second reminder";

        Random rand = new Random();
        int choice = rand.nextInt(2);
        CharSequence output;

        System.out.println(choice);

        if(choice == 1){

            output = reminder1;

        }
        else{

            output = reminder2;

        }


        Intent activityChange = new Intent(context, DestinationActivity.class);
        activityChange.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pend = PendingIntent.getActivity(context, 0, activityChange, PendingIntent.FLAG_IMMUTABLE);

        // specifies how the notification will look
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyConor")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reminder")
                .setContentText(output)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pend)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }
}
