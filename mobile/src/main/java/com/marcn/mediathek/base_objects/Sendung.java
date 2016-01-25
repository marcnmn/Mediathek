package com.marcn.mediathek.base_objects;

public class Sendung {
    public final String title, shortTitle, detail, thumb_url_low, thumb_url_high, vcmsUrl, member;
    public int assetId;
    public Channel channel;
    public boolean isHeader;

    public Sendung(String title, String shortTitle, String detail,
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

        this.channel = new Channel(channel);
    }

    public static Sendung createSendungHeader(String title) {
        Sendung s = new Sendung(title, "", "", "", "", "", "", 0, "");
        s.isHeader = true;
        return s;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sendung))
            return false;
        Sendung v = (Sendung) o;
        return v.assetId == this.assetId;
    }
}
