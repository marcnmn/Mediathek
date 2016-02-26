package com.marcn.mediathek.base_objects;

public class LiveStreamM3U8 {
    private String m3u8Url;

    public LiveStreamM3U8(String m3u8Url) {
        this.m3u8Url = m3u8Url;
    }

    public String getStreamUrl() {
        return m3u8Url;
    }

    public String getStreamUrl(String quality) {
        return m3u8Url;
    }

    @Override
    public String toString() {
        return m3u8Url;
    }
}
