package com.marcn.mediathek.utils;

import com.marcn.mediathek.base_objects.Video;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class BaseListUtilities {

    public static ArrayList<Video> addHeaders(ArrayList<Video> videos) {
        Calendar lastCalendar = null;
        Calendar calendar;

        ArrayList<Video> result = new ArrayList<>();
        //clean list
        for (Video v : videos)
            if (!v.isHeader) result.add(v);

        Collections.sort(result, new Comparator<Video>() {
            @Override
            public int compare(Video lhs, Video rhs) {
                Calendar dLhs = DateFormat.zdfAirtimeStringToDate(lhs.airtime);
                Calendar dRhs = DateFormat.zdfAirtimeStringToDate(rhs.airtime);
                return dRhs.compareTo(dLhs);
            }
        });

        int index = 0;
        int size = result.size();

        Video video;
        while (index < size) {
            video = result.get(index);
            if (index == 0) {
                lastCalendar = DateFormat.zdfAirtimeStringToDate(video.airtime);
                result.add(index, Video.createHeaderVideo(DateFormat.calendarToHeadlineFormat(lastCalendar)));
                index++;
                size++;
            } else if (!video.isHeader && lastCalendar == null) {
                lastCalendar = DateFormat.zdfAirtimeStringToDate(video.airtime);
            } else if (!video.isHeader) {
                calendar = DateFormat.zdfAirtimeStringToDate(video.airtime);
                if (calendar != null && calendar.getTimeInMillis() < lastCalendar.getTimeInMillis()) {
                    lastCalendar = calendar;
                    result.add(index, Video.createHeaderVideo(DateFormat.calendarToHeadlineFormat(lastCalendar)));
                    index++;
                    size++;
                }
            }
            index++;
        }
        return result;
    }
}
