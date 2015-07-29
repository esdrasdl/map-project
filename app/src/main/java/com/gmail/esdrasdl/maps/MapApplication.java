package com.gmail.esdrasdl.maps;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by esdras on 29/07/15.
 */
public class MapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
