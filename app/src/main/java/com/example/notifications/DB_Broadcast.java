package com.example.notifications;

import static android.content.Context.USAGE_STATS_SERVICE;

import android.app.AlarmManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DB_Broadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        NotificationSetter ns = new NotificationSetter();
        ns.setDBWriteAlarmFromNoti(alarmManager, context);

        long phoneUsageToday = 0;

        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);

        // find start of the day time
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.HOUR_OF_DAY, 0);
        beginCal.set(Calendar.MINUTE, 0);
        beginCal.set(Calendar.SECOND, 0);

        Calendar currentTime = Calendar.getInstance();


        System.out.println("Current Time: " + currentTime.getTime());

        long currTime = System.currentTimeMillis();

        UsageEvents.Event currentEvent;

        List<UsageEvents.Event> allEvents = new ArrayList<>();

        HashMap<String, AppUsageInfo> map = new HashMap <String, AppUsageInfo> ();

        UsageStatsManager mUsageStatsManager =  (UsageStatsManager)
                context.getSystemService(USAGE_STATS_SERVICE);

        assert mUsageStatsManager != null;
        UsageEvents usageEvents = mUsageStatsManager.queryEvents(beginCal.getTimeInMillis(), currTime);

        while (usageEvents.hasNextEvent()) {

            currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);

            if (currentEvent.getEventType() == UsageEvents.Event.SCREEN_INTERACTIVE ||
                    currentEvent.getEventType() == UsageEvents.Event.SCREEN_NON_INTERACTIVE) {

                allEvents.add(currentEvent);
                String key = currentEvent.getPackageName();

                // taking it into a collection to access by package name
                if (map.get(key)==null)
                    map.put(key,new AppUsageInfo(key));
            }
        }

        // if statement for if there is only one event, screen ON
        if(allEvents.size() == 1){


            UsageEvents.Event E0 = allEvents.get(0);
            // if last event in list is an open
            // find the difference between timestamp of open and current time
            if (E0.getEventType() == 15) {

            long diff = System.currentTimeMillis() - E0.getTimeStamp();
            phoneUsageToday += diff; //gloabl Long var for total usagetime in the timerange
            map.get(E0.getPackageName()).timeInForeground += diff;

            }

        }
        else {
            //iterating through the arraylist
            for (int i = 0; i < allEvents.size() - 1; i++) {

                UsageEvents.Event E0 = allEvents.get(i);
                UsageEvents.Event E1 = allEvents.get(i + 1);

                //for UsageTime of apps in time range
                // if first event is open screen and second is close
                // find the difference between timestamp of close and open
                // returns time that screen was on before closing
                if (E0.getEventType() == 15 && (E1.getEventType() == 16 || E1.getEventType() == 26)
                ) {


                    long diff = E1.getTimeStamp() - E0.getTimeStamp();
                    phoneUsageToday += diff; //gloabl Long var for total usagetime in the timerange
                    map.get(E0.getPackageName()).timeInForeground += diff;
                }
                // if last event in list is an open
                // find the difference between timestamp of open and current time
                else if (E1.getEventType() == 15 && ((i + 1) == allEvents.size() - 1)) {

                    long diff = System.currentTimeMillis() - E1.getTimeStamp();
                    phoneUsageToday += diff; //gloabl Long var for total usagetime in the timerange
                    map.get(E0.getPackageName()).timeInForeground += diff;

                }


            }
        }
        //transferred final data into modal class object
        //smallInfoList = new ArrayList<>(map.values());


        System.out.println("Phone usage today: " + phoneUsageToday/(1000*60));


        // writing usage to database
        UsageDBSchema dbModel;

        SimpleDateFormat sdf = new SimpleDateFormat("dd - MM - yyyy");
        Date date = new Date(System.currentTimeMillis());
        String time = sdf.format(date);

        try {

            dbModel = new UsageDBSchema(-1, time,  Math.toIntExact(phoneUsageToday/(1000*60)));
            Toast.makeText(context, "Adding usage: " + Math.toIntExact(phoneUsageToday/(1000*60)), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            dbModel = new UsageDBSchema(-1, "ERROR", 0);
            Toast.makeText(context, "ERROR ADDING TO DB", Toast.LENGTH_SHORT).show();

        }

        DBHelper dbHelper = new DBHelper(context);
        List<UsageDBSchema> priorUsage = dbHelper.getAllUsage();

        // if there is prior usage
        if(priorUsage.size() != 0){

            String previousDate = priorUsage.get(priorUsage.size() - 1).getDate();

            if(previousDate.equals(time)){

                dbHelper.update(dbModel, time);


            }else{

                dbHelper.addOne(dbModel);

            }
            // if no prior usage then nothing to compare with so just add
        }else{

            dbHelper.addOne(dbModel);

        }

        return;



    }
}
