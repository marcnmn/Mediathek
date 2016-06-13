package com.marcn.mediathek.model.ard;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.marcn.mediathek.model.base.Stream;

public class ArdLive extends ArdTeaser implements Stream {
    private static final int IMAGE_LIVE_WIDTH = 500;
    private String mLiveTitle;
    private String mRemainingTime;
    private String mLiveImageUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeString(getRemainingTime());
        dest.writeString(getThumbUrl());
    }

    public static final Creator<ArdLive> CREATOR = new Creator<ArdLive>() {
        @Override
        public ArdLive createFromParcel(Parcel source) {
            return new ArdLive(source);
        }

        @Override
        public ArdLive[] newArray(int size) {
            return new ArdLive[size];
        }
    };

    public ArdLive() {

    }

    private ArdLive(Parcel in) {
        mLiveTitle = in.readString();
        mRemainingTime = in.readString();
        mLiveImageUrl = in.readString();
    }

    @Override
    public String getTitle() {
        if (mLiveTitle == null) {
            mLiveTitle = maybeParseTitle();
        }
        return mLiveTitle;
    }

    @Override
    public String getStreamTitle() {
        return getTitle();
    }

    @Override
    public String getThumbUrl() {
        if (mLiveImageUrl == null) {
            mLiveImageUrl = getThumbUrl(IMAGE_LIVE_WIDTH);
        }
        return mLiveImageUrl;
    }

    @Override
    public String getRemainingTime() {
        if (mRemainingTime == null) {
            mRemainingTime = maybeParseRemainingTime();
        }
        return mRemainingTime;
    }

    @Override
    public String getStreamUrl() {
        return null;
    }

    @Nullable
    private String maybeParseRemainingTime() {
        if (mDescription == null || mDescription.split("<br>").length < 2) {
            return null;
        }
        return mDescription.split("<br>")[0];
    }

    @Nullable
    private String maybeParseTitle() {
        if (mDescription == null || mDescription.split("<br>").length < 2) {
            return null;
        }
        return mDescription.split("<br>")[1];
    }
}
