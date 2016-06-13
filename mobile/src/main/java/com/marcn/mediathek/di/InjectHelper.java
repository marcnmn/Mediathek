package com.marcn.mediathek.di;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.marcn.mediathek.MediathekApplication;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.pages.ActivityModule;
import com.marcn.mediathek.pages.DaggerActivityComponent;

public class InjectHelper {
    MediathekApplication mApplication;

    public InjectHelper(MediathekApplication application) {
        mApplication = application;
    }

    public ApplicationComponent setupApplication() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(mApplication))
                .build();
    }

    public static void setupPage(Injector injector) {
        if (injector instanceof Activity) {
            ActivityComponent component = setupActivity(((Activity) injector));
            injector.injectWith(component);
        } else if (injector instanceof Fragment) {
            ActivityComponent component = setupActivity(((Fragment) injector).getActivity());
            injector.injectWith(component);
        }
    }

    private static ActivityComponent setupActivity(Activity activity) {
        MediathekApplication app = (MediathekApplication) activity.getApplication();
        return DaggerActivityComponent.builder()
                .applicationComponent(app.getApplicationComponent())
                .activityModule(new ActivityModule(activity))
                .build();
    }
}
