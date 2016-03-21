package com.marcn.mediathek.base_objects;

public class Series {
    public String title, shortTitle, detail, thumb_url_low, thumb_url_high, vcmsUrl, member;
    public int assetId;
    private String thumb_url;
    private String stationTitle;
    private String episodesInfo;
    public Station station;
    public boolean isHeader;
    private boolean isHidden;

    public Series(String title, String shortTitle, String detail,
                  String thumb_url_low, String thumb_url_high, String channel, String vcmsUrl,
                  int assetId, String member) {
        this.title = title;
        this.shortTitle = shortTitle;
        this.detail = detail;
        this.thumb_url_low = thumb_url_low;
        this.thumb_url_high = thumb_url_high;
        this.vcmsUrl = vcmsUrl;
        this.assetId = assetId;
        this.member = member;

        this.stationTitle = channel;
        this.station = new Station(channel);
    }

    public Series(String title) {
        this(title, "", "", "", "", "", "", 0, title.substring(0,1).toUpperCase());
    }

    public static Series createSendungHeader(String title) {
        Series s = new Series(title, "", "", "", "", "", "", 0, "");
        s.isHeader = true;
        return s;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Series))
            return false;
        Series v = (Series) o;
        return v.assetId == this.assetId;
    }

    public String getStationTitle() {
        return stationTitle;
    }

    public void setStationTitle(String title) {stationTitle = title;}

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
        this.thumb_url_high = thumb_url;
        this.thumb_url_low = thumb_url;
    }

    public String getAssetId() {
        return String.valueOf(assetId);
    }

    public void setAssetId(String assetId) {
        this.assetId = Integer.parseInt(assetId);
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getEpisodesInfo() {
        return episodesInfo;
    }

    public void setEpisodesInfo(String episodesInfo) {
        this.episodesInfo = episodesInfo;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
