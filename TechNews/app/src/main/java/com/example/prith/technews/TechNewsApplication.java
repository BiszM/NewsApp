package com.example.prith.technews;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by prith on 2/21/2018.
 */

public class TechNewsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
