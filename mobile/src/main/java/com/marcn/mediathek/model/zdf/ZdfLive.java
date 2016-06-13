package com.marcn.mediathek.model.zdf;

import android.os.Parcel;

import com.marcn.mediathek.model.base.Stream;

public class ZdfLive extends ZdfTeaser implements Stream {
    private String mLiveTitle;
    private String mRemainingTime;
    private String mLiveImageUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLiveTitle);
        dest.writeString(mRemainingTime);
        dest.writeString(mLiveImageUrl);
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

    public ZdfLive() {
    }

    private ZdfLive(Parcel in) {
        mLiveTitle = in.readString();
        mRemainingTime = in.readString();
        mLiveImageUrl = in.readString();
    }


    @Override
    public String getStreamTitle() {
        mLiveTitle = getTitle();
        return mLiveTitle;
    }

    @Override
    public String getRemainingTime() {
        return null;
    }

    @Override
    public String getStreamUrl() {
        return null;
    }

    @Override
    public String getThumbUrl() {
        mLiveImageUrl = getThumbUrl(MEDIUM);
        return mLiveImageUrl;
    }
}
