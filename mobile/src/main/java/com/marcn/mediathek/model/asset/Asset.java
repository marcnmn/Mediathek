package com.marcn.mediathek.model.asset;

import android.view.View;

public interface Asset {
    String getTitle();
    String getStationTitle();
    View renderEpisodeView();
}
