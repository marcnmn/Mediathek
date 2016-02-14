package com.marcn.mediathek.base_objects;

public class Episode {
    public static final int ACTION_INTERNAL_PLAYER = 0;
    public static final int ACTION_SHARE_VIDEO_DIALOG = 1;
    public static final int ACTION_DEFAULT_EXTERNAL_PLAYER = 2;
    public static final int ACTION_DOWNLOAD = 3;

    public String title, detail, thumb_url, airtime, vcmsUrl;
    public String nurOnline, onlineFassung, ganzeSendung, originChannelTitle;
    public int assetId, originChannelId, lengthSec;
    public Channel channel;

    public String dayAndDate;
    public boolean isHeader;

    public Episode(String dayAndDate) {
        isHeader = true;
        this.dayAndDate = dayAndDate;
    }

    public Episode(String title, String detail, String thumb_url,
                   String channel, String airtime, String vcmsUrl,
                   int assetId, int originChannelId, int lengthSec) {
        this.title = title;
        this.detail = detail;
        this.thumb_url = thumb_url;
        this.airtime = airtime;
        this.vcmsUrl = vcmsUrl;
        this.assetId = assetId;
        this.originChannelId = originChannelId;
        this.lengthSec = lengthSec;

        this.channel = new Channel(channel);
    }

    public Episode(String title, String detail, String thumb_url,
                   String channel, String airtime, String vcmsUrl,
                   int assetId, int originChannelId, int lengthSec,
                   String nurOnline, String onlineFassung, String ganzeSendung, String originChannelTitle) {
        this.title = title;
        this.detail = detail;
        this.thumb_url = thumb_url;
        this.airtime = airtime;
        this.vcmsUrl = vcmsUrl;
        this.assetId = assetId;
        this.originChannelId = originChannelId;
        this.lengthSec = lengthSec;

        this.channel = new Channel(channel);

        this.nurOnline = nurOnline;
        this.onlineFassung = onlineFassung;
        this.ganzeSendung = ganzeSendung;
        this.originChannelTitle = originChannelTitle;
    }

    public static Episode createHeaderVideo(String title) {
        Episode episode = new Episode(title, "", "", "", "", "", 0, 0, 0);
        episode.isHeader = true;
        return episode;
    }

    public String getAirTimeDay() {
        return airtime.length() >= 2 ? airtime.substring(0, 2) : "";
    }

    public String getAirtime() {
        if (airtime == null) return "";
        String [] s = airtime.split(" ");
        if (s.length >= 2)
            return s[1];
        return airtime;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Episode))
            return false;
        Episode v = (Episode) o;
        return v.assetId == this.assetId;
    }
}
