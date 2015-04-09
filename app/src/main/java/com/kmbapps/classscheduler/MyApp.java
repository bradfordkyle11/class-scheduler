package com.kmbapps.classscheduler;

import android.app.Application;
import android.content.Context;

/**
 * Created by Kyle on 4/8/2015.
 */
public class MyApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
