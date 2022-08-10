package com.example.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import java.util.Calendar;
import java.util.Date;

public class NotificationSetter {

    public void setEduAlarm(AlarmManager alarmManager, Context context){



        Calendar eduWriteCal = Calendar.getInstance();
        eduWriteCal.set(Calendar.HOUR_OF_DAY, 9);
        eduWriteCal.set(Calendar.MINUTE, 30);
        eduWriteCal.set(Calendar.SECOND, 0);

        Intent edu_intent = new Intent(context, Education_Broadcast.class);
        PendingIntent edu_pend = PendingIntent.getBroadcast(context, 3, edu_intent, PendingIntent.FLAG_IMMUTABLE);

        if ((new Date().getTime() - eduWriteCal.getTimeInMillis()) >= 1800000) {

            eduWriteCal.add(Calendar.DAY_OF_WEEK,1);
            genericSet(eduWriteCal, edu_pend, alarmManager, "Health");


        } else if(0 <= (new Date().getTime() - eduWriteCal.getTimeInMillis()) && (new Date().getTime() - eduWriteCal.getTimeInMillis()) < 1800000){

            // do nothing

        }
        else{

            genericSet(eduWriteCal, edu_pend, alarmManager, "Education ");

        }

    }

    public void setEduAlarmFromNoti(AlarmManager alarmManager, Context context){

        Calendar eduWriteCal = Calendar.getInstance();
        eduWriteCal.set(Calendar.HOUR_OF_DAY, 9);
        eduWriteCal.set(Calendar.MINUTE, 30);
        eduWriteCal.set(Calendar.SECOND, 0);

        Intent edu_intent = new Intent(context, Education_Broadcast.class);
        PendingIntent edu_pend = PendingIntent.getBroadcast(context, 3, edu_intent, PendingIntent.FLAG_IMMUTABLE);

        if ((new Date().getTime() - eduWriteCal.getTimeInMillis()) >= 0) {

            eduWriteCal.add(Calendar.DAY_OF_WEEK,1);
            genericSet(eduWriteCal, edu_pend, alarmManager, "Education Noti");


        }

    }


    public void setHealthAlarm(AlarmManager alarmManager, Context context){



        Calendar healthWriteCal = Calendar.getInstance();
        healthWriteCal.set(Calendar.HOUR_OF_DAY, 17);
        healthWriteCal.set(Calendar.MINUTE, 30);
        healthWriteCal.set(Calendar.SECOND, 0);

        Intent health_intent = new Intent(context, HealthWarning_Broadcast.class);
        PendingIntent health_pend = PendingIntent.getBroadcast(context, 3, health_intent, PendingIntent.FLAG_IMMUTABLE);

        if ((new Date().getTime() - healthWriteCal.getTimeInMillis()) >= 1800000) {

            healthWriteCal.add(Calendar.DAY_OF_WEEK,1);
            genericSet(healthWriteCal, health_pend, alarmManager, "Health");


        } else if(0 <= (new Date().getTime() - healthWriteCal.getTimeInMillis()) && (new Date().getTime() - healthWriteCal.getTimeInMillis()) < 1800000){

            // do nothing

        }
        else{

            genericSet(healthWriteCal, health_pend, alarmManager, "Health");

        }

    }

    public void setHealthAlarmFromNoti(AlarmManager alarmManager, Context context){

        Calendar healthWriteCal = Calendar.getInstance();
        healthWriteCal.set(Calendar.HOUR_OF_DAY, 17);
        healthWriteCal.set(Calendar.MINUTE, 30);
        healthWriteCal.set(Calendar.SECOND, 0);

        Intent health_intent = new Intent(context, HealthWarning_Broadcast.class);
        PendingIntent health_pend = PendingIntent.getBroadcast(context, 3, health_intent, PendingIntent.FLAG_IMMUTABLE);

        if ((new Date().getTime() - healthWriteCal.getTimeInMillis()) >= 0) {

            healthWriteCal.add(Calendar.DAY_OF_WEEK,1);
            genericSet(healthWriteCal, health_pend, alarmManager, "Health Noti");


        }

    }

    public void setEODAlarm(AlarmManager alarmManager, Context context){

        // CALLING NOTIFICATION ALARM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE,30);
        calendar.set(Calendar.SECOND, 0);

        Intent EOD_usage_intent = new Intent(context, EndOfDayUsage_Broadcast.class);
        PendingIntent EOD_pend = PendingIntent.getBroadcast(context, 0, EOD_usage_intent, PendingIntent.FLAG_IMMUTABLE);

        // if current time is 30 mins or more ahead of scheduled alarm time
        // set alarm for the future
        if ((new Date().getTime() - calendar.getTimeInMillis()) >= 1800000) {

            calendar.add(Calendar.DAY_OF_WEEK, 1);

            // PENDING INTENTS

            // ALARM MANAGER SCHEDULING
            genericSet(calendar, EOD_pend, alarmManager, "EOD");



        } else if(0 <= (new Date().getTime() - calendar.getTimeInMillis()) && (new Date().getTime() - calendar.getTimeInMillis()) < 1800000){

            //wait for alarm to go off

        }
        else{

            // ALARM MANAGER SCHEDULING
            genericSet(calendar, EOD_pend, alarmManager, "EOD");

        }


    }

