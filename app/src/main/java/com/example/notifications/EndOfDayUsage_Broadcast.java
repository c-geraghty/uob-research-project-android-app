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
public class EndOfDayUsage_Broadcast extends BroadcastReceiver {

    // is the code that is run when the notification is pushed
    @Override
    public void onReceive(Context context, Intent intent) {

        //
        String output;

        // determine usage so far that day
        GetDailyUsage gdu = new GetDailyUsage(context);
        int current = gdu.getUsage();

        // create database object
        // get history of prior usage from database
        DBHelper dbHelper = new DBHelper(context);
        List<UsageDBSchema> usageEvents = dbHelper.getAllUsage();

        // if no prior usage to compare ... DO SOMETHING
        if(usageEvents.size() == 0){

            System.out.println("No usage events");

        }
        // comapre with prior usage
        else {

            // usage from the day before is the last usage stored in DB
            int previous = usageEvents.get(usageEvents.size() - 1).getUsageInMillis();

            // if current usage is greater
            // then usage has increased from previous day
            if (current > previous) {

                System.out.println("Current usage: " + current);
                System.out.println("Yesterdays usage: " + previous);
                System.out.println("You're using your phone more than yesterday!");

                // set notification text to indicate increased usage
                output = "You're using your phone more than yesterday!";

            } else {

                System.out.println("Current usage: " + current);
                System.out.println("Yesterdays usage: " + previous);
                System.out.println("Your usage is down from yesterday, well done!");

                // set notification text to indicate decreased usage
                output = "Your usage is down from yesterday, well done!";

            }


            Intent activityChange = new Intent(context, HealthAdviceActivity.class);
            activityChange.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pend = PendingIntent.getActivity(context, 0, activityChange, PendingIntent.FLAG_IMMUTABLE);

            // specifies how the notification will look
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyConor")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Phone usage compared to yesterday:")
                    .setContentText(output)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pend)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            notificationManager.notify(200, builder.build());
        }
    }




}
