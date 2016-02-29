package com.marcn.mediathek.stations;

import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class Station {
    protected String title;
    protected LinkedHashMap<String, String> top_level_categories;
    protected LinkedHashMap<String, String> episode_widgets;

    public static Station createStation(String title) {
        if (title == null) return null;
        switch (title) {
            // ZDF Sender
            case Constants.TITLE_CHANNEL_ZDF: return new ZdfGroup(title);
            case Constants.TITLE_CHANNEL_PHOENIX: return new ZdfGroup(title);
            case Constants.TITLE_CHANNEL_ZDF_KULTUR: return new ZdfGroup(title);
            case Constants.TITLE_CHANNEL_ZDF_INFO: return new ZdfGroup(title);
            case Constants.TITLE_CHANNEL_3SAT: return new ZdfGroup(title);
            case Constants.TITLE_CHANNEL_ZDF_NEO: return new ZdfGroup(title);
            // ARTE
            case Constants.TITLE_CHANNEL_ARTE: return new Arte();
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return null;
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return null;
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return null;
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return null;
            case Constants.TITLE_CHANNEL_WDR: return null;
            case Constants.TITLE_CHANNEL_MDR: return null;
            case Constants.TITLE_CHANNEL_NDR: return null;
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return null;
            case Constants.TITLE_CHANNEL_SR: return null;
            case Constants.TITLE_CHANNEL_HR: return null;
            case Constants.TITLE_CHANNEL_RBB: return null;
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return null;
            default: return null;
        }
    }

    public abstract LiveStreamM3U8 getLiveStream();
    public abstract Episode getCurrentEpisode();

    public abstract ArrayList<Episode> fetchWidgetEpisodes(String key, String assetId, int count);

    public abstract String getRecommendedUrl(int offset, int limit);
    public abstract String getMostViewsUrl(int offset, int limit);
    public abstract String getMostRecentUrl(int offset, int limit);

//    public abstract ArrayList<Episode> getMostRecentEpisodes(int offset, int limit);
    public abstract ArrayList<Episode> getMostRecentEpisodes(int offset, int limit, Calendar startDate, Calendar endDate);

    public abstract String getCategoryUrl(String key, int offset, int limit);

    public abstract String getStationId();

    public String getTitle() {
        return title;
    }

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

    public HashMap<String, String> getTopLevelCategories() {
        return top_level_categories;
    }

    public HashMap<String, String> getEpisodeWidgets() {
        return episode_widgets;
    }
}
