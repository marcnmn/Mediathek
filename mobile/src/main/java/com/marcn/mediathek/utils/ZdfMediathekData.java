package com.marcn.mediathek.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Video;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;


public class ZdfMediathekData {

    public static final int QUALiTY_VERY_HIGH = 0;
    public static final int QUALiTY_HIGH = 1;
    public static final int QUALiTY_MED = 2;
    public static final int QUALiTY_LOW = 3;

    // LiveStreams
    public static ArrayList<Video> getMissedShows(Context c, int offset, int count,
                                                  String startDate, String endDate) throws IOException {

        String url =  c.getString(R.string.zdf_gruppe_sendung_verpasst)
                + "?maxLength=" + count + "&offset=" + offset
                + "&startdate=" + startDate + "&enddate=" + endDate;
        Document d = Jsoup.connect(url).get();

        ArrayList<Video> videos = new ArrayList<>();
        if (d == null)
            return videos;

        Elements statusCode = d.getElementsByTag("statuscode");
        if (statusCode == null || !statusCode.text().equals("ok"))
            return videos;

        Elements elements = d.getElementsByTag("teaser");
        for (Element el: elements) {
            try {
                String title = getSingleStringByTag(el, "title");
                String detail = getSingleStringByTag(el, "detail");
                String thumb_url = getThumbUrl(el, "teaserimage");
                String channel = getSingleStringByTag(el, "title");
                String airtime = getSingleStringByTag(el, "airtime");
                String vcmsUrl = getSingleStringByTag(el, "vcmsUrl");
                int assetId = getSingleIntegerByTag(el, "assetId");
                int originChannelId = getSingleIntegerByTag(el, "originChannelId");
                int lengthSec = getSingleIntegerByTag(el, "lengthSec");

                String nurOnline = getSingleStringByTag(el, "nurOnline");
                String onlineFassung = getSingleStringByTag(el, "onlineFassung");
                String ganzeSendung = getSingleStringByTag(el, "ganzeSendung");
                String originChannelTitle = getSingleStringByTag(el, "originChannelTitle");

                Video v = new Video(title, detail, thumb_url, channel, airtime, vcmsUrl,
                        assetId, originChannelId, lengthSec, nurOnline, onlineFassung,
                        ganzeSendung, originChannelTitle);
                videos.add(v);
            } catch (NullPointerException ignored){}
        }
        Collections.reverse(videos);
        return videos;
    }

    @Nullable
    public static TreeMap<Integer, String> getVideoUrl(Context c, int id) throws IOException{
        String url =  c.getString(R.string.zdf_gruppe_video) + "?id=" + id;
        Document d = Jsoup.connect(url).get();

        Elements statusCode = d.getElementsByTag("statuscode");
        if (statusCode == null || !statusCode.text().equals("ok"))
            return null;

        TreeMap<Integer, String> urls = new TreeMap<>();
        Elements elements = d.select("formitaet[basetype=vp8_vorbis_webm_http_na_na]");
        for (Element el: elements) {
            try {
                int quality = getQualityType(getSingleStringByTag(el, "quality"));
                String qUrl = getSingleStringByTag(el, "url");
                urls.put(quality, qUrl);
            } catch (NullPointerException ignored){}
        }
        return urls;
    }

    private static String getSingleStringByTag(Element el, String tag) {
        Elements e = el.getElementsByTag(tag);
        if (e == null || e.isEmpty()) return "";
        Element target = e.get(0);
        if (target == null || !target.hasText()) return "";
        return target.text();
    }

    private static int getSingleIntegerByTag(Element el, String tag) {
        String s = getSingleStringByTag(el, tag);
        try {
            int i = Integer.parseInt(s);
            return i;
        } catch (Exception e) {
            return -1;
        }
    }

    private static String getThumbUrl(Element el, String tag) {
        Elements e = el.getElementsByTag(tag);
        if (e == null || e.isEmpty()) return "";
        String s = "";
        for (Element element : e) {
            if (!element.attr("alt").isEmpty() && element.attr("key").equals("485x273"))
                s = element.text();
        }
        if (s.isEmpty())
            return el.getElementsByTag(tag).last().text();
        return s;
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
