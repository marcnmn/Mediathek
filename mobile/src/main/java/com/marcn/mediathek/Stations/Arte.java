package com.marcn.mediathek.stations;

import android.support.annotation.Nullable;

import com.marcn.mediathek.StationUtils.ArteUtils;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.DataUtils;
import com.marcn.mediathek.utils.EpgUtils;
import com.marcn.mediathek.utils.NetworkTasks;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class Arte extends Station {
    private static final String channel_title = "Arte";

    private static final String base_url = "http://www.arte.tv";
    private static final String search_api = "/papi/tvguide/videos/plus7/program/";
    private static final String live_stream_api = "/papi/tvguide/videos/livestream/player/";
    private static final String live_stream_m3u8 = "http://delive.artestras.cshls.lldns.net/artestras/delive/delive.m3u8";
    private static final String video_api = "http://www.arte.tv/papi/tvguide/videos/stream/";

    public static final String LANG_FRENCH = "F";
    public static final String LANG_GERMAN = "D";

    private static final String default_lang = LANG_GERMAN;
    private static final String default_detail_level = "L2";
    private static final String default_category = "ALL";
    private static final String default_cluster = "ALL";
    private static final String default_recommended = "-1";
    private static final String default_sort = "AIRDATE_DESC";

    private String lang = LANG_GERMAN;

    // http://www.arte.tv/papi/tvguide/videos/plus7/program/{lang}/{detailLevel}/{category}/{cluster}/{recommended}/{sort}/{limit}/{offset}/DE_FR.json
    public Arte() {
        this.title = channel_title;

        top_level_categories = new LinkedHashMap<>();
        top_level_categories.put("Alle", "ALL");
        top_level_categories.put("Aktuelles und Gesellschaft", "ACT");
        top_level_categories.put("Fernsehfilme & Serien", "FIC");
        top_level_categories.put("Kino", "CIN");
        top_level_categories.put("Kunst & Kultur", "ART");
        top_level_categories.put("Popkultur & Alternativ", "CUL");
        top_level_categories.put("Entdeckung", "DEC");
        top_level_categories.put("Geschichte", "HIS");
        top_level_categories.put("Junior", "JUN");
    }

    @Override
    public LiveStreamM3U8 getLiveStream() {
        return new LiveStreamM3U8(live_stream_m3u8);
    }

    public TreeMap<Integer, String> getVodUrls(String id){
        String url = video_api + lang + "/" + id + "_PLUS7-" + lang + "/ALL/ALL.json";
        return ArteUtils.getVideoUrls(url);
    }

    @Override
    @Nullable
    public Episode getCurrentEpisode() {
        String url = Constants.LIVE_STREAM_EPG_URL + Constants.LIVE_STREAM_EPG_ARTE_NAME + "/now/json";
        Episode episode = EpgUtils.getZDFLiveEpisode(url, 0);

        if (episode != null)
            episode.setThumb_url(getLiveThumbnail());

        return episode;
    }

    @Override
    public ArrayList<Episode> fetchWidgetEpisodes(String key, String assetId, int count) {
        String request = getSearchRequestUrl(top_level_categories.get(key), 0, count);
        ArrayList<Episode> episodes = new ArrayList<>();
        episodes = ArteUtils.fetchVideoList(request);

        return episodes;
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
        return null;
    }

//    @Override
//    public ArrayList<Episode> getMostRecentEpisodes(int offset, int limit) {
//        return null;
//    }

    @Override
    public ArrayList<Episode> getMostRecentEpisodes(int offset, int limit, Calendar startDate, Calendar endDate) {
        return null;
    }

    @Override
    @Nullable
    public String getCategoryUrl(String key, int offset, int limit) {
        String category = top_level_categories.get(key);
        if (category == null || category.isEmpty()) return null;
        return getSearchRequestUrl(category, offset, limit);
    }

    @Override
    public String getStationId() {
        return "Arte";
    }


    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        if (LANG_FRENCH.equals(lang))
            lang = LANG_FRENCH;
        else
            lang = LANG_GERMAN;
    }

    private static String getSearchRequestUrl(String category, int offset, int limit) {
        return getSearchRequestUrl(null, null, category, null, null, null, offset, limit);
    }

    private static String getSearchRequestUrl(String lang, String detailLevel, String category,
                                        String cluster, String recommended, String sort,
                                        int offset, int limit) {
        String req = base_url + search_api;
        req += (lang != null ? lang : default_lang) + "/";
        req += (detailLevel != null ? detailLevel : default_detail_level) + "/";
        req += (category != null ? category : default_category) + "/";
        req += (cluster != null ? cluster : default_cluster) + "/";
        req += (recommended != null ? recommended : default_recommended) + "/";
        req += (sort != null ? sort : default_sort) + "/";
        req += limit + "/";
        req += offset + "/DE_FR.json";

        return req;
    }

    @Nullable
    private String getLiveThumbnail() {
        String url = base_url + live_stream_api + "D";
        try {
            JSONObject jsonObject = NetworkTasks.downloadJSONData(url);
            return jsonObject.getJSONObject("videoJsonPlayer").getString("programImage");
        } catch (JSONException | NullPointerException ignored) {
        }
        return null;
    }
}
