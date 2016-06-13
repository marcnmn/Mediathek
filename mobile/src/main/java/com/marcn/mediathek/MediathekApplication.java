package com.marcn.mediathek;

import android.app.Application;

import com.marcn.mediathek.di.ApplicationComponent;
import com.marcn.mediathek.di.InjectHelper;

public class MediathekApplication extends Application {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = new InjectHelper(this).setupApplication();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}
