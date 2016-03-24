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

    public final ArrayList<Video> mVideos;

    public LiveStreams(Context context) {
        this(Video.getBaseLiveStreams(context));
    }

    public LiveStreams(ArrayList<Video> videos) {
        this.mVideos = videos;
    }

    public ArrayList<Video> getGroup(int originId) {
        ArrayList<Video> ls = new ArrayList<>();
        for (Video l : mVideos)
            if (l.originChannelId == originId)
                ls.add(l);
        return ls;
    }

    @Nullable
    public Video getArteLiveStream() {
        ArrayList<Video> ls = getGroup(Video.ARTE_GROUP);
        if (ls.size() > 0) return ls.get(0);
        else return null;
    }

    public void pushLiveStream(Video l) {
        if (mVideos == null || l == null) return;
        int index = mVideos.indexOf(l);
        if (index < 0)
            mVideos.add(l);
        else
            mVideos.set(index, l);
    }

    public void pushLiveStreams(ArrayList<Video> ls) {
        if (mVideos == null || ls == null) return;
        for (Video l : ls)
            pushLiveStream(l);
    }

    public static int indexOfId(ArrayList<Video> ls, String id) {
        for (int i = 0; i < ls.size(); i++)
            if (ls.get(i).id.equals(id))
                return i;
        return -1;
    }

    public int indexOfId(String id) {
        return indexOfId(mVideos, id);
    }

    public Video get(int i) {
        if (mVideos == null || i >= mVideos.size())
            return null;
        else
            return mVideos.get(i);
    }

    public int size() {
        if (mVideos == null) return 0;
        else return mVideos.size();
    }

    public static int indexOfName(ArrayList<Video> ls, String name) {
        for (int i = 0; i < ls.size(); i++)
            if (ls.get(i).channel.equals(name))
                return i;
        return -1;
    }
}
