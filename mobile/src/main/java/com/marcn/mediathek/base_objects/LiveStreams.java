package com.marcn.mediathek.base_objects;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class LiveStreams {
    public final static String ZDF_ID = "2639200";
    public final static String PHOENIX_ID = "2492878";
    public final static String ZDF_KULTUR_ID = "1822544";
    public final static String ZDF_INFO_ID = "2306126";
    public final static String ZDF_NEO_ID = "1822440";

    public final ArrayList<LiveStream> mLiveStreams;

    public LiveStreams(Context context) {
        this(LiveStream.getBaseLiveStreams(context));
    }

    public LiveStreams(ArrayList<LiveStream> liveStreams) {
        this.mLiveStreams = liveStreams;
    }

    public ArrayList<LiveStream> getGroup(int originId) {
        ArrayList<LiveStream> ls = new ArrayList<>();
        for (LiveStream l : mLiveStreams)
            if (l.originChannelId == originId)
                ls.add(l);
        return ls;
    }

    @Nullable
    public LiveStream getArteLiveStream() {
        ArrayList<LiveStream> ls = getGroup(LiveStream.ARTE_GROUP);
        if (ls.size() > 0) return ls.get(0);
        else return null;
    }

    public void pushLiveStream(LiveStream l) {
        if (mLiveStreams == null || l == null) return;
        int index = mLiveStreams.indexOf(l);
        if (index < 0)
            mLiveStreams.add(l);
        else
            mLiveStreams.set(index, l);
    }

    public void pushLiveStreams(ArrayList<LiveStream> ls) {
        if (mLiveStreams == null || ls == null) return;
        for (LiveStream l : ls)
            pushLiveStream(l);
    }

    public static int indexOfId(ArrayList<LiveStream> ls, String id) {
        for (int i = 0; i < ls.size(); i++)
            if (ls.get(i).id.equals(id))
                return i;
        return -1;
    }

    public int indexOfId(String id) {
        return indexOfId(mLiveStreams, id);
    }

    public LiveStream get(int i) {
        if (mLiveStreams == null || i >= mLiveStreams.size())
            return null;
        else
            return mLiveStreams.get(i);
    }

    public int size() {
        if (mLiveStreams == null) return 0;
        else return mLiveStreams.size();
    }

    public static int indexOfName(ArrayList<LiveStream> ls, String name) {
        for (int i = 0; i < ls.size(); i++)
            if (ls.get(i).title.equals(name))
                return i;
        return -1;
    }
}
