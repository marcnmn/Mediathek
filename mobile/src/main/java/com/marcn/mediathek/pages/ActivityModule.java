package com.marcn.mediathek.pages;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by marcneumann on 24.05.16.
 */
@Module
public class ActivityModule {
    protected final Activity mContext;

    public ActivityModule(Activity context) {
        mContext = context;
    }

    @Provides
    Activity providesActivityContext() {
        return mContext;
    }
}
