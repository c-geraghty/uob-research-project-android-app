package com.example.notifications;

import static android.content.Context.USAGE_STATS_SERVICE;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class GetDailyUsage {

    private final int usage;
    private String day;

    public GetDailyUsage(Context context) {

        long phoneUsageToday = 0;

        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);

        // find start of the day time
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(Calendar.HOUR_OF_DAY, 0);
        beginCal.set(Calendar.MINUTE, 0);
        beginCal.set(Calendar.SECOND, 0);

        Calendar currentTime = Calendar.getInstance();

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

        usage = ((int)phoneUsageToday / (1000*60));


    }

    public int getUsage() {
        return usage;
    }

    public String getDay() {
        return day;
    }
}
