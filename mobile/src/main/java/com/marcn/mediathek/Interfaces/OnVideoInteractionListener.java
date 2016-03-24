package com.marcn.mediathek.Interfaces;

import android.app.ActivityOptions;
import android.view.View;

import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.base_objects.StationOld;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.base_objects.Series;

public interface OnVideoInteractionListener {
    void onLiveStreamClicked(LiveStreamM3U8 video, View view, int videoAction);
    void onVideoClicked(Episode episode, View view, int videoAction);
    void onSendungClicked(Series series, View thumbnail, View logo);
    void onChannelClicked(StationOld station, View view);

    void playVideoWithInternalPlayer(String url, ActivityOptions activityOptions);
    void playVideoExternal(String url, String title, int videoAction);

    void onMoreClicked(String assetId, int type);
    void onMoreClicked2(String channelTitle, String assetId, String widgetTitle);
}