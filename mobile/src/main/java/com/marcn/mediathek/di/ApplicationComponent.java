package com.marcn.mediathek.di;

import android.content.Context;

import com.marcn.mediathek.network.ApiModule;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.views.bottom_bar.BottomBarManager;
import com.marcn.mediathek.views.bottom_bar.BottomItem;

import javax.inject.Singleton;

import dagger.Component;
import rx.subjects.Subject;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        ApiModule.class})
public interface ApplicationComponent {

    Context provideContext();

    Subject<BottomItem, BottomItem> providesBottomSubject();

    BottomBarManager providesBottomBarManager();

    ZdfInteractor provideZdfInteractor();

    ArdInteractor provideArdInteractor();

}
