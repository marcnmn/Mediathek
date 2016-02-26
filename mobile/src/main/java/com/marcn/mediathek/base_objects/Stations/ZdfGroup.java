package com.marcn.mediathek.base_objects.Stations;

import android.support.annotation.Nullable;

import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.EpgUtils;
import com.marcn.mediathek.utils.NetworkTasks;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ZdfGroup extends Station {
    public static final String LIVE_STREAM_ZDF = "http://zdf1314-lh.akamaihd.net/i/de14_v1@392878/index_3056_av-p.m3u8?sd=10&amp;dw=0&amp;rebase=on&amp;hdntl=";
    public static final String LIVE_STREAM_PHOENIX = "http://zdf0910-lh.akamaihd.net/i/de09_v1@392871/master.m3u8";
    public static final String LIVE_STREAM_ZDF_KULTUR = "http://zdf1112-lh.akamaihd.net/i/de11_v1@392881/master.m3u8?dw=0";
    public static final String LIVE_STREAM_ZDF_INFO = "http://zdf1112-lh.akamaihd.net/i/de12_v1@392882/master.m3u8?dw=0";
    public static final String LIVE_STREAM_3SAT = "http://zdf0910-lh.akamaihd.net/i/dach10_v1@392872/master.m3u8?dw=0";
    public static final String LIVE_STREAM_ZDF_NEO = "http://zdf1314-lh.akamaihd.net/i/de13_v1@392877/master.m3u8?dw=0";

    public static final String LIVE_STREAM_EPG_URL = "http://sofa01.zdf.de/epgservice/";

    public static final String LIVE_STREAM_EPG_ZDF_NAME = "zdf";
    public static final String LIVE_STREAM_EPG_PHOENIX_NAME = "phoenix";
    public static final String LIVE_STREAM_EPG_ZDF_KULTUR_NAME = "zdfkultur";
    public static final String LIVE_STREAM_EPG_ZDF_INFO_NAME = "zdfinfo";
    public static final String LIVE_STREAM_EPG_3SAT_NAME = "3sat";
    public static final String LIVE_STREAM_EPG_ZDF_NEO_NAME = "zdfneo";

    private static final String live_stream_api = "http://www.zdf.de/ZDFmediathek/xmlservice/web/live?maxLength=6";

    public ZdfGroup(String title) {
        super(title);
    }

    @Override
    public LiveStreamM3U8 getLiveStream() {
        String url = "";
        switch (title) {
            // ZDF Sender
            case Constants.TITLE_CHANNEL_ZDF: url = LIVE_STREAM_ZDF; break;
            case Constants.TITLE_CHANNEL_PHOENIX: url = LIVE_STREAM_PHOENIX; break;
            case Constants.TITLE_CHANNEL_ZDF_KULTUR: url = LIVE_STREAM_ZDF_KULTUR; break;
            case Constants.TITLE_CHANNEL_ZDF_INFO: url = LIVE_STREAM_ZDF_INFO; break;
            case Constants.TITLE_CHANNEL_3SAT: url = LIVE_STREAM_3SAT; break;
            case Constants.TITLE_CHANNEL_ZDF_NEO: url = LIVE_STREAM_ZDF_NEO; break;
        }
        return url.isEmpty() ? null : new LiveStreamM3U8(url);
    }

    @Override
    @Nullable
    public Episode getCurrentEpisode() {
        String url = getLiveEpgUrl();
        Episode episode = EpgUtils.getZDFLiveEpisode(url, 0);

//        if (episode != null)
//            episode.setThumb_url(getLiveThumbnail());

        return episode;
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

    @Override
    public String getCategoryUrl(String key, int offset, int limit) {
        return null;
    }

    public String getLiveEpgUrl () {
        String url = LIVE_STREAM_EPG_URL;
        switch (title) {
            // ZDF Sender
            case Constants.TITLE_CHANNEL_ZDF: url += Constants.LIVE_STREAM_EPG_ZDF_NAME; break;
            case Constants.TITLE_CHANNEL_PHOENIX: url += Constants.LIVE_STREAM_EPG_PHOENIX_NAME; break;
            case Constants.TITLE_CHANNEL_ZDF_KULTUR: url += Constants.LIVE_STREAM_EPG_ZDF_KULTUR_NAME; break;
            case Constants.TITLE_CHANNEL_ZDF_INFO: url += Constants.LIVE_STREAM_EPG_ZDF_INFO_NAME; break;
            case Constants.TITLE_CHANNEL_3SAT: url += Constants.LIVE_STREAM_EPG_3SAT_NAME; break;
            case Constants.TITLE_CHANNEL_ZDF_NEO: url += Constants.LIVE_STREAM_EPG_ZDF_NEO_NAME; break;
            case Constants.TITLE_CHANNEL_ARTE: url += Constants.LIVE_STREAM_EPG_ARTE_NAME; break;
            default: return null;
        }
        return url + "/now/json";
    }
}
