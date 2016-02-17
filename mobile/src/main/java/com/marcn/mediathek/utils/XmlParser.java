package com.marcn.mediathek.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.Station;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.LiveStreams;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class XmlParser {

    // LiveStreams
    public static ArrayList<LiveStream> getZDFLiveStreamData2(Context c, ArrayList<LiveStream> ls) throws IOException {
        String url = c.getString(R.string.zdf_live_api);
        Document d = Jsoup.connect(url).get();

        if (d == null)
            return ls;

        Elements elements = d.getElementsByTag("teaser");
        for (Element el : elements.select("teaser[member=onAir]")) {
            try {
                String detail = el.getElementsByTag("detail").get(0).text();
                String thumb = el.select("teaserimage[key=485x273]").text();
                String title = el.getElementsByTag("title").get(0).text();
                String channel = el.getElementsByTag("channel").get(0).text();

                int index = LiveStreams.indexOfName(ls, channel);
                if (index < 0) continue;

                ls.get(index).setThumb_url(thumb);
//                ls.get(index).setTitle(title);
                ls.get(index).setDescription(detail);
            } catch (NullPointerException ignored) {
            }
        }
        return ls;
    }

    @Nullable
    public static LiveStream getLivestreamFromChannel(Context context, Station station) {
        LiveStream liveStream = null;
        switch (station.title) {
            case Constants.TITLE_CHANNEL_ARD:
                liveStream = ardLiveStreamsData(context, station);
                break;
            case Constants.TITLE_CHANNEL_ARTE:
                liveStream = arteLiveStreamData(context, station);
                break;
            default:
                liveStream = getZDFLiveStreamData(context, station);
                break;
        }
        return liveStream;
    }

    @Nullable
    public static LiveStream getZDFLiveStreamData(Context context, Station station) {
        String url = context.getString(R.string.zdf_live_api);
        Document d;
        try {
            d = Jsoup.connect(url).get();
        } catch (IOException e) {
            return null;
        }

        if (d == null || station == null)
            return null;

        LiveStream liveStream = null;
        Elements elements = d.getElementsByTag("teaser");
        for (Element el : elements.select("teaser[member=onAir]")) {
            try {
                String detail = getStringByTag(el, "detail");
                String thumb = el.select("teaserimage[key=485x273]").text();
                String title = getStringByTag(el, "title");
                String channelName = getStringByTag(el, "channel");
                String assetId = getStringByTag(el, "assetId");
                int originChannelId = getIntegerByTag(el, "originChannelId");

                if (station.title.equals(channelName)) {
                    liveStream = new LiveStream(assetId, channelName, originChannelId);
                    liveStream.detail = detail;
                    liveStream.thumb_url = thumb;
                    liveStream.title = title;
                }
            } catch (NullPointerException ignored) {
            }
        }
        return liveStream;
    }

    public static LiveStream arteLiveStreamData(Context c, Station station) {
        if (c == null || station == null) return null;
        String url = c.getString(R.string.arte_live_api);

        LiveStream liveStream = new LiveStream("6", c.getString(R.string.arte_name), LiveStream.ARTE_GROUP);
        try {
            Document doc = Jsoup.connect(url).get();
            String thumbnail = doc.select("div.video-block.has-play > img").attr("src");
            liveStream.setThumb_url(thumbnail);
            liveStream.stationObject = station;
            return liveStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static LiveStream arteLiveStreamData(Context c, LiveStream l) {
        if (c == null) return l;
        String url = c.getString(R.string.arte_live_api);

        try {
            Document doc = Jsoup.connect(url).get();
            String thumbnail = doc.select("div.video-block.has-play > img").attr("src");
            l.setThumb_url(thumbnail);
            return l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LiveStream ardLiveStreamsData(Context c, Station station) {
        if (c == null) return null;
        LiveStream liveStream = station.getLiveStream();
        String url = c.getString(R.string.ard_live_api);
        String image_url = c.getString(R.string.ard_live_image_api);
        try {
            Document doc = Jsoup.connect(url).get();
            String thumb;
            try {
                String json = doc.select("a[href=/tv/" + liveStream.queryName + "/live?kanal=" + liveStream.id + "].medialink").select("img.img.hideOnNoScript").attr("data-ctrl-image");
                JSONObject j = new JSONObject(json);
                thumb = j.getString("urlScheme");
            } catch (JSONException e) {
                return null;
            }
            if (thumb != null && !thumb.isEmpty() && thumb.indexOf("#") > 0) {
                thumb = thumb.substring(0, thumb.indexOf("#"));
                liveStream.setThumb_url(image_url + thumb + "384");
            }
            return liveStream;
        } catch (IOException ignored) {
            return liveStream;
        }
    }

    public static ArrayList<LiveStream> ardLiveStreamsData(Context c, ArrayList<LiveStream> ls) {
        if (c == null) return null;
        String url = c.getString(R.string.ard_live_api);
        String image_url = c.getString(R.string.ard_live_image_api);
        try {
            Document doc = Jsoup.connect(url).get();
            for (LiveStream l : ls) {
                String thumb;
                try {
                    String json = doc.select("a[href=/tv/" + l.queryName + "/live?kanal=" + l.id + "].medialink").select("img.img.hideOnNoScript").attr("data-ctrl-image");
                    JSONObject j = new JSONObject(json);
                    thumb = j.getString("urlScheme");
                } catch (JSONException e) {
                    continue;
                }
                if (thumb != null && !thumb.isEmpty() && thumb.indexOf("#") > 0) {
                    thumb = thumb.substring(0, thumb.indexOf("#"));
                    l.setThumb_url(image_url + thumb + "384");
                }
            }
            return ls;
        } catch (IOException ignored) {
            return ls;
        }
    }

    private static String getStringByTag(Element el, String tag) {
        Elements e = el.getElementsByTag(tag);
        if (e == null || e.isEmpty()) return "";
        Element target = e.get(0);
        if (target == null || !target.hasText()) return "";
        return target.text();
    }

    private static int getIntegerByTag(Element el, String tag) {
        String s = getStringByTag(el, tag);
        try {
            int i = Integer.parseInt(s);
            return i;
        } catch (Exception e) {
            return -1;
        }
    }
}
