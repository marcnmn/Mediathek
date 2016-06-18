package com.marcn.mediathek.di;

import android.content.Context;

import com.marcn.mediathek.network.ApiModule;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.BaseActivity;

import dagger.Component;

/**
 * Created by marcneumann on 24.05.16.
 */
@Component(modules = {
        ApplicationModule.class,
        ApiModule.class})
public interface ApplicationComponent {

    Context provideContext();

    ZdfInteractor provideZdfInteractor();

    ArdInteractor provideArdInteractor();

}
