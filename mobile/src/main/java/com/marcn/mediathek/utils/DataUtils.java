package com.marcn.mediathek.utils;

import com.google.android.exoplayer.MediaFormat;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.Series;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class DataUtils {

    public static String[] getFormatNames(ArrayList<MediaFormat> formats) {
        String[] qualites = new String[formats.size()];
        for (int i = 0; i < formats.size(); i++) {
            if (formats.get(i).height <= 0) {
                qualites[i] = "auto";
            } else {
                qualites[i] = formats.get(i).height + "p";
            }
        }
        return qualites;
    }

    public static ArrayList<MediaFormat> filterRedundantTracks(ArrayList<MediaFormat> input) {
        ArrayList<MediaFormat> tracks = new ArrayList<>(input);
        Iterator iter = tracks.listIterator();
        while (iter.hasNext()) {
            MediaFormat format = (MediaFormat) iter.next();
            if (format != null && isRedundant(format, tracks)) {
                iter.remove();
            }
        }
        return tracks;
    }

    private static boolean isRedundant(MediaFormat format, ArrayList<MediaFormat> tracks) {
        for (MediaFormat mediaFormat : tracks) {
            if (mediaFormat != format && format.height == mediaFormat.height
                    && format.bitrate <= mediaFormat.bitrate) {
                return true;
            }
        }
        return false;
    }


    public static void filterByStation(ArrayList<Episode> data, String title) {
        if (title == null) return;
        Iterator iter = data.listIterator();

        while (iter.hasNext()) {
            Episode e = (Episode) iter.next();
            if (e != null && !title.equals(e.getStationTitle()))
                iter.remove();
        }
    }

    public static void searchSeriesList(ArrayList<Series> data, String query) {
        Iterator iter = data.listIterator();
        if (query == null) return;

        while (iter.hasNext()) {
            Series e = (Series) iter.next();
            String title = e.title;

            if (title == null) {
                e.setHidden(true);
                continue;
            }
            if (query.isEmpty()) {
                e.setHidden(false);
                continue;
            }

            if (stringContains(title, query))
                e.setHidden(false);
            else
                e.setHidden(true);
        }
    }

    public static void filterByHidden(ArrayList<Series> data) {
        Iterator iter = data.listIterator();

        while (iter.hasNext()) {
            Series e = (Series) iter.next();
            if (e.isHidden()) iter.remove();
        }
    }

    public static void filterGanze(ArrayList<Episode> data) {
        Iterator iter = data.listIterator();

        while (iter.hasNext()) {
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
            }
        });
    }

    public static void addNameHeaders(ArrayList<Series> data) {
        if (data == null || data.isEmpty()) return;

        int index = 0;
        String lastMember = null;

        while (index < data.size()) {
            String member = data.get(index).member;
            if (lastMember == null || !member.equals(lastMember))
                data.add(index, Series.createSendungHeader(member));
            lastMember = member;
            index++;
        }
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
//                    result.add(index, Asset.createHeader(FormatTime.calendarToHeadlineFormat(lastCalendar)));
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

    private static boolean stringContains(String data, String query) {
        if (data == null) return false;
        data = data.toLowerCase();
        if (query == null || query.isEmpty()) return true;
        query = query.toLowerCase();

        String[] qArray = query.split(" ");
        for (String q : qArray)
            if (!data.contains(q))
                return false;
        return true;

//        return data.contains(query);

//        String[] array = data.split(" ");

//        for (String s : array) {
//            for (String q : qArray)
//                if (s.startsWith(q))
//                    return true;
//        }


    }
}
