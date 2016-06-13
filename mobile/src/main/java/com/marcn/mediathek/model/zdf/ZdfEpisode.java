package com.marcn.mediathek.model.zdf;

import com.marcn.mediathek.model.base.Episode;

public class ZdfEpisode extends ZdfTeaser implements Episode {
    private boolean isHeader;

    @Override
    public long getEpisodeLengthInMs() {
        return getLength();
    }

    @Override
    public String getThumbUrl() {
        return getThumbUrl(ZdfTeaser.MEDIUM);
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }
}
