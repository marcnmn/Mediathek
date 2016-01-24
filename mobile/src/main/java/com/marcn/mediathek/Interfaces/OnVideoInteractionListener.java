package com.marcn.mediathek.Interfaces;

import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.base_objects.Channel;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Sendung;
import com.marcn.mediathek.base_objects.Video;

public interface OnVideoInteractionListener {
    void onLiveStreamClicked(LiveStream liveStream, View view, int videoAction);
    void onVideoClicked(Video video, View view, int videoAction);
    void onSendungClicked(Sendung sendung, View view);
    void onChannelClicked(Channel channel, View view);
}