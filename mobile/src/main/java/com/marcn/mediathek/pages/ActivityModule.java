package com.marcn.mediathek.pages;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

@ActivityScope
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
