package com.example.notifications;

import android.graphics.drawable.Drawable;

public class AppUsageInfo {
    Drawable appIcon;
    String appName, packageName;
    long timeInForeground;
    int launchCount;

    AppUsageInfo(String pName) {
        this.packageName=pName;
    }

    public String getAppName() {
        return appName;
    }
}