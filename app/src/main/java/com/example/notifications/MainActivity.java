package com.example.notifications;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    List<AppUsageInfo> smallInfoList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        Button button = findViewById(R.id.button);
        TextView tv = findViewById(R.id.textView3);



        if (!isAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }


        // GETS DAILY USAGE STATS
        GetDailyUsage gdu = new GetDailyUsage(this);
        System.out.println(gdu.getUsage());

        String outputStr = "Today's usage: \n" + gdu.getUsage() + " mins";
        tv.setText(outputStr);

        // GET PREVIOUS USAGE
        DBHelper dbHelper = new DBHelper(MainActivity.this);


        List<UsageDBSchema> usageEvents = dbHelper.getAllUsage();

        if(usageEvents.size() != 0) {
            System.out.println(usageEvents.get(usageEvents.size() - 1).getUsageInMillis());
        }

        // CALLING NOTIFICATION ALARM

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Calendar calendar2 = Calendar.getInstance();

        if (calendar2.getTime().compareTo(new Date()) < 0)
            calendar2.add(Calendar.DAY_OF_MONTH, 1);

        // CODE FOR UPDATING DATABASE EVERY 15 MINUTES

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        Intent EOD_usage_intent = new Intent(MainActivity.this, EndOfDayUsage_Broadcast.class);
        Intent avg_usage_intent = new Intent(MainActivity.this, AverageUsage_Broadcast.class);
        Intent DB_write_intent = new Intent(MainActivity.this, DB_Broadcast.class);
        Intent health_intent = new Intent(MainActivity.this, HealthWarning_Broadcast.class);
        Intent edu_intent = new Intent(MainActivity.this, Education_Broadcast.class);


        // PENDING INTENTS
        PendingIntent EOD_pend = PendingIntent.getBroadcast(MainActivity.this, 0, EOD_usage_intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent DBwrite_pend = PendingIntent.getBroadcast(MainActivity.this, 1, DB_write_intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent avg_pend = PendingIntent.getBroadcast(MainActivity.this, 2, avg_usage_intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager3 = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent health_pend = PendingIntent.getBroadcast(MainActivity.this, 3, health_intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager4 = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent edu_pend = PendingIntent.getBroadcast(MainActivity.this, 4, edu_intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager5 = (AlarmManager) getSystemService(ALARM_SERVICE);

        // ALARM MANAGER SCHEDULING
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                EOD_pend);

        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                DBwrite_pend);

        alarmManager3.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                avg_pend);

        alarmManager4.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                health_pend);

        alarmManager5.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar2.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES/30,
                edu_pend);


        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GetDailyUsage gd = new GetDailyUsage(MainActivity.this);
                                String outputStr = "Todays Usage: \n" + String.valueOf(gd.getUsage()) + " mins";
                                tv.setText(outputStr);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();




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

        if (current > average){

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