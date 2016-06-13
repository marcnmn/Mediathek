package com.marcn.mediathek.model.zdf;

import android.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marcn.mediathek.utils.FormatTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ZdfTeaser implements ZdfAsset {
    protected static final int LOW = 0;
    protected static final int MEDIUM = 1;
    protected static final int HIGH = 2;

    @JsonProperty("teaserBild")
    private TeaserImages mTeaserImages;
    @JsonProperty("titel")
    private String mTitle;
    @JsonProperty("beschreibung")
    private String mDescribtion;
    private String mId;
    private Long mLength;
    private Calendar mAirtime;
    private String mTimetolive;
    private String mChannel;
    private Long mOriginChannelId;
    private String mOriginChannelTitle;

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getStationTitle() {
        return mChannel;
    }

    @Override
    public View renderEpisodeView() {
        return null;
    }

    public String getThumbUrl() {
        if (mTeaserImages.mImages.keySet().contains(HIGH)) {
            return mTeaserImages.mImages.get(HIGH).url;
        } else if (mTeaserImages.mImages.keySet().contains(MEDIUM)) {
            return mTeaserImages.mImages.get(MEDIUM).url;
        } else if (mTeaserImages.mImages.keySet().contains(LOW)) {
            return mTeaserImages.mImages.get(LOW).url;
        } else {
            return null;
        }
    }

    public String getThumbUrl(int quality) {
        if (mTeaserImages.mImages.keySet().contains(quality)) {
            return mTeaserImages.mImages.get(quality).url;
        }
        return null;
    }

    public void setTeaserImages(TeaserImages teaserImages) {
        mTeaserImages = teaserImages;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescribtion() {
        return mDescribtion;
    }

    public void setDescribtion(String describtion) {
        mDescribtion = describtion;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Long getLength() {
        return mLength;
    }

    public void setLength(Long length) {
        mLength = length * 1000;
    }

    public String getAirtime() {
        if (mAirtime == null && mLength <= 0) return "";
        String sLength = mLength / 60000 + " min";
        if (mAirtime == null) return sLength;
        String sAirtime = FormatTime.calendarToEpisodeStartFormat(mAirtime);
        if (mLength <= 0)
            return sAirtime;
        else return sAirtime + " | " + sLength;
    }

    public void setAirtime(String airtime) {
        mAirtime = FormatTime.zdfAirtimeStringToDate(airtime);
    }

    public String getTimetolive() {
        return mTimetolive;
    }

    public void setTimetolive(String timetolive) {
        mTimetolive = timetolive;
    }

    public String getChannel() {
        return mChannel;
    }

    public void setChannel(String channel) {
        mChannel = channel;
    }

    public Long getOriginChannelId() {
        return mOriginChannelId;
    }

    public void setOriginChannelId(Long originChannelId) {
        mOriginChannelId = originChannelId;
    }

    public String getOriginChannelTitle() {
        return mOriginChannelTitle;
    }

    public void setOriginChannelTitle(String originChannelTitle) {
        mOriginChannelTitle = originChannelTitle;
    }

    private static class TeaserImages {

        private HashMap<Integer, SingleImage> mImages = new LinkedHashMap<>();

        @JsonProperty("276")
        public void setLowTeaser(SingleImage image) {
            mImages.put(LOW, image);
        }

        @JsonProperty("485")
        public void setMediumTeaser(SingleImage image) {
            mImages.put(MEDIUM, image);
        }

        @JsonProperty("946")
        public void setHigheaser(SingleImage image) {
            mImages.put(HIGH, image);
        }

        private static class SingleImage {
            public String url;
            public Integer width;
            public Integer height;

            public SingleImage() {
            }
        }
    }
}
