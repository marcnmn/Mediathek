package com.marcn.mediathek.model.zdf;

import android.os.Parcel;

import com.marcn.mediathek.model.base.Stream;

public class ZdfLive extends ZdfTeaser implements Stream {
    private String mLiveImageUrl;
    private String mStreamUrl;

    private static final String LIVE_ZDF = "http://zdf1314-lh.akamaihd.net/i/de14_v1@392878/index_3056_av-p.m3u8";
    private static final String LIVE_PHOENIX = "http://zdf0910-lh.akamaihd.net/i/de09_v1@392871/master.m3u8";
    private static final String LIVE_ZDF_KULTUR = "http://zdf1112-lh.akamaihd.net/i/de11_v1@392881/master.m3u8";
    private static final String LIVE_ZDF_INFO = "http://zdf1112-lh.akamaihd.net/i/de12_v1@392882/master.m3u8";
    private static final String LIVE_3SAT = "http://zdf0910-lh.akamaihd.net/i/dach10_v1@392872/master.m3u8";
    private static final String LIVE_ZDF_NEO = "http://zdf1314-lh.akamaihd.net/i/de13_v1@392877/master.m3u8";

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mStationTitle);
        dest.writeString(getThumbUrl());
        dest.writeString(getStreamUrl());
    }

    public ZdfLive() {
    }

    private ZdfLive(Parcel in) {
        mTitle = in.readString();
        mStationTitle = in.readString();
        mLiveImageUrl = in.readString();
        mStreamUrl = in.readString();
    }

    public static final Creator<ZdfLive> CREATOR = new Creator<ZdfLive>() {
        @Override
        public ZdfLive createFromParcel(Parcel source) {
            return new ZdfLive(source);
        }

        @Override
        public ZdfLive[] newArray(int size) {
            return new ZdfLive[size];
        }
    };

    @Override
    public String getStreamTitle() {
        return mTitle;
    }


    @Override
    public String getRemainingTime() {
        return null;
    }

    @Override
    public String getThumbUrl() {
        return mLiveImageUrl != null ? mLiveImageUrl : getThumbUrl(MEDIUM);
    }

    @Override
    public String getStreamUrl() {
        if (mStreamUrl != null) {
            return mStreamUrl;
        }

        switch (getStationTitle()) {
            case "ZDF":
                return LIVE_ZDF;
            case "ZDFneo":
                return LIVE_ZDF_NEO;
            case "ZDF.kultur":
                return LIVE_ZDF_KULTUR;
            case "ZDFinfo":
                return LIVE_ZDF_INFO;
            case "3sat":
                return LIVE_3SAT;
            case "Phoenix":
                return LIVE_PHOENIX;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
