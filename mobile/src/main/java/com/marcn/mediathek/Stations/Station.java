package com.marcn.mediathek.Stations;

import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public abstract class Station {
    final String title;
    protected HashMap<String, String> top_level_categories;

    public Station(String title) {
        this.title = title;
    }

    public abstract LiveStreamM3U8 getLiveStream();
    public abstract Episode getCurrentEpisode();

    public abstract String getRecommendedUrl(int offset, int limit);
    public abstract String getMostViewsUrl(int offset, int limit);
    public abstract String getMostRecentUrl(int offset, int limit);

//    public abstract ArrayList<Episode> getMostRecentEpisodes(int offset, int limit);
    public abstract ArrayList<Episode> getMostRecentEpisodes(int offset, int limit, Calendar startDate, Calendar endDate);

    public abstract String getCategoryUrl(String key, int offset, int limit);

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Station))
            return false;
        Station ls = (Station) o;
        return ls.title.equals(this.title);
    }

    public HashMap<String, String> getTop_level_categories() {
        return top_level_categories;
    }
}
