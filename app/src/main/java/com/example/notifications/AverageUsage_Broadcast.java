package com.example.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;


// broadcast reciever is used to send notifications even if app is closed
// needs to be specified in android manifest
public class AverageUsage_Broadcast extends BroadcastReceiver {

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
            int runningTotal = 0;

            for(int i = 0; i < usageEvents.size(); i++){


                runningTotal += usageEvents.get(i).getUsageInMillis();


            }

            int average = runningTotal / usageEvents.size();


            // if current usage is greater
            // then usage has increased from previous day
            if (current > average) {

                System.out.println("Current usage: " + current);
                System.out.println("Average daily usage: " + average);
                System.out.println("You're using your phone more than average!");

                // set notification text to indicate increased usage
                output = "You're using your phone more than average!";

            } else {

                System.out.println("Current usage: " + current);
                System.out.println("Average daily usage: " + average);
                System.out.println("Your usage is down from average, well done!");

                // set notification text to indicate decreased usage
                output = "Your usage is down from average, well done!";

            }


            Intent activityChange = new Intent(context, HealthAdviceActivity.class);
            activityChange.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pend = PendingIntent.getActivity(context, 0, activityChange, PendingIntent.FLAG_IMMUTABLE);

            // specifies how the notification will look
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyConor")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Phone usage compared to average:")
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
