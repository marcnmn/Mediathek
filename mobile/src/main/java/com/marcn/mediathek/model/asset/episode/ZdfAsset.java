package com.marcn.mediathek.model.asset.episode;

import android.view.View;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.marcn.mediathek.model.asset.Asset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcneumann on 10.04.16.
 * Describes ZdfAsset Object.
 */
public class ZdfAsset implements Episode {
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("contextLink")
    @Expose
    public String contextLink;
    @SerializedName("teaserBild")
    @Expose
    public ZdfTeaserBild teaserBild;
    @SerializedName("titel")
    @Expose
    public String titel;
    @SerializedName("beschreibung")
    @Expose
    public String beschreibung;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("length")
    @Expose
    public Long length;
    @SerializedName("airtime")
    @Expose
    public String airtime;
    @SerializedName("timetolive")
    @Expose
    public String timetolive;
    @SerializedName("channel")
    @Expose
    public String channel;
    @SerializedName("originChannelId")
    @Expose
    public Long originChannelId;
    @SerializedName("originChannelTitle")
    @Expose
    public String originChannelTitle;
    @SerializedName("otherChannels")
    @Expose
    public List<Long> otherChannels = new ArrayList<>();
    @SerializedName("member")
    @Expose
    public String member;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getStationTitle() {
        return null;
    }

    @Override
    public View renderEpisodeView() {
        return null;
    }

    @Override
    public long getEpisodeLengthInMs() {
        return 0;
    }

    private class ZdfTeaserBild {

        @SerializedName("298")
        @Expose
        public Small small;
        @SerializedName("485")
        @Expose
        public Medium medium;
        @SerializedName("946")
        @Expose
        public Big big;

        private class Small {
            @SerializedName("url")
            @Expose
            public String url;
            @SerializedName("width")
            @Expose
            public Integer width;
            @SerializedName("height")
            @Expose
            public Integer height;
        }

        private class Medium {
            @SerializedName("url")
            @Expose
            public String url;
            @SerializedName("width")
            @Expose
            public Integer width;
            @SerializedName("height")
            @Expose
            public Integer height;
        }

        private class Big {
            @SerializedName("url")
            @Expose
            public String url;
            @SerializedName("width")
            @Expose
            public Integer width;
            @SerializedName("height")
            @Expose
            public Integer height;
        }
    }
}
