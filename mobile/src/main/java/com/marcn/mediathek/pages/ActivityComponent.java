package com.marcn.mediathek.pages;

import com.marcn.mediathek.di.ApplicationComponent;
import com.marcn.mediathek.pages.home.MainActivity;
import com.marcn.mediathek.pages.live.LiveActivity;
import com.marcn.mediathek.pages.missed.MissedActivity;
import com.marcn.mediathek.pages.station_page.StationActivity;
import com.marcn.mediathek.player.PlayerFragment;
import com.marcn.mediathek.ui_fragments.LiveStreamsFragment;
import com.marcn.mediathek.pages.missed.ZdfMissedVideoFragment;

import dagger.Component;

@ActivityScope
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {ActivityModule.class}
)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

    void inject(ZdfMissedVideoFragment zdfMissedVideoFragment);

    void inject(LiveStreamsFragment liveStreamsFragment);

    void inject(StationActivity stationActivity);

    void inject(LiveActivity liveActivity);

    void inject(PlayerFragment playerFragment);

    void inject(MissedActivity missedActivity);

    void inject(BaseActivity baseActivity);
}
