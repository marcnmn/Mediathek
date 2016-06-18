package com.marcn.mediathek.model.base;

import android.os.Parcelable;

public interface Stream extends Asset, Parcelable {

    String getStreamTitle();

    String getStationTitle();

    String getThumbUrl();

    String getRemainingTime();

    String getStreamUrl();
}
