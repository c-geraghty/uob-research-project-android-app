package com.example.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    List<AppUsageInfo> smallInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        Button button = findViewById(R.id.button);

        if (!isAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }



        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 20);
        calendar.set(Calendar.SECOND, 0);



        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 11);
        calendar2.set(Calendar.MINUTE, 25);
        calendar2.set(Calendar.SECOND, 0);

        if (calendar2.getTime().compareTo(new Date()) < 0)
            calendar2.add(Calendar.DAY_OF_MONTH, 1);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        System.out.println("TEST");






        Intent intent2 = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(MainActivity.this, 1, intent2, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent2);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long phoneUsageToday = 0;
                long totalTime = 0;



                Toast.makeText(MainActivity.this, "Reminder set!", Toast.LENGTH_SHORT).show();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 10);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.getTime().compareTo(new Date()) < 0)
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


                final UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
                Calendar beginCal = Calendar.getInstance();
                beginCal.set(Calendar.HOUR_OF_DAY, 0);
                beginCal.set(Calendar.MINUTE, 0);
                beginCal.set(Calendar.SECOND, 0);

                Calendar endCal = Calendar.getInstance();


                long currTime = System.currentTimeMillis();
                long startTime = currTime - 1000*3600*3; //querying past three hours

                UsageEvents.Event currentEvent;
                List<UsageEvents.Event> allEvents = new ArrayList<>();
                HashMap<String, AppUsageInfo> map = new HashMap <String, AppUsageInfo> ();

                UsageStatsManager mUsageStatsManager =  (UsageStatsManager)
                        getSystemService(USAGE_STATS_SERVICE);

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

                //iterating through the arraylist 
                for (int i=0;i<allEvents.size()-1;i++){



                    UsageEvents.Event E0=allEvents.get(i);
                    UsageEvents.Event E1=allEvents.get(i+1);




//for UsageTime of apps in time range
                    if (E0.getEventType()==15 && E1.getEventType()==16
                    ){

                        long diff = E1.getTimeStamp()-E0.getTimeStamp();
                        phoneUsageToday+=diff; //gloabl Long var for total usagetime in the timerange
                        map.get(E0.getPackageName()).timeInForeground+= diff;
                    }
                    else if(E1.getEventType()==15 && ((i+1) == allEvents.size()-1)){

                        long diff = System.currentTimeMillis()-E1.getTimeStamp();
                        phoneUsageToday+=diff; //gloabl Long var for total usagetime in the timerange
                        map.get(E0.getPackageName()).timeInForeground+= diff;

                    }

                }
//transferred final data into modal class object
                smallInfoList = new ArrayList<>(map.values());


                System.out.println("Phone usage today: " + phoneUsageToday/(1000*60));


                UsageDBSchema dbModel;

                try {

                    dbModel = new UsageDBSchema(-1, "21/07/2022", Math.toIntExact(phoneUsageToday/(1000*60)));
                    Toast.makeText(MainActivity.this, "Adding usage: " + Math.toIntExact(phoneUsageToday/(1000*60)), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                    dbModel = new UsageDBSchema(-1, "ERROR", 0);
                    Toast.makeText(MainActivity.this, "ERROR ADDING TO DB", Toast.LENGTH_SHORT).show();

                }

                DBHelper dbHelper = new DBHelper(MainActivity.this);

                boolean success = dbHelper.addOne(dbModel);


                /*final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginCal.getTimeInMillis(), endCal.getTimeInMillis());

                System.out.println("results for " + beginCal.getTime() + " - " + endCal.getTime());

                for (UsageStats app : queryUsageStats) {

                    totalTime = totalTime + app.getTotalTimeInForeground();

                }

                System.out.println("Total time: " + totalTime/(1000));


                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent);*/



            }
        });
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(this.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }

    private void createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           CharSequence name =  "ReminderChannel";
           String description = "Channel for Reminder";
           int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyConor",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);



        }

    }



}