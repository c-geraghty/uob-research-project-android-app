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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    List<AppUsageInfo> smallInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        Button button = findViewById(R.id.button);
        Button clear = findViewById(R.id.clear);

        if (!isAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }


        // GETS DAILY USAGE STATS

        GetDailyUsage gdu = new GetDailyUsage(this);
        System.out.println(gdu.getUsage());

        // GET PREVIOUS USAGE

        DBHelper dbHelper = new DBHelper(MainActivity.this);


        // TODO fix the compare statement - to work when there's zero items in the list

        List<UsageDBSchema> usageEvents = dbHelper.getAllUsage();

        if(usageEvents.size() != 0) {
            System.out.println(usageEvents.get(usageEvents.size() - 1).getUsageInMillis());
        }

        // CALLING NOTIFICATION ALARM

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        Intent intenttest = new Intent(MainActivity.this, ReminderBroadcast.class);

        Intent intent2 = new Intent(MainActivity.this, DB_Broadcast.class);


        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        Intent output_intent;

        output_intent = intenttest;

        if (dayOfWeek.getValue() == 2){

            output_intent = intenttest;

        }





        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(MainActivity.this, 1, output_intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, output_intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES/15,
                pendingIntent);

        Calendar calendar2 = Calendar.getInstance();

        if (calendar2.getTime().compareTo(new Date()) < 0)
            calendar2.add(Calendar.DAY_OF_MONTH, 1);

        // CODE FOR UPDATING DATABASE EVERY 15 MINUTES

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);



        //
        //
        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES/15,
                pendingIntent2);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (usageEvents.size() == 0){
                    Toast.makeText(MainActivity.this, "NO PRIOR ACTIVITY TO COMPARE WITH", Toast.LENGTH_SHORT).show();
                }else {
                    compareAverage();
                }

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbHelper.clearDatabase();

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReminderChannel";
            String description = "Channel for Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyConor", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


        }
    }

    public void compareUsage() {

        GetDailyUsage gdu = new GetDailyUsage(MainActivity.this);
        int current = gdu.getUsage();

        DBHelper dbHelper = new DBHelper(MainActivity.this);
        List<UsageDBSchema> usageEvents = dbHelper.getAllUsage();

        int previous = usageEvents.get(usageEvents.size() - 1).getUsageInMillis();



        if (current > previous){

            System.out.println("Current usage: " + current);
            System.out.println("Yesterdays usage: " + previous);
            System.out.println("You're using your phone more than yesterday!");

        }
        else{

            System.out.println("Current usage: " + current);
            System.out.println("Yesterdays usage: " + previous);
            System.out.println("Your usage is down from yesterday, well done!");

        }

    }

    public void compareAverage(){

        GetDailyUsage gdu = new GetDailyUsage(MainActivity.this);
        int current = gdu.getUsage();

        DBHelper dbHelper = new DBHelper(MainActivity.this);
        List<UsageDBSchema> usageEvents = dbHelper.getAllUsage();

        int runningTotal = 0;

        for(int i = 0; i < usageEvents.size(); i++){


            runningTotal += usageEvents.get(i).getUsageInMillis();


        }

        int average = runningTotal / usageEvents.size();

        if (current > runningTotal){

            System.out.println("Current usage: " + current);
            System.out.println("Average usage: " + average);
            System.out.println("You're using your phone more than average!");

        }
        else{

            System.out.println("Current usage: " + current);
            System.out.println("Average usage: " + average);
            System.out.println("Your usage is less than your average, well done!");

        }


    }



}