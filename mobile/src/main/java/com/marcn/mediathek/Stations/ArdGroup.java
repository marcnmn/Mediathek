package com.marcn.mediathek.stations;

import android.support.annotation.Nullable;

import com.marcn.mediathek.StationUtils.ArdUtils;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.DataUtils;
import com.marcn.mediathek.utils.NetworkTasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import rx.Observable;

public class ArdGroup extends Station {
    private static final String ard_live_api = "http://www.ardmediathek.de/tv/live";
    public static final String ARD_BASE_URL = "http://www.ardmediathek.de";
    public static final String ARD_SERIES_URL = ARD_BASE_URL + "/tv/Sendung?documentId=";
    private static final String ard_category_url = ARD_BASE_URL + "/Tipps?";
    private static final String all_series_api = ARD_BASE_URL + "/tv/sendungen-a-z?sendungsTyp=sendung&buchstabe=";

    private static final String widget_key_neuste = "Neuste Videos";
    private static final String widget_key_ausgewaehlte_filme = "Ausgewählte Filme";
    private static final String widget_key_ausgewaehlte_dokus = "Ausgewählte Dokus & Reportagen";
    private static final String widget_key_meist = "Meistabgerufene Videos";
    private static final String widget_key_beste = "Am besten bewertet";
//    private static final String widget_key_sturm = "Sturm der Liebe";

    private static final String widget_key_serien = "Serien";
    private static final String widget_key_themen = "Themen";
    private static final String widget_key_rubriken = "Rubriken";


    private ArrayList<Episode> lastEpisodes;

    public ArdGroup(String title) {
        this.title = title;

        // Setup Episode - Widgets
        top_level_categories = new LinkedHashMap<>();
        top_level_categories.put(widget_key_neuste, ARD_BASE_URL + "/tv/Neueste-Videos/mehr?documentId=23644268");
        top_level_categories.put(widget_key_ausgewaehlte_filme, ARD_BASE_URL + "/tv/Ausgewählte-Filme/Tipps?documentId=33649088");
        top_level_categories.put(widget_key_ausgewaehlte_dokus, ARD_BASE_URL + "/tv/Ausgewählte-Dokus-Reportagen/mehr?documentId=33649086");
        top_level_categories.put(widget_key_meist, ARD_BASE_URL + "/tv/Meistabgerufene-Videos/mehr?documentId=23644244");
        top_level_categories.put(widget_key_beste, ARD_BASE_URL + "/tv/Am-besten-bewertet/mehr?documentId=21282468");
//        top_level_categories.put(widget_key_sturm, ARD_BASE_URL + "/tv/Sturm-der-Liebe/Sendung?documentId=5290");

        // Setup Series - Widgets
        series_widgets = new LinkedHashMap<>();
        series_widgets.put(widget_key_serien, ARD_BASE_URL + "/tv/Serien/Tipps?documentId=26402940");
        series_widgets.put(widget_key_themen, ARD_BASE_URL + "/tv/Serien/Tipps?documentId=21301810");
        series_widgets.put(widget_key_rubriken, ARD_BASE_URL + "/tv/Serien/Tipps?documentId=21282550");

        // Setup Episode - Widgets
        episode_widgets = new LinkedHashMap<>();
    }

    @Override
    public LiveStreamM3U8 getLiveStream() {
        String url = "";
        switch (title) {
            // ARD
            case Constants.TITLE_CHANNEL_ARD: url = Constants.LIVE_STREAM_CHANNEL_ARD; break;
            case Constants.TITLE_CHANNEL_ARD_ALPHA: url = Constants.LIVE_STREAM_CHANNEL_ARD_ALPHA; break;
            case Constants.TITLE_CHANNEL_TAGESSCHAU: url = Constants.LIVE_STREAM_CHANNEL_TAGESSCHAU; break;
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: url = Constants.LIVE_STREAM_CHANNEL_SWR; break;
            case Constants.TITLE_CHANNEL_WDR: url = Constants.LIVE_STREAM_CHANNEL_WDR; break;
            case Constants.TITLE_CHANNEL_MDR: url = Constants.LIVE_STREAM_CHANNEL_MDR; break;
            case Constants.TITLE_CHANNEL_NDR: url = Constants.LIVE_STREAM_CHANNEL_NDR; break;
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: url = Constants.LIVE_STREAM_CHANNEL_BR; break;
            case Constants.TITLE_CHANNEL_SR: url = Constants.LIVE_STREAM_CHANNEL_SR; break;
            case Constants.TITLE_CHANNEL_RBB: url = Constants.LIVE_STREAM_CHANNEL_RBB; break;
        }
        return url.isEmpty() ? null : new LiveStreamM3U8(url);
    }

    @Override
    public Observable<Episode> fetchCurrentEpisode() {
        return Observable.defer(() -> Observable.just(getCurrentEpisode()));
    }

    @Override
    @Nullable
    public Episode getCurrentEpisode() {
        return ArdUtils.getCurrentEpisode(this, ard_live_api, ARD_BASE_URL);
    }

