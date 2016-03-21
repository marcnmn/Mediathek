package com.marcn.mediathek.StationUtils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.stations.Station;
import com.marcn.mediathek.utils.FormatTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;


public class ZdfUtils {
    public static final int QUALiTY_VERY_HIGH = 0;
    public static final int QUALiTY_HIGH = 1;
    public static final int QUALiTY_MED = 2;
    public static final int QUALiTY_LOW = 3;

    public static ArrayList<Episode> getMissedShows(Context c, int offset, int count,
                                                    String startDate, String endDate) {
        String url =  c.getString(R.string.zdf_gruppe_sendung_verpasst)
                + "?maxLength=" + count + "&offset=" + offset
                + "&startdate=" + startDate + "&enddate=" + endDate;
        try {
            ArrayList<Episode> episodes = fetchVideoList(url);
            Collections.reverse(episodes);
            return episodes;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static ArrayList<Series> getAllShows(String url) {
        return fetchSendungList(url);
    }

    public static ArrayList<Series> getAllShows(Context c, int rangeStart, int rangeEnd) {
        String characterRangeStart = String.valueOf((char) rangeStart);
        String characterRangeEnd = String.valueOf((char) rangeEnd);

        String url =  c.getString(R.string.zdf_gruppe_sendungen_abisz)
                + "?characterRangeStart=" + characterRangeStart + "&characterRangeEnd=" + characterRangeEnd
                + "&detailLevel=2&maxLength=100";
        return fetchSendungList(url);
    }

    public static ArrayList<Episode> fetchVideoList(String url) throws IOException {
        Document d = Jsoup.connect(url).get();

        ArrayList<Episode> episodes = new ArrayList<>();
        if (d == null)
            return episodes;

        Elements statusCode = d.getElementsByTag("statuscode");
        if (statusCode == null || !statusCode.text().equals("ok"))
            return episodes;

        Elements elements = d.getElementsByTag("teaser");
        for (Element el: elements) {
            try {
                String title = getSingleStringByTag(el, "title");
                String channel = getSingleStringByTag(el, "channel");
                String assetId = getSingleStringByTag(el, "assetId");
                Station station = Station.createStation(channel);
                Episode v = new Episode(title, station, assetId);

                String detail = getSingleStringByTag(el, "detail");
                v.setDescription(detail);
                String thumb_url = getThumbUrl(el, "teaserimage");
                v.setThumb_url(thumb_url);
                String airtime = getSingleStringByTag(el, "airtime");
                v.setStartTime(FormatTime.zdfAirtimeStringToDate(airtime));

                String vcmsUrl = getSingleStringByTag(el, "vcmsUrl");
                v.setBrowserUrl(vcmsUrl);
                int originChannelId = getSingleIntegerByTag(el, "originChannelId");
                int lengthSec = getSingleIntegerByTag(el, "lengthSec");
                v.setEpisodeLengthInMs(lengthSec * 1000);

                String nurOnline = getSingleStringByTag(el, "nurOnline");
                boolean nO = nurOnline != null && nurOnline.equals("true");
                v.setNurOnline(nO);
                String onlineFassung = getSingleStringByTag(el, "onlineFassung");
//                v.setOnlineFassung(onlineFassung);
                String ganzeSendung = getSingleStringByTag(el, "ganzeSendung");
                boolean gS = ganzeSendung != null && ganzeSendung.equals("true");
                v.setGanzeSendung(gS);
                String originChannelTitle = getSingleStringByTag(el, "originChannelTitle");

                episodes.add(v);
            } catch (NullPointerException ignored){}
        }
        return episodes;
    }

    @Nullable
    public static ArrayList<Series> fetchSendungList(String url) {
        Document d;
        try {
            d = Jsoup.connect(url).get();
        } catch (IOException e) {
            return null;
        }
        String member = "";

        ArrayList<Series> sendungen = new ArrayList<>();
        if (d == null)
            return sendungen;

        Elements statusCode = d.getElementsByTag("statuscode");
        if (statusCode == null || !statusCode.text().equals("ok"))
            return sendungen;

        //sendungen.add(Series.createSendungHeader(character));

        Elements elements = d.getElementsByTag("teaser");
        for (Element el: elements) {
            try {
//                if (!member.equals(el.attr("member"))) {
//                    member = el.attr("member");
//                    sendungen.add(Series.createSendungHeader(member));
//                }

                member = el.attr("member");
                String title = getSingleStringByTag(el, "title");
                String shortTitle = getSingleStringByTag(el, "shortTitle");
                String detail = getSingleStringByTag(el, "detail");
                String thumb_url_low = el.select("teaserimage[key=144x81]").text();
                String thumb_url_high = el.select("teaserimage[key=946x532]").text();
                String channel = getSingleStringByTag(el, "channel");
                String vcmsUrl = getSingleStringByTag(el, "vcmsUrl");
                int assetId = getSingleIntegerByTag(el, "assetId");

                Series series = new Series(title, shortTitle, detail,
                        thumb_url_low, thumb_url_high, channel, vcmsUrl, assetId, member);
                sendungen.add(series);
            } catch (NullPointerException ignored){}
        }
        return sendungen;
    }

    @Nullable
    public static TreeMap<Integer, String> getVideoUrl(Context c, String id) throws IOException{
        String url =  c.getString(R.string.zdf_gruppe_video) + "?id=" + id;
        Document d = Jsoup.connect(url).get();

        Elements statusCode = d.getElementsByTag("statuscode");
        if (statusCode == null || !statusCode.text().equals("ok"))
            return null;

        TreeMap<Integer, String> urls = new TreeMap<>();
        Elements elements = d.select("formitaet[basetype=h264_aac_ts_http_m3u8_http]");
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
