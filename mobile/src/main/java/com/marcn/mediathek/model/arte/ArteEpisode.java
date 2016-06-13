package com.marcn.mediathek.model.arte;

import android.view.View;

import com.marcn.mediathek.model.base.Episode;

/**
 * Created by marcneumann on 10.04.16.
 */
public class ArteEpisode implements Episode {

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

    @Override
    public long getEpisodeLengthInMs() {
        return 0;
    }

    @Override
    public String getThumbUrl() {
        return null;
    }
}
