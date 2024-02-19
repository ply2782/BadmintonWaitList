package com.jc.jcsports;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MainApplication extends Application {

    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
}
