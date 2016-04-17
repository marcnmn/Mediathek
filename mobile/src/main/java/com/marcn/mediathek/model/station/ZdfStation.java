package com.marcn.mediathek.model.station;

import android.view.View;

import com.marcn.mediathek.model.station.Station;

public class ZdfStation implements Station {
    
    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public int getAssetId() {
        return 0;
    }

    @Override
    public View renderStationView() {
        return null;
    }
}
