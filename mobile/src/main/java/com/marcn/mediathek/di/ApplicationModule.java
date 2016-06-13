package com.marcn.mediathek.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by marcneumann on 24.05.16.
 */
@Module
public class ApplicationModule {
    protected final Context mAppContext;

    public ApplicationModule(Context context) {
        mAppContext = context.getApplicationContext();
    }

    @Provides
    public Context providesApplicationContext() {
        return mAppContext;
    }
}
