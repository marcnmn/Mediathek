package com.marcn.mediathek.model.base;

public interface Episode extends Asset {

    long getEpisodeLengthInMs();

    String getThumbUrl();
}
