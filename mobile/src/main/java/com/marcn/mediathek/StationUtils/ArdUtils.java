package com.marcn.mediathek.StationUtils;

import android.annotation.SuppressLint;

import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.stations.ArdGroup;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.FormatTime;
import com.marcn.mediathek.utils.NetworkTasks;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

public class ArdUtils {

    public static Episode getCurrentEpisode(ArdGroup station, String api, String api_thumb) {
        try {
            Document doc = Jsoup.connect(api + "?kanal=Alle").get();
            String data = doc.select("a[href=/tv/" + station.getLiveQueryString() + "/live?kanal=" + station.getStationId()
                    + "].textlink").select("p.subtitle").text();
            Episode e = htmlToEpisode(data);

            if (e == null) return null;
            e.setStationTitle(station.getTitle());

            String json = doc.select("a[href=/tv/" + station.getLiveQueryString() + "/live?kanal=" + station.getStationId()
                    + "].medialink").select("img.img.hideOnNoScript").attr("data-ctrl-image");
//                JSONObject j = new JSONObject(json);
            String thumb = parseNoScriptThumb(json);
            if (thumb != null)
                e.setThumb_url(thumb);

            try {
                Document doc2 = Jsoup.connect(api + "?kanal=" + station.getStationId()).get();
                Element element = doc2.select("div.section.onlyWithJs.sectionA").first();
                element = element.select("div.mod.modA.modProgramm").first();
                String test = element.select("p.teasertext").text();
                e.setDescription(test);
            } catch (NullPointerException ignored) {
            }

            return e;
        } catch (IOException ignored) {
            return null;
        }
    }

    public static ArrayList<Episode> fetchEpisodeList(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            ArrayList<Episode> episodes = new ArrayList<>();
            Elements elements = doc.select("div.box.flash.pd.hls");
            for (Element e : elements) {
                Episode ep = catHtmlToEpisode(e);
                if (ep != null) {
                    ep.setStationTitle(Constants.TITLE_CHANNEL_ARD);
                    episodes.add(ep);
                }
            }
            return episodes;
        } catch (IOException ignored) {
            return null;
        }
    }

    public static TreeMap<Integer, String> getVideoUrl(String assetId) {
        String url = "http://www.ardmediathek.de/play/media/"
                + assetId + "?devicetype=pc&features=flash";

        TreeMap<Integer, String> urls = new TreeMap<>();
        try {
            JSONObject j = NetworkTasks.downloadJSONData(url);
            String autoUrl = j.getJSONArray("_mediaArray")
                    .getJSONObject(0)
                    .getJSONArray("_mediaStreamArray")
                    .getJSONObject(0)
                    .getString("_stream");
            urls.put(Constants.QUALITY_AUTO, autoUrl);
        } catch (JSONException e) {
            return null;
        }

        return urls;
    }

    private static Episode htmlToEpisode(String data) {
        if (data == null || data.length() < 14) return null;
        String time = data.substring(0, 13);
        String title = data.substring(14);
        Episode episode = new Episode(title);
        updateEpisodeTime(episode, time);
        return episode;
    }

    private static Episode catHtmlToEpisode(Element e) {
        String title = e.select("h4.headline").text();
        if (title == null) return null;

        String id = e.select("a.textLink").attr("href");
        if (id == null) return null;

        id = id.substring(id.lastIndexOf("=") + 1);

        Episode episode = new Episode(title, null, id);

        String time = e.select("p.subtitle").text();
        if (time != null)
            catSetEpisodeTime(episode, time);



        String thumb = e.select("img.hideOnNoScript").attr("data-ctrl-image");
        thumb = parseNoScriptThumb(thumb);
        if (thumb != null)
            episode.setThumb_url(thumb);

        return episode;
    }

    private static void updateEpisodeTime(Episode e, String data) {
        String split[] = data.split(" · ");
        try {
            Calendar from = FormatTime.parseArdString(split[0]);
            Calendar until = FormatTime.parseArdString(split[1]);
            if (from.getTimeInMillis() > until.getTimeInMillis())
                until.add(Calendar.DAY_OF_YEAR, 1);
            long length = until.getTimeInMillis() - from.getTimeInMillis();
            e.setStartTime(from);
            e.setEpisodeLengthInMs(length);
//            length = 16339367;
            int remainingMinutes = FormatTime.remainingMinutes(from, length);
            e.setRemainingTime(remainingMinutes);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static void catSetEpisodeTime(Episode e, String data) {
        String split[] = data.split(" ");
        if (split.length < 3) return;
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat s = new SimpleDateFormat("dd.MM.yyyy");
            calendar.setTime(s.parse(split[0]));

            e.setStartTime(calendar);
            e.setEpisodeLengthInMs(Long.parseLong(split[2]) * 60000);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    private static String parseNoScriptThumb(String s) {
        try {
            JSONObject j = new JSONObject(s);
            String thumb = j.getString("urlScheme");
            if (thumb != null && !thumb.isEmpty() && thumb.indexOf("#") > 0)
                return ArdGroup.ARD_BASE_URL + thumb.substring(0, thumb.indexOf("#")) + Constants.SIZE_THUMB_MEDIUM_X;
        } catch (JSONException ignored) {
        }
        return null;
    }
}
