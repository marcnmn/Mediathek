package com.marcn.mediathek.StationUtils;

import android.support.annotation.Nullable;

import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.FormatTime;
import com.marcn.mediathek.utils.NetworkTasks;
import com.marcn.mediathek.utils.ParserUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeMap;


public class ArteUtils {
    public static final int QUALiTY_VERY_HIGH = 0;
    public static final int QUALiTY_HIGH = 1;
    public static final int QUALiTY_MED = 2;
    public static final int QUALiTY_LOW = 3;

    @Nullable
    public static ArrayList<Episode> fetchVideoList(String url) {
        JSONObject jsonObject = NetworkTasks.downloadJSONData(url);
        if (jsonObject == null)
            return null;

        ArrayList<Episode> episodes = new ArrayList<>();
        try {
            JSONArray array = jsonObject.getJSONArray("programDEList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject j = array.getJSONObject(i);
                String title = ParserUtils.getString(j, "TIT");
                String assetId = ParserUtils.getString(j, "PID");

                Episode e = new Episode(title, null, assetId);
                e.setStationTitle(Constants.TITLE_CHANNEL_ARTE);

                JSONObject jThumb = j.getJSONObject("IMG");
                if (jThumb != null) {
                    e.setThumb_url(ParserUtils.getString(jThumb, "IUR"));
                }

                JSONObject vdo = j.getJSONObject("VDO");
                if (vdo != null) {
                    String airtime = ParserUtils.getString(vdo, "VDA");
                    if (airtime != null)
                        e.setStartTime(FormatTime.arteAirtimeStringToDate(airtime));

                    int duration = ParserUtils.getInt(vdo, "videoDurationSeconds", -1);
                    e.setEpisodeLengthInMs(duration * 1000);
                }
                e.setDescription(ParserUtils.getString(j, "DSS"));
                episodes.add(e);
            }
        } catch (JSONException e) {
            return null;
        }

        return episodes;
    }

    @Nullable
    public static TreeMap<Integer, String> getVideoUrls(String url) {
        JSONObject jsonObject = NetworkTasks.downloadJSONData(url);
        if (jsonObject == null)
            return null;

        TreeMap<Integer, String> urls = new TreeMap<>();
        try {
            jsonObject = jsonObject.getJSONObject("video");
            JSONArray array = jsonObject.getJSONArray("VSR");
            for (int i = 0; i < array.length(); i++) {
                JSONObject j = array.getJSONObject(i);
                String type = ParserUtils.getString(j, "VFO");
                String stream = ParserUtils.getString(j, "VUR");
                if (type != null && type.equals("M3U8"))
                    urls.put(Constants.QUALITY_AUTO, stream);
            }
        } catch (JSONException e) {
            return null;
        }
        return urls;
    }

    public static int getQualityType(String s) {
        switch (s) {
            case "veryhigh": return QUALiTY_VERY_HIGH;
            case "high": return QUALiTY_HIGH;
            case "med": return QUALiTY_MED;
            case "low": return QUALiTY_LOW;
            default: return QUALiTY_LOW;
        }
    }
}
