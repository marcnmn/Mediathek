package com.marcn.mediathek.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateFormat {

    public static String calendarToHeadlineFormat(Calendar c) {
        SimpleDateFormat s = new SimpleDateFormat("EEEE, MMMM d");
        return s.format(c.getTime());
    }
}
