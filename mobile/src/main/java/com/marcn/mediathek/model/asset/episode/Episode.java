package com.marcn.mediathek.model.asset.episode;

import com.marcn.mediathek.model.asset.Asset;

public interface Episode extends Asset {

    long getEpisodeLengthInMs();
}
