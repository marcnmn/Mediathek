package com.marcn.mediathek.stations;

import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import rx.Observable;

public abstract class Station {
    protected String title;
    protected LinkedHashMap<String, String> top_level_categories;
    protected LinkedHashMap<String, String> series_widgets;
    protected LinkedHashMap<String, String> episode_widgets;
    protected Episode currentEpisode2;

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
            case Constants.TITLE_CHANNEL_ARD: return new ArdGroup(title);
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return new ArdGroup(title);
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return new ArdGroup(title);
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return new ArdGroup(title);
            case Constants.TITLE_CHANNEL_WDR: return new ArdGroup(title);
            case Constants.TITLE_CHANNEL_MDR: return new ArdGroup(title);
            case Constants.TITLE_CHANNEL_NDR: return new ArdGroup(title);
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return new ArdGroup(title);
            case Constants.TITLE_CHANNEL_SR: return new ArdGroup(title);
            case Constants.TITLE_CHANNEL_HR: return new ArdGroup(title);
            case Constants.TITLE_CHANNEL_RBB: return new ArdGroup(title);
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return null;
            default: return null;
        }
    }

    public abstract LiveStreamM3U8 getLiveStream();

    public abstract Observable<Episode> fetchCurrentEpisode();
    public abstract Episode getCurrentEpisode();

    public abstract ArrayList<Series> fetchAllSeries(int count, int offset);

    public abstract ArrayList<Episode> fetchCategoryEpisodes(String key, int offset, int limit);

    public abstract Observable<ArrayList<Episode>> fetchObsWidgetEpisodes(String key, String assetId, int count);
    public abstract ArrayList<Episode> fetchWidgetEpisodes(String key, String assetId, int count);
    public abstract ArrayList<Series> fetchWidgetSeries(String mHeaderTitle, String mAssetId, int videoItemCount);
    public abstract ArrayList<Episode> fetchSeriesEpisodes(String assetId, int count, int offset);

    public abstract String getRecommendedUrl(int offset, int limit);
    public abstract String getMostViewsUrl(int offset, int limit);
    public abstract String getMostRecentUrl(int offset, int limit);

//    public abstract ArrayList<Asset> getMostRecentEpisodes(int offset, int limit);
    public abstract ArrayList<Episode> getMostRecentEpisodes(int offset, int limit, Calendar startDate, Calendar endDate);


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

    public HashMap<String, String> getSeriesWidgets() {
        return series_widgets;
    }

    public HashMap<String, String> getEpisodeWidgets() {
        return episode_widgets;
    }

    public Episode getCurrentEpisode2() {
        return currentEpisode2;
    }

    public void setCurrentEpisode2(Episode currentEpisode2) {
        this.currentEpisode2 = currentEpisode2;
    }
}
