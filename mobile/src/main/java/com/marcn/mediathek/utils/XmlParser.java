package com.marcn.mediathek.utils;

import android.content.Context;

import com.marcn.mediathek.R;
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
        String url =  c.getString(R.string.zdf_live_api);
        Document d = Jsoup.connect(url).get();

        if (d == null)
            return ls;

        Elements elements = d.getElementsByTag("teaser");
        for (Element el: elements.select("teaser[member=onAir]")) {
            try {
                String detail = el.getElementsByTag("detail").get(0).text();
                String thumb = el.select("teaserimage[key=485x273]").text();
                String channel = el.getElementsByTag("title").get(0).text();

                int index = LiveStreams.indexOfName(ls, channel);
                if (index < 0) continue;

                ls.get(index).setThumb_url(thumb);
                ls.get(index).setDescription(detail);
            } catch (NullPointerException ignored){}
        }

        return null;
    }

    public static LiveStream arteLiveStreamData(Context c, LiveStream l) {
        if (c == null) return l;
        String url =  c.getString(R.string.arte_live_api);

        try {
            Document doc = Jsoup.connect(url).get();
            String thumbnail = doc.select("div.video-block.LIVE.has-play > img").attr("src");
            l.setThumb_url(thumbnail);
            return l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
                    String json = doc.select("a[href=/tv/"+l.queryName +"/live?kanal="+l.id+"].medialink").select("img.img.hideOnNoScript").attr("data-ctrl-image");
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
}
