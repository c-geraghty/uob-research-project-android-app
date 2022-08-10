package com.example.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.*;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


// broadcast reciever is used to send notifications even if app is closed
// needs to be specified in android manifest
public class EndOfDayUsage_Broadcast extends BroadcastReceiver {

    // is the code that is run when the notification is pushed

    @Override
    public void onReceive(Context context, Intent intent) {


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        NotificationSetter ns = new NotificationSetter();
        ns.setEODAlarmFromNoti(alarmManager, context);

        String output;

        // determine usage so far that day
        GetDailyUsage gdu = new GetDailyUsage(context);
        int current = gdu.getUsage();

        // create database object
        // get history of prior usage from database
        DBHelper dbHelper = new DBHelper(context);
        List<UsageDBSchema> usageEvents = dbHelper.getAllUsage();

        //
        int minsUsage = gdu.getUsage();
        int hourUsage = 0;

        if (minsUsage < 60){

            output = minsUsage + " mins";


        } else {

            hourUsage = 1;

            while(minsUsage - 60 > 59){

                minsUsage = minsUsage - 60;
                hourUsage = hourUsage+1;

            }


            if (hourUsage > 1){
                output = hourUsage + " hours, " + minsUsage % 60 + " mins";
            }
            else{
                output = hourUsage + " hour, " + minsUsage % 60 + " mins";
            }

        }

        String outputStr = "Todays Usage: " + output;



        // if no prior usage to compare ... DO SOMETHING
        if(usageEvents.size() == 1){

            output = "This is the first day of tracking screen-time. Comparisons will be available on further. ";

        }
        // comapre with prior usage
        else {

            // usage from the day before is the last usage stored in DB
            int previous = usageEvents.get(usageEvents.size() - 1).getUsageInMillis();

            // if current usage is greater
            // then usage has increased from previous day
            if (current > previous) {

                // set notification text to indicate increased usage
                output = "You're using your phone more than yesterday! " + previous;

            } else {

                // set notification text to indicate decreased usage
                output = "Your usage is down from yesterday, well done!";

            }
        }

        System.out.println("TEST");

        Intent activityChange = new Intent(context, MainActivity.class);
        activityChange.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pend = PendingIntent.getActivity(context, 0, activityChange, PendingIntent.FLAG_IMMUTABLE);

        // specifies how the notification will look
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyConor")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(outputStr)
                .setContentText(output)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pend)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());



        }
    }






