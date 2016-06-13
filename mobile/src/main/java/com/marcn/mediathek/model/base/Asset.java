package com.marcn.mediathek.model.base;

import android.view.View;

public interface Asset {
    String getTitle();
    String getStationTitle();
    View renderEpisodeView();
}