    public void setEODAlarmFromNoti(AlarmManager alarmManager, Context context){

        // CALLING NOTIFICATION ALARM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE,30);
        calendar.set(Calendar.SECOND, 0);

        Intent EOD_usage_intent = new Intent(context, EndOfDayUsage_Broadcast.class);
        PendingIntent EOD_pend = PendingIntent.getBroadcast(context, 0, EOD_usage_intent, PendingIntent.FLAG_IMMUTABLE);

        // if current time is 30 mins or more ahead of scheduled alarm time
        // set alarm for the future
        if ((new Date().getTime() - calendar.getTimeInMillis()) >= 0) {

            // ALARM MANAGER SCHEDULING
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            genericSet(calendar, EOD_pend, alarmManager, "EOD Noti");

        }




    }





    public void setDBWriteAlarm(AlarmManager alarmManager, Context context){

        Calendar dbWriteCal = Calendar.getInstance();

        dbWriteCal.set(Calendar.HOUR_OF_DAY, 0);
        dbWriteCal.set(Calendar.MINUTE, 0);
        dbWriteCal.set(Calendar.SECOND, 0);

        Intent DB_write_intent = new Intent(context, DB_Broadcast.class);
        PendingIntent DBwrite_pend = PendingIntent.getBroadcast(context, 1, DB_write_intent, PendingIntent.FLAG_IMMUTABLE);

        if ((new Date().getTime() - dbWriteCal.getTimeInMillis()) >= 1750000) {

            int dbWriteTime = dbWriteCal.get(Calendar.MINUTE);

            while(dbWriteCal.getTime().compareTo(new Date()) < 0){

                dbWriteTime = dbWriteTime + 15;

                if(dbWriteTime >= 60){

                    dbWriteCal.add(Calendar.HOUR_OF_DAY,1);
                    dbWriteTime = 0;
                    dbWriteCal.set(Calendar.MINUTE, dbWriteTime);

                }else{

                    if(!(dbWriteCal.MINUTE == 0 && (dbWriteCal.HOUR_OF_DAY == 12 || dbWriteCal.HOUR_OF_DAY == 17 || dbWriteCal.HOUR_OF_DAY == 21 ))) {

                        dbWriteCal.set(Calendar.MINUTE, dbWriteTime);

                    }
                    else{

                        System.out.println("Other alarms scheduled");

                    }

                }
            }

            genericSet(dbWriteCal, DBwrite_pend, alarmManager, "DB");


        } else if(0 <= (new Date().getTime() - dbWriteCal.getTimeInMillis()) && (new Date().getTime() - dbWriteCal.getTimeInMillis()) < 1800000){

            //wait for alarm to go off

        }
        else{

            // ALARM MANAGER SCHEDULING
            genericSet(dbWriteCal, DBwrite_pend, alarmManager, "DB");

        }

    }

    public void setDBWriteAlarmFromNoti(AlarmManager alarmManager, Context context){

        Calendar dbWriteCal = Calendar.getInstance();

        dbWriteCal.set(Calendar.HOUR_OF_DAY, 0);
        dbWriteCal.set(Calendar.MINUTE, 0);
        dbWriteCal.set(Calendar.SECOND, 0);

        Intent DB_write_intent = new Intent(context, DB_Broadcast.class);
        PendingIntent DBwrite_pend = PendingIntent.getBroadcast(context, 1, DB_write_intent, PendingIntent.FLAG_IMMUTABLE);

        if ((new Date().getTime() - dbWriteCal.getTimeInMillis()) >= 0) {

            int dbWriteTime = dbWriteCal.get(Calendar.MINUTE);

            while(dbWriteCal.getTime().compareTo(new Date()) < 0){

                dbWriteTime = dbWriteTime + 15;

                if(dbWriteTime >= 60){

                    dbWriteCal.add(Calendar.HOUR_OF_DAY,1);
                    dbWriteTime = 0;
                    dbWriteCal.set(Calendar.MINUTE, dbWriteTime);

                }else{

                    dbWriteCal.set(Calendar.MINUTE, dbWriteTime);

                }
            }

            genericSet(dbWriteCal, DBwrite_pend, alarmManager, "DB Noti");

        }


    }

    private void genericSet(Calendar calendar, PendingIntent pend, AlarmManager alarmManager, String name){

        // ALARM MANAGER SCHEDULING
        System.out.println(name + " alarm set for: " + calendar.getTime());
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pend);

    }

}
