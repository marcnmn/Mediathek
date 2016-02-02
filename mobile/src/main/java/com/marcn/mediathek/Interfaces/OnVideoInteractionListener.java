package com.marcn.mediathek.Interfaces;

import android.app.ActivityOptions;
import android.view.View;

import com.marcn.mediathek.base_objects.Channel;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Sendung;
import com.marcn.mediathek.base_objects.Video;

public interface OnVideoInteractionListener {
    void onLiveStreamClicked(LiveStream liveStream, View view, int videoAction);
    void onVideoClicked(Video video, View view, int videoAction);
    void onSendungClicked(Sendung sendung, View thumbnail, View logo);
    void onChannelClicked(Channel channel, View view);

    void playVideoWithInternalPlayer(String url, ActivityOptions activityOptions);
    void playVideoExternal(String url, String title, int videoAction);

    void onMoreClicked(String assetId, int type);
}