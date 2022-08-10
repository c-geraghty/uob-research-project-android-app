package com.example.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        NotificationSetter ns = new NotificationSetter();
        ns.setHealthAlarm(alarmManager, context);
        ns.setDBWriteAlarm(alarmManager, context);
        ns.setEODAlarm(alarmManager, context);
        ns.setEduAlarm(alarmManager, context);

    }

}
