package com.marcn.mediathek.model.base;

import android.view.View;

public interface Station {
    String getTitle();
    int getAssetId();
    View renderStationView();
}
