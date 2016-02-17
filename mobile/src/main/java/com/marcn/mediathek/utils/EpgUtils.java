package com.marcn.mediathek.utils;

import com.marcn.mediathek.StationUtils.Ard;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Station;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class EpgUtils {
    public static ArrayList<LiveStream> getLiveStreamEpgNow(ArrayList<LiveStream> ls) {
        for (LiveStream l : ls)
            getLiveStreamEpgNow(l);
        return ls;
    }

    public static LiveStream getLiveStreamEpgNow(LiveStream l) {
        if (l.stationObject == null || l.stationObject.getGroup() < 0) return l;
        try {
            switch (l.stationObject.getGroup()) {
                case Station.ZDF_GOURP:
                    return getZDFLiveStreamEpgNow(l);
                case Station.ARTE_GOURP:
                    return getZDFLiveStreamEpgNow(l);
                case Station.ARD_GOURP:
                    return Ard.getARDLiveStreamEpgNow(l);
            }
        } catch (IOException ignored) {}
        return l;
    }

    public static LiveStream getZDFLiveStreamEpgNow(LiveStream l) throws IOException {
        String url = l.getLiveEPGURL();
        JSONObject json = NetworkTasks.downloadJSONData(url);
        try {
            json = json.getJSONObject("response").getJSONArray("sendungen").getJSONObject(0);
            json = json.getJSONObject("sendung").getJSONObject("value");

            String title = json.getString("titel");
            Episode episode = new Episode(title, l.stationObject);

            String description = ParserUtils.getString(json, "beschreibung");
            episode.setDescription(description);

            String time = json.getString("time");
            String endTime = json.getString("endTime");
            Calendar cTime = FormatTime.zdfEpgStringToDate(time);
            Calendar cEndTime = FormatTime.zdfEpgStringToDate(endTime);
            if (cTime != null && cEndTime != null) {
                episode.setStartTime(cTime);
                long length = cEndTime.getTimeInMillis() - cTime.getTimeInMillis();
                episode.setEpisodeLengthInMs(length);
                episode.setRemainingTime(cTime, length);
            }
            l.setCurrentEpisode(episode);
        } catch (JSONException | NullPointerException ignored) {
        }
        return l;
    }
}
