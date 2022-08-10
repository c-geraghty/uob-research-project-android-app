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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
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

        if (!isAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        System.out.println("LAdTEST!");

        TextView tv = findViewById(R.id.textView3);

        displayUsage();

    }

    protected void onStart(){

        super.onStart();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        NotificationSetter ns = new NotificationSetter();
        ns.setHealthAlarm(alarmManager, this);
        ns.setDBWriteAlarm(alarmManager, this);
        ns.setEODAlarm(alarmManager, this);
        ns.setEduAlarm(alarmManager, this);

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                displayUsage();

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

    }

    private void displayUsage() {


        GetDailyUsage gd = new GetDailyUsage(MainActivity.this);
        int minsUsage = gd.getUsage();
        int hourUsage = 0;
        String output = null;

        TextView tv = findViewById(R.id.textView3);

        if (minsUsage < 60){

            output = String.valueOf(minsUsage) + " mins";


        } else {

            hourUsage = 1;

            while(minsUsage - 60 > 59){

                minsUsage = minsUsage - 60;
                hourUsage = hourUsage+1;
            }

            if (hourUsage > 1){
                output = String.valueOf(hourUsage) + " hours, " + String.valueOf(minsUsage % 60) + " mins";
            }
            else{
                output = String.valueOf(hourUsage) + " hour, " + String.valueOf(minsUsage % 60) + " mins";
            }

        }

        String outputStr = "Todays usage: \n" + output;
        tv.setText(outputStr);

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
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{ 0 });

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);


        }
    }

}