package com.marcn.mediathek.base_objects;

import com.marcn.mediathek.utils.FormatTime;

import java.util.Calendar;

public class Episode2 {
    public static final int ACTION_INTERNAL_PLAYER = 0;
    public static final int ACTION_SHARE_VIDEO_DIALOG = 1;
    public static final int ACTION_DEFAULT_EXTERNAL_PLAYER = 2;
    public static final int ACTION_DOWNLOAD = 3;

    // Essentials
    private final String title;
    private final Station station;
    private final int assetId;

    private Calendar startTime;
    private long episodeLengthInMs;
    private boolean isHeader;

    private String remainingTime;

    // Additional information
    private String description, thumb_url, browserUrl;

    // Mediathek specific
    private boolean nurOnline, onlineFassung, ganzeSendung;

    public Episode2(String title, Station station) {
        this(title, station, (int) (Math.random() * Integer.MAX_VALUE));
    }

    public Episode2(String title, Station station, int assetId) {
        this.title = title;
        this.station = station;
        this.assetId = assetId;
    }

    public Episode2(String title, Station station, int assetId, Calendar startTime, long episodeLengthInMs) {
        this.title = title;
        this.station = station;
        this.assetId = assetId;
        this.startTime = startTime;
        this.episodeLengthInMs = episodeLengthInMs;
    }

    public static Episode2 createHeader(String title) {
        Episode2 episode = new Episode2(title, null);
        episode.isHeader = true;
        return episode;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Episode2))
            return false;
        Episode2 v = (Episode2) o;
        return v.assetId == this.assetId;
    }

    public String getTitle() {
        return title;
    }

    public Station getStation() {
        return station;
    }

    public int getAssetId() {
        return assetId;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public long getEpisodeLengthInMs() {
        return episodeLengthInMs;
    }

    public void setEpisodeLengthInMs(long episodeLengthInMs) {
        this.episodeLengthInMs = episodeLengthInMs;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getBrowserUrl() {
        return browserUrl;
    }

    public void setBrowserUrl(String browserUrl) {
        this.browserUrl = browserUrl;
    }

    public boolean getNurOnline() {
        return nurOnline;
    }

    public void setNurOnline(boolean nurOnline) {
        this.nurOnline = nurOnline;
    }

    public boolean getOnlineFassung() {
        return onlineFassung;
    }

    public void setOnlineFassung(boolean onlineFassung) {
        this.onlineFassung = onlineFassung;
    }

    public boolean getGanzeSendung() {
        return ganzeSendung;
    }

    public void setGanzeSendung(boolean ganzeSendung) {
        this.ganzeSendung = ganzeSendung;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Calendar start, long ms) {
        remainingTime = "";
        if (startTime != null && episodeLengthInMs >= 0)
            remainingTime = "Noch " + FormatTime.remainingMinutes(start, ms) + " min";
    }
}
