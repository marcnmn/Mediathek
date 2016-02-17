package com.marcn.mediathek.Interfaces;

import android.app.ActivityOptions;
import android.view.View;

import com.marcn.mediathek.base_objects.Station;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Series;

public interface OnVideoInteractionListener {
    void onLiveStreamClicked(LiveStream liveStream, View view, int videoAction);
    void onVideoClicked(Episode episode, View view, int videoAction);
    void onSendungClicked(Series series, View thumbnail, View logo);
    void onChannelClicked(Station station, View view);

    void playVideoWithInternalPlayer(String url, ActivityOptions activityOptions);
    void playVideoExternal(String url, String title, int videoAction);

    void onMoreClicked(String assetId, int type);
}