package com.marcn.mediathek.model.asset.episode;

import android.view.View;

import com.marcn.mediathek.model.asset.Asset;

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
}
