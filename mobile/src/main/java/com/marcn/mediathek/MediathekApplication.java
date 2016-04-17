package com.marcn.mediathek;

import android.app.Application;
import android.location.LocationManager;

import com.marcn.mediathek.pages.BaseActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by marcneumann on 16.04.16.
 */
public class MediathekApplication extends Application {

    @Singleton
    @Component(modules = AndroidModule.class)
    public interface ApplicationComponent {
        void inject(MediathekApplication application);
        void inject(BaseActivity homeActivity);
//        void inject(HomeActivity homeActivity);
//        void inject(DemoActivity demoActivity);
    }

    @Inject
    LocationManager locationManager; // for some reason.
    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
//        component = DaggerDemoApplication_ApplicationComponent
//        component = DaggerMedia.builder()
//                .androidModule(new AndroidModule(this))
//                .build();
//        component().inject(this);
    }

    public ApplicationComponent component() {
        return component;
    }
}
