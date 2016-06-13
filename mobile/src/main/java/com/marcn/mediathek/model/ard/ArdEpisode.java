package com.marcn.mediathek.model.ard;

import android.view.View;

import com.marcn.mediathek.model.base.Asset;

/**
 * Created by marcneumann on 10.04.16.
 */
public class ArdEpisode extends ArdTeaser implements Asset {
    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getStationTitle() {
        return null;
    }

    @Override
    public View renderEpisodeView() {
        return null;
    }
}
