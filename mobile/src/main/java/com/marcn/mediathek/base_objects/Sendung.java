package com.marcn.mediathek.base_objects;

public class Sendung {
    public final String title, shortTitle, detail, thumb_url, vcmsUrl;
    public int assetId;
    public Channel channel;
    public boolean isHeader;

    public Sendung(String title, String shortTitle, String detail,
                   String thumb_url, String channel, String vcmsUrl,
                   int assetId) {
        this.title = title;
        this.shortTitle = shortTitle;
        this.detail = detail;
        this.thumb_url = thumb_url;
        this.vcmsUrl = vcmsUrl;
        this.assetId = assetId;

        this.channel = new Channel(channel);
    }

    public static Sendung createSendungItem(String title) {
        Sendung s = new Sendung(title, "", "", "", "", "", 0);
        return s;
    }

    public static Sendung createSendungHeader(String title) {
        Sendung s = new Sendung(title, "", "", "", "", "", 0);
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
