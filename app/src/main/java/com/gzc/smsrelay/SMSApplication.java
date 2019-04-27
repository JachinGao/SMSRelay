package com.gzc.smsrelay;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class SMSApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        SMSApplication.context = getApplicationContext();
    }

    public static Context getAppContext(){
        return SMSApplication.context;
    }
}
