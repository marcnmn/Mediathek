package com.marcn.mediathek.utils;

import com.marcn.mediathek.base_objects.Episode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class DataUtils {

    public static void filterByStation(ArrayList<Episode> data, String title) {
        if (title == null) return;
        Iterator iter = data.listIterator();

        while(iter.hasNext()){
            Episode e = (Episode) iter.next();
            if (e != null && !title.equals(e.getStationTitle()))
                iter.remove();
        }
    }

    public static void filterGanze(ArrayList<Episode> data) {
        Iterator iter = data.listIterator();

        while(iter.hasNext()){
            Episode e = (Episode) iter.next();
            if (!e.getGanzeSendung())
                iter.remove();
        }
    }

    public static void sortEpisodesDateAsc(ArrayList<Episode> data) {
        Collections.sort(data, new Comparator<Episode>() {
            @Override
            public int compare(Episode lhs, Episode rhs) {
                if (lhs.getStartTime() == null && rhs.getStartTime() == null) return 0;
                if (lhs.getStartTime() == null) return -1;
                if (rhs.getStartTime() == null) return 1;

                return rhs.getStartTime().compareTo(lhs.getStartTime());
            }
        });
    }

    public static ArrayList<Episode> addDateHeaders(ArrayList<Episode> episodes) {
        if (episodes == null || episodes.isEmpty()) return episodes;
//        filterGanze(episodes);

        ArrayList<Episode> result = new ArrayList<>();
        //clean list
        for (Episode v : episodes)
            if (!v.isHeader()) result.add(v);

        int index = 0;
        int size = result.size();

        Calendar lastCalendar = null;
        Calendar calendar;
        while (index < size) {
            Episode episode = result.get(index);
            if (index == 0) {
                lastCalendar = result.get(index).getStartTime();
                Episode.addHeader(result, lastCalendar, index);
                index++;
                size++;
            } else if (!episode.isHeader() && lastCalendar == null) {
                lastCalendar = episode.getStartTime();
            } else if (!episode.isHeader()) {
                calendar = episode.getStartTime();
                if (calendar != null && calendar.get(Calendar.DAY_OF_YEAR) < lastCalendar.get(Calendar.DAY_OF_YEAR)) {
                    lastCalendar = calendar;
                    Episode.addHeader(result, lastCalendar, index);
//                    result.add(index, Episode.createHeader(FormatTime.calendarToHeadlineFormat(lastCalendar)));
                    index++;
                    size++;
                }
            }
            index++;
        }
        return result;
    }
}
