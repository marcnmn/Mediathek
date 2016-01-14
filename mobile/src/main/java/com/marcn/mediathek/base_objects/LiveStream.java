package com.marcn.mediathek.base_objects;

public class LiveStream {
    public final String id;
    public final String title;
    public final String thumb_url;

    public LiveStream(String id, String title, String thumb_url) {
        this.id = id;
        this.title = title;
        this.thumb_url = thumb_url;
    }

    public String getLiveM3U8 () {
        switch (title) {
            case "ZDF": return http://zdf1314-lh.akamaihd.net/i/de14_v1@392878/master.m3u8?dw=0;
        }
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LiveStream))
            return false;
        LiveStream ls = (LiveStream) o;
        return ls.title.equals(this.title);
    }
}
