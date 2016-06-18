package com.marcn.mediathek.player;

import android.support.annotation.NonNull;

public interface PlayerControls {

    void seekTo(long positionMs);

    void play(@NonNull String url);

    void resume();

    void pause();

    long getDuration();

    long getCurrentPosition();
}
