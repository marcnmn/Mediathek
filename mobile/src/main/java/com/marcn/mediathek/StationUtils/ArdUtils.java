package com.marcn.mediathek.StationUtils;

import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Station;
import com.marcn.mediathek.utils.FormatTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

public class ArdUtils {

    public static LiveStream getARDLiveStreamEpgNow(LiveStream l) {
        String url = "http://www.ardmediathek.de/tv/live";
        try {
            Document doc = Jsoup.connect(url).get();
            String data = doc.select("a[href=/tv/" + l.queryName + "/live?kanal=" + l.id + "].textlink").select("p.subtitle").text();
            l.setCurrentEpisode(parseHtmlSubtitle(l.stationObject, data));
        } catch (IOException ignored) {}
        return l;
    }

    private static Episode parseHtmlSubtitle(Station station, String data) {
        if (station == null || data == null || data.length() < 14) return null;
        String time = data.substring(0, 13);
        String title = data.substring(14);
        Episode episode = new Episode(title, station);
        updateEpisodeTime(episode, time);
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
}
