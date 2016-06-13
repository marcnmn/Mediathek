package com.marcn.mediathek.model.ard;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marcn.mediathek.model.base.Video;

import java.util.List;

public class ArdVideo implements Video {
    private String mType;
    private boolean mIsLive;
    private String mPreviewImageUrl;
    private int mSubtitleOffset;
    private long mDuration;
    private boolean mDvrEnabled;
    private boolean mGeoblocked;
    private List<ArdMedia> mMediaArray;

    public boolean isLive() {
        return mIsLive;
    }

    @JsonProperty("_isLive")
    public void setLive(boolean live) {
        mIsLive = live;
    }

    public String getPreviewImageUrl() {
        return mPreviewImageUrl;
    }

    @JsonProperty("_previewImage")
    public void setPreviewImageUrl(String previewImageUrl) {
        mPreviewImageUrl = previewImageUrl;
    }

    public int getSubtitleOffset() {
        return mSubtitleOffset;
    }

    @JsonProperty("_subtitleOffset")
    public void setSubtitleOffset(int subtitleOffset) {
        mSubtitleOffset = subtitleOffset;
    }

    public long getDuration() {
        return mDuration;
    }

    @JsonProperty("_duration")
    public void setDuration(long duration) {
        mDuration = duration;
    }

    public boolean isDvrEnabled() {
        return mDvrEnabled;
    }

    @JsonProperty("_dvrEnabled")
    public void setDvrEnabled(boolean dvrEnabled) {
        mDvrEnabled = dvrEnabled;
    }

    public boolean isGeoblocked() {
        return mGeoblocked;
    }

    @JsonProperty("_geoblocked")
    public void setGeoblocked(boolean geoblocked) {
        mGeoblocked = geoblocked;
    }

    @JsonProperty("_mediaArray")
    public void setMediaArray(List<ArdMedia> mediaArray) {
        mMediaArray = mediaArray;
    }

    @Override
    public String getUrl() {
        return getMediaStream() != null ? getMediaStream().stream : null;
    }

    @Override
    public String getThumbnailUrl() {
        return mPreviewImageUrl;
    }

    @Nullable
    private ArdMedia.MediaStream getMediaStream() {
        if (mMediaArray == null || mMediaArray.isEmpty()) {
            return null;
        }
        ArdMedia ardMedia = mMediaArray.get(0);
        if (ardMedia == null || ardMedia.mediaStreamArray == null || ardMedia.mediaStreamArray.isEmpty()) {
            return null;
        }
        return ardMedia.mediaStreamArray.get(0);
    }

    private static class ArdMedia {
        @JsonProperty("_plugin")
        int plugin;
        @JsonProperty("_mediaStreamArray")
        List<MediaStream> mediaStreamArray;

        private static class MediaStream {
            @JsonProperty("_quality")
            String quality;
            @JsonProperty("_server")
            String server;
            @JsonProperty("_cdn")
            String cdn;
            @JsonProperty("_stream")
            String stream;
        }
    }
}
