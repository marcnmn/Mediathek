package com.marcn.mediathek.Interfaces;

import android.view.View;

import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Video;

public interface OnVideoInteractionListener {
    void onLiveStreamClicked(LiveStream liveStream, View view, int videoAction);
    void onVideoClicked(Video video, View view, int videoAction);
}