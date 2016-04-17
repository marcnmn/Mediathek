package com.marcn.mediathek.model.station;

import android.view.View;

public interface Station {
    String getTitle();
    int getAssetId();
    View renderStationView();
}
