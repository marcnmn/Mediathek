package com.marcn.mediathek.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.LiveStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class HTMLParser {

    @Nullable
    public static LiveStream arteLiveStreamData(Context c, LiveStream l) {
        if (c == null) return null;
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
