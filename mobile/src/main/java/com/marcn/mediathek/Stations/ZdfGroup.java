package com.marcn.mediathek.stations;

import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.marcn.mediathek.R;
import com.marcn.mediathek.StationUtils.ZdfUtils;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.DataUtils;
import com.marcn.mediathek.utils.EpgUtils;
import com.marcn.mediathek.utils.FormatTime;
import com.marcn.mediathek.utils.XmlParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;

public class ZdfGroup extends Station {
    private static final String live_m3u8_zdf = "http://zdf1314-lh.akamaihd.net/i/de14_v1@392878/index_3056_av-p.m3u8?sd=10&amp;dw=0&amp;rebase=on&amp;hdntl=";
    private static final String live_m3u8_phoenix = "http://zdf0910-lh.akamaihd.net/i/de09_v1@392871/master.m3u8";
    private static final String live_m3u8_kultur = "http://zdf1112-lh.akamaihd.net/i/de11_v1@392881/master.m3u8?dw=0";
    private static final String live_m3u8_info = "http://zdf1112-lh.akamaihd.net/i/de12_v1@392882/master.m3u8?dw=0";
    private static final String live_m3u8_3sat = "http://zdf0910-lh.akamaihd.net/i/dach10_v1@392872/master.m3u8?dw=0";
    private static final String live_m3u8_neo = "http://zdf1314-lh.akamaihd.net/i/de13_v1@392877/master.m3u8?dw=0";

    private static final String live_epg_ZDF_key_key = "zdf";
    private static final String live_epg_phoenix_key = "phoenix";
    private static final String live_epg_kultur_key = "zdfkultur";
    private static final String live_epg_info_key = "zdfinfo";
    private static final String live_epg_3sat_key = "3sat";
    private static final String live_epg_neo_key = "zdfneo";

    private static final String station_id_neo = "857392";
    private static final String station_id_kultur = "1321386";
    private static final String station_id_info = "398";

    public final static String top_level_id = "_STARTSEITE";
    public final static String top_level_id_most = "_GLOBAL";

    private static final String all_series_api = "http://www.zdf.de/ZDFmediathek/xmlservice/web/sendungenAbisZ";
    private static final String live_stream_api = "http://www.zdf.de/ZDFmediathek/xmlservice/web/live?maxLength=6";
    private static final String most_recent_api = "http://www.zdf.de/ZDFmediathek/xmlservice/web/sendungVerpasst?";
    private static final String live_epg_api = "http://sofa01.zdf.de/epgservice/";

    private static final String widget_key_aktuellste = "Aktuellste Sendungen";
    private static final String widget_key_meistgesehen = "Meist gesehen";
    private static final String widget_key_weitere = "Weitere";
    private static final String widget_key_tipps = "Tipps";

    private static final String aktuellste_api = "http://www.zdf.de/ZDFmediathek/xmlservice/web/aktuellste";
    private static final String meist_gesehen_api = "http://www.zdf.de/ZDFmediathek/xmlservice/web/meistGesehen";
    private static final String weitere_beitraege_api = "http://www.zdf.de/ZDFmediathek/xmlservice/web/weitereBeitraege";
    private static final String tipps_api = "http://www.zdf.de/ZDFmediathek/xmlservice/web/tipps";

    public ZdfGroup(String title) {
        this.title = title;

        // Setup Episode - Widgets
        top_level_categories = new LinkedHashMap<>();
        top_level_categories.put(widget_key_aktuellste, aktuellste_api);
        top_level_categories.put(widget_key_meistgesehen, meist_gesehen_api);
        top_level_categories.put(widget_key_tipps, tipps_api);

        // Setup Episode - Widgets
        episode_widgets = new LinkedHashMap<>();
        episode_widgets.put(widget_key_aktuellste, aktuellste_api);
        episode_widgets.put(widget_key_meistgesehen, meist_gesehen_api);
        episode_widgets.put(widget_key_tipps, tipps_api);
        episode_widgets.put(widget_key_weitere, weitere_beitraege_api);
    }

    @Override
    public LiveStreamM3U8 getLiveStream() {
        String url = "";
        switch (title) {
            // ZDF Sender
            case Constants.TITLE_CHANNEL_ZDF: url = live_m3u8_zdf; break;
            case Constants.TITLE_CHANNEL_PHOENIX: url = live_m3u8_phoenix; break;
            case Constants.TITLE_CHANNEL_ZDF_KULTUR: url = live_m3u8_kultur; break;
            case Constants.TITLE_CHANNEL_ZDF_INFO: url = live_m3u8_info; break;
            case Constants.TITLE_CHANNEL_3SAT: url = live_m3u8_3sat; break;
            case Constants.TITLE_CHANNEL_ZDF_NEO: url = live_m3u8_neo; break;
        }
        return url.isEmpty() ? null : new LiveStreamM3U8(url);
    }

    @Override
    @Nullable
    public Episode getCurrentEpisode() {
        String url = getLiveEpgUrl();
        Episode episode = EpgUtils.getZDFLiveEpisode(url, 0);
        if (episode != null)
            episode.setThumb_url(getLiveThumbnail());
        return episode;
    }

