package com.marcn.mediathek.utils;

import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.Series;

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

    public static void sortSeriesByName(ArrayList<Series> data) {
        Collections.sort(data, new Comparator<Series>() {
            @Override
            public int compare(Series lhs, Series rhs) {
                if (lhs.member == null && rhs.member == null) return 0;
                if (lhs.member == null) return -1;
                if (rhs.member == null) return 1;

                return lhs.member.compareTo(rhs.member);
//                int a = lhs.member.charAt(0);
//                int b = rhs.member.charAt(0);
//                return Integer.compare(a, b);
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

    public static boolean episodeListsAreEqual(ArrayList<Episode> a, ArrayList<Episode> b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            String aId = a.get(i).getAssetId();
            String bId = b.get(i).getAssetId();
            if (!aId.equals(bId)) return false;
        }
        return true;
    }
}