    @Override
    public ArrayList<Series> fetchAllSeries(int count, int offset) {
        ArrayList<Series> series = new ArrayList<>();
        int lastCharacter = ("Z").charAt(0);

        String url = all_series_api + "0-9";
//        series.addAll(ArdUtils.fetchSeriesList(url, title));
        ArdUtils.addSeriesList(series, url, title);

        int character = ("A").charAt(0);
        while (character <= lastCharacter) {
            url = all_series_api + String.valueOf((char) character);
//            series.addAll(ArdUtils.fetchSeriesList(url, title));
            ArdUtils.addSeriesList(series, url, title);
            character++;
        }

        return series;
    }

    @Override
    public ArrayList<Episode> fetchCategoryEpisodes(String key, int limit, int offset) {
        String url = top_level_categories.get(key);
        if (url == null) return null;
        offset = Math.round(offset / 12.f) + 1;
        url += "&mcontent=page." + offset;

        if (lastEpisodes == null)
            lastEpisodes = ArdUtils.fetchEpisodeList(url);
        else {
            ArrayList<Episode> episodes = ArdUtils.fetchEpisodeList(url);
            if (DataUtils.episodeListsAreEqual(lastEpisodes, episodes))
                lastEpisodes = null;
            else
                lastEpisodes = ArdUtils.fetchEpisodeList(url);
        }

        return lastEpisodes;
    }

    @Nullable
    @Override
    public ArrayList<Episode> fetchWidgetEpisodes(String key, String assetId, int count) {
        String url = top_level_categories.get(key);
        if (url == null) return null;
        url += "&mcontent=page.1";
        return ArdUtils.fetchEpisodeList(url);
    }

    @Override
    public Observable<ArrayList<Episode>> fetchObsWidgetEpisodes(String key, String assetId, int count) {
        return Observable.defer(() -> Observable.just(fetchWidgetEpisodes(key, assetId, count)));
    }

    @Nullable
    @Override
    public ArrayList<Series> fetchWidgetSeries(String key, String assetId, int count) {
        String url = series_widgets.get(key);
        if (url == null) return null;
        url += "&mcontent=page.1";
        return ArdUtils.addSeriesList(null, url, title);
    }

    @Nullable
    @Override
    public ArrayList<Episode> fetchSeriesEpisodes(String assetId, int count, int offset) {
        String url = ARD_SERIES_URL + assetId;

        offset = Math.round(offset / 12.f) + 1;
        url += "&mcontent=page." + offset;

        // Check if on last page
        if (offset > 1) {
            String a = NetworkTasks.downloadStringData(url);
            int beg = a.lastIndexOf("content=\"http://www.ardmediathek.de/tv/") + 1;
            int end = a.indexOf("/", beg);
            String asd = a.substring(beg, end);

            String url2 = ARD_SERIES_URL + assetId + "&mcontent=page." + (offset - 1);
            String b = NetworkTasks.downloadStringData(ARD_SERIES_URL + assetId + "&mcontent=page." + (offset - 1));
            if (a != null && a.equals(b))
                return null;
        }

        if (lastEpisodes == null)
            lastEpisodes = ArdUtils.fetchEpisodeList(url);
        else {
            ArrayList<Episode> episodes = ArdUtils.fetchEpisodeList(url);
            if (DataUtils.episodeListsAreEqual(lastEpisodes, episodes))
                lastEpisodes = null;
            else
                lastEpisodes = ArdUtils.fetchEpisodeList(url);
        }

        return lastEpisodes;
    }

    @Override
    public String getRecommendedUrl(int offset, int limit) {
        return null;
    }

    @Override
    public String getMostViewsUrl(int offset, int limit) {
        return null;
    }

    @Override
    public String getMostRecentUrl(int offset, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable
    public ArrayList<Episode> getMostRecentEpisodes(int offset, int limit, Calendar startDate, Calendar endDate) {
        return null;
    }

    @Override
    public String getStationId() {
        switch (title) {
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return "208";
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return "5868";
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return "5878";
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return "5904";
            case Constants.TITLE_CHANNEL_WDR: return "5902";
            case Constants.TITLE_CHANNEL_MDR: return "1386804";
            case Constants.TITLE_CHANNEL_NDR: return "21518352";
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return "21518950";
            case Constants.TITLE_CHANNEL_SR: return "5870";
            case Constants.TITLE_CHANNEL_RBB: return "21518358";
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return "5886";
        }
        return null;
    }

    public String getLiveQueryString() {
        switch (title) {
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return Video.ARD_QUERY;
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return Video.ARD_ALPHA_QUERY;
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return Video.TAGESSCHAU_QUERY;
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return Video.SWR_QUERY;
            case Constants.TITLE_CHANNEL_WDR: return Video.WDR_QUERY;
            case Constants.TITLE_CHANNEL_MDR: return Video.MDR_QUERY;
            case Constants.TITLE_CHANNEL_NDR: return Video.NDR_QUERY;
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return Video.BR_QUERY;
            case Constants.TITLE_CHANNEL_SR: return Video.SR_QUERY;
            case Constants.TITLE_CHANNEL_RBB: return Video.RBB_QUERY;
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return Video.KIKA_QUERY;
        }
        return null;
    }
}
