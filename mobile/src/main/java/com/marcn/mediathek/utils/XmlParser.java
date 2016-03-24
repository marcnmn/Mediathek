package com.marcn.mediathek.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.stations.Station;
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
    public static ArrayList<Video> getZDFLiveStreamData2(Context c, ArrayList<Video> ls) throws IOException {
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
    public static Video getLivestreamFromChannel(Context context, Station station) {
        Video video = null;
        switch (station.getTitle()) {
            case Constants.TITLE_CHANNEL_ARD:
                video = ardLiveStreamsData(context, station);
                break;
            case Constants.TITLE_CHANNEL_ARTE:
                video = arteLiveStreamData(context, station);
                break;
            default:
                video = getZDFLiveStreamData(context, station);
                break;
        }
        return video;
    }

    @Nullable
    public static Video getZDFLiveStreamData(Context context, Station station) {
        String url = context.getString(R.string.zdf_live_api);
        return getZDFLiveStreamData(url, station.getTitle());
    }

    @Nullable
    public static Video getZDFLiveStreamData(String url, String titie) {
        Document d;
        try {
            d = Jsoup.connect(url).get();
        } catch (IOException e) {
            return null;
        }

        if (d == null || titie == null)
            return null;

        Video video = null;
        Elements elements = d.getElementsByTag("teaser");
        for (Element el : elements.select("teaser[member=onAir]")) {
            try {
                String detail = getStringByTag(el, "detail");
                String thumb = el.select("teaserimage[key=485x273]").text();
                String title = getStringByTag(el, "title");
                String channelName = getStringByTag(el, "channel");
                String assetId = getStringByTag(el, "assetId");
                int originChannelId = getIntegerByTag(el, "originChannelId");

                if (titie.equals(channelName)) {
                    video = new Video(assetId, channelName, originChannelId);
                    video.detail = detail;
                    video.thumb_url = thumb;
                    video.title = title;
                }
            } catch (NullPointerException ignored) {
            }
        }
        return video;
    }

    public static Video arteLiveStreamData(Context c, Station station) {
        if (c == null || station == null) return null;
        String url = c.getString(R.string.arte_live_api);

        Video video = new Video("6", c.getString(R.string.arte_name), Video.ARTE_GROUP);
        try {
            Document doc = Jsoup.connect(url).get();
            String thumbnail = doc.select("div.video-block.has-play > img").attr("src");
            video.setThumb_url(thumbnail);
            video.stationObject = station;
            return video;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Video arteLiveStreamData(Context c, Video l) {
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

    public static Video ardLiveStreamsData(Context c, Station station) {
        if (c == null) return null;
        return null;
//        Video liveStream = station.getLiveStream();
//        String url = c.getString(R.string.ard_live_api);
//        String image_url = c.getString(R.string.ard_live_image_api);
//        try {
//            Document doc = Jsoup.connect(url).get();
//            String thumb;
//            try {
//                String json = doc.select("a[href=/tv/" + liveStream.queryName + "/live?kanal=" + liveStream.id + "].medialink").select("img.img.hideOnNoScript").attr("data-ctrl-image");
//                JSONObject j = new JSONObject(json);
//                thumb = j.getString("urlScheme");
//            } catch (JSONException e) {
//                return null;
//            }
//            if (thumb != null && !thumb.isEmpty() && thumb.indexOf("#") > 0) {
//                thumb = thumb.substring(0, thumb.indexOf("#"));
//                liveStream.setThumb_url(image_url + thumb + "384");
//            }
//            return liveStream;
//        } catch (IOException ignored) {
//            return liveStream;
//        }
    }

    public static ArrayList<Video> ardLiveStreamsData(Context c, ArrayList<Video> ls) {
        if (c == null) return null;
        String url = c.getString(R.string.ard_live_api);
        String image_url = c.getString(R.string.ard_live_image_api);
        try {
            Document doc = Jsoup.connect(url).get();
            for (Video l : ls) {
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
                    l.setThumb_url(image_url + thumb + Constants.SIZE_THUMB_MEDIUM_X);
                }
            }
            return ls;
        } catch (IOException ignored) {
            return ls;
        }
    }

    public static String getStringByTag(Element el, String tag) {
        Elements e = el.getElementsByTag(tag);
        if (e == null || e.isEmpty()) return "";
        Element target = e.get(0);
        if (target == null || !target.hasText()) return "";
        return target.text();
    }

    public static int getIntegerByTag(Element el, String tag) {
        String s = getStringByTag(el, tag);
        try {
            int i = Integer.parseInt(s);
            return i;
        } catch (Exception e) {
            return -1;
        }
    }
}
