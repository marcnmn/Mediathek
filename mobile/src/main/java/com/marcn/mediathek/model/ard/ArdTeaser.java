package com.marcn.mediathek.model.ard;

import android.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ArdTeaser implements ArdAsset {
    protected String mId;
    protected String mType;
    protected String mTeaserType;
    protected String mStationTitle;
    protected String mDescription;
    protected List<TeaserImage> mTeaserImages = new ArrayList<>();
    private Link mLink;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public String getTitle() {
        return mDescription;
    }

    @Override
    public String getStationTitle() {
        return mStationTitle;
    }

    @Override
    public View renderEpisodeView() {
        return null;
    }

    @JsonProperty("typ")
    public String getType() {
        return mType;
    }

    @JsonProperty("typ")
    public void setType(String type) {
        mType = type;
    }

    @JsonProperty("teaserTyp")
    public String getTeaserType() {
        return mTeaserType;
    }

    @JsonProperty("teaserTyp")
    public void setTeaserType(String teaserType) {
        mTeaserType = teaserType;
    }

    @JsonProperty("ueberschrift")
    public void setStationTitle(String stationTitle) {
        mStationTitle = stationTitle;
    }

    @JsonProperty("unterzeile")
    public void setDescription(String description) {
        mDescription = description;
    }

    @JsonProperty("bilder")
    public List<TeaserImage> getTeaserImages() {
        return mTeaserImages;
    }

    @JsonProperty("bilder")
    public void setTeaserImages(ArrayList<TeaserImage> teaserImages) {
        mTeaserImages = teaserImages;
    }

    public String getThumbUrl(int width) {
        if (mTeaserImages == null || mTeaserImages.isEmpty()) {
            return null;
        }
        return getTeaserImages().get(0).mUrl.replace("##width##", String.valueOf(width));
    }

    @JsonProperty("link")
    public void setLink(Link link) {
        mLink = link;
        setId(parseLinkForId(link));
    }

    private String parseLinkForId(Link link) {
        if (link == null || link.mUrl == null) {
            return null;
        }
        int start = mLink.mUrl.lastIndexOf("/") + 1;
        int end = mLink.mUrl.indexOf("?");
        return mLink.mUrl.substring(start, end);
    }

    private static class TeaserImage {
        @JsonProperty("schemaUrl")
        public String mUrl;
        @JsonProperty("initUrl")
        public String mInitUrl;
    }

    private static class Link {
        @JsonProperty("url")
        public String mUrl;
    }
}
