package com.marcn.mediathek.utils;

import com.marcn.mediathek.base_objects.Episode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class BaseListUtilities {

    public static ArrayList<Episode> addHeaders(ArrayList<Episode> episodes) {
        Calendar lastCalendar = null;
        Calendar calendar;

        ArrayList<Episode> result = new ArrayList<>();
        //clean list
        for (Episode v : episodes)
            if (!v.isHeader()) result.add(v);

        Collections.sort(result, new Comparator<Episode>() {
            @Override
            public int compare(Episode lhs, Episode rhs) {
                Calendar dLhs = FormatTime.zdfAirtimeStringToDate(lhs.getAirTime());
                Calendar dRhs = FormatTime.zdfAirtimeStringToDate(rhs.getAirTime());
                return dRhs.compareTo(dLhs);
            }
        });

        int index = 0;
        int size = result.size();

        Episode episode;
        while (index < size) {
            episode = result.get(index);
            if (index == 0) {
                lastCalendar = FormatTime.zdfAirtimeStringToDate(episode.getAirTime());
                result.add(index, Episode.createHeader(FormatTime.calendarToHeadlineFormat(lastCalendar)));
                index++;
                size++;
            } else if (!episode.isHeader() && lastCalendar == null) {
                lastCalendar = FormatTime.zdfAirtimeStringToDate(episode.getAirTime());
            } else if (!episode.isHeader()) {
                calendar = FormatTime.zdfAirtimeStringToDate(episode.getAirTime());
                if (calendar != null && calendar.getTimeInMillis() < lastCalendar.getTimeInMillis()) {
                    lastCalendar = calendar;
                    result.add(index, Episode.createHeader(FormatTime.calendarToHeadlineFormat(lastCalendar)));
                    index++;
                    size++;
                }
            }
            index++;
        }
        return result;
    }
}
