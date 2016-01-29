package com.marcn.mediathek.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateFormat {

    @SuppressLint("SimpleDateFormat")
    public static String calendarToHeadlineFormat(Calendar c) {
        SimpleDateFormat s = new SimpleDateFormat("EEEE, MMMM d");
        return s.format(c.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static Calendar zdfAirtimeStringToDate(String airtime) {
        String day = airtime.split(" ")[0];
        SimpleDateFormat format  = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = format.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
