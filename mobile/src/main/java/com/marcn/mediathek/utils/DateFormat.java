package com.marcn.mediathek.utils;

import android.content.Context;

import com.marcn.mediathek.base_objects.Channel;
import com.marcn.mediathek.base_objects.Video;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DateFormat {

    public static String calendarToHeadlineFormat(Calendar c) {
        SimpleDateFormat s = new SimpleDateFormat("EEEE, MMMM d");
        return s.format(c.getTime());
    }
}