    @Override
    public ArrayList<Series> fetchAllSeries(int rangeStart, int last) {
        int intervall = 5;

        ArrayList<Series> series = new ArrayList<>();
        ArrayList<Series> temp = new ArrayList<>();

        while (temp != null && rangeStart <= last) {
            String characterRangeStart = String.valueOf((char) rangeStart);
            String characterRangeEnd = String.valueOf((char) (rangeStart + intervall));

            String url =  all_series_api + "?characterRangeStart=" + characterRangeStart
                    + "&characterRangeEnd=" + characterRangeEnd + "&detailLevel=2&maxLength=100";

            temp = ZdfUtils.fetchSendungList(url);
            series.addAll(temp);

            rangeStart += intervall;
        }

        return  series;
    }

    public ArrayList<Episode> fetchEpisodes(String url) {
        try {
            return ZdfUtils.fetchVideoList(url);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public ArrayList<Episode> fetchCategoryEpisodes(String key, int limit, int offset) {
        if (title.equals(Constants.LIVE_STREAM_CHANNEL_3SAT )|| title.equals(Constants.TITLE_CHANNEL_PHOENIX))
            return null;

        String url = top_level_categories.get(key);
        url += "?maxLength=" + limit + "&id=" + getTopLevelId(key) + "&offset=" + offset;
        ArrayList<Episode> episodes = fetchEpisodes(url);
        DataUtils.filterByStation(episodes, title);
        return fetchEpisodes(url);
    }

    @Override
    public ArrayList<Episode> fetchWidgetEpisodes(String key, String assetId, int count) {
        if (assetId == null)
            assetId = getTopLevelId(key);

        ArrayList<Episode> episodes = new ArrayList<>();
        int dCount = 2 * count;
        int offset = 0;

        String baseUrl = episode_widgets.get(key);
        baseUrl += "?maxLength=" + dCount + "&id=" + assetId + "&offset=";

        while (episodes.size() < count) {
            String url = baseUrl + offset;

            ArrayList<Episode> temp = fetchEpisodes(url);

            if (temp != null && !temp.isEmpty()) {
                DataUtils.filterByStation(temp, title);
                episodes.addAll(temp);
                offset += dCount;
            }
            else
                break;
        }

        return episodes;
    }

    @Override
    public ArrayList<Series> fetchWidgetSeries(String mHeaderTitle, String mAssetId, int videoItemCount) {
        return null;
    }

    @Override
    public ArrayList<Episode> fetchSeriesEpisodes(String assetId, int count, int offset) {
        return null;
    }

    private String getTopLevelId(String key) {
        switch (title) {
            case Constants.TITLE_CHANNEL_ZDF_NEO:
                return station_id_neo;
            case Constants.TITLE_CHANNEL_ZDF_KULTUR:
                return station_id_kultur;
            case Constants.TITLE_CHANNEL_ZDF_INFO:
                return station_id_info;
            default:
                return widget_key_meistgesehen.equals(key) ? top_level_id_most : top_level_id;
        }
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

    public ArrayList<Episode> getMostRecentEpisodes(int offset, int limit, int assetId) {
        String request = aktuellste_api + "?maxLength=" + limit
                + "&offset=" + offset + "&id=" + assetId;
        try {
            return ZdfUtils.fetchVideoList(request);
        } catch (IOException e) {
            return null;
        }
    }


    @Override
    @Nullable
    public ArrayList<Episode> getMostRecentEpisodes(int offset, int limit, Calendar startDate, Calendar endDate) {
        String url = most_recent_api
                + "maxLength=" + limit + "&offset=" + offset;

        String start = FormatTime.zdfCalendarToRecentRequest(startDate);
        if (start != null)
            url += "&startdate=" + start;

        String end = FormatTime.zdfCalendarToRecentRequest(startDate);
        if (end != null)
            url += "&enddate=" + end;

        try {
            ArrayList<Episode> episodes = ZdfUtils.fetchVideoList(url);
            Collections.reverse(episodes);
            return episodes;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getStationId() {
        return null;
    }

    @Nullable
    private String getLiveThumbnail(Element el) {
        try {
            String thumb = el.select("teaserimage[key=485x273]").text();
            String channelName = XmlParser.getStringByTag(el, "channel");
            return title.equals(channelName) ? thumb : null;
        } catch (NullPointerException ignored) {
        }
        return null;
    }

    @Nullable
    private String getLiveThumbnail() {
        try {
            Document d = Jsoup.connect(live_stream_api).get();
            Elements elements = d.getElementsByTag("teaser");
            for (Element el : elements.select("teaser[member=onAir]")) {
                String thumb = getLiveThumbnail(el);
                if (thumb != null)
                    return thumb;
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public String getLiveEpgUrl () {
        String url = live_epg_api;
        switch (title) {
            // ZDF Sender
            case Constants.TITLE_CHANNEL_ZDF: url += live_epg_ZDF_key_key; break;
            case Constants.TITLE_CHANNEL_PHOENIX: url += live_epg_phoenix_key; break;
            case Constants.TITLE_CHANNEL_ZDF_KULTUR: url += live_epg_kultur_key; break;
            case Constants.TITLE_CHANNEL_ZDF_INFO: url += live_epg_info_key; break;
            case Constants.TITLE_CHANNEL_3SAT: url += live_epg_3sat_key; break;
            case Constants.TITLE_CHANNEL_ZDF_NEO: url += live_epg_neo_key; break;
            default: return null;
        }
        return url + "/now/json";
    }
}
