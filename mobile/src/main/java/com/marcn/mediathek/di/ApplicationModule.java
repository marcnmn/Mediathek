package com.marcn.mediathek.di;

import android.content.Context;

import com.marcn.mediathek.views.bottom_bar.BottomItem;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

@Module
class ApplicationModule {
    private final Context mAppContext;

    ApplicationModule(Context context) {
        mAppContext = context.getApplicationContext();
    }

    @Provides
    @Singleton
    Context providesApplicationContext() {
        return mAppContext;
    }

    @Provides
    @Singleton
    Subject<BottomItem, BottomItem> providesBottomSubject() {
        return PublishSubject.create();
    }

}
