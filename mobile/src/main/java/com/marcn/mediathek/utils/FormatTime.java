package com.marcn.mediathek.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FormatTime {

    @SuppressLint("SimpleDateFormat")
    public static String calendarToHeadlineFormat(Calendar c) {
        SimpleDateFormat s = new SimpleDateFormat("EEEE, MMMM d");
        return s.format(c.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String calendarToEpisodeStartFormat(Calendar c) {
        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        return s.format(c.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String remainingMinutes(Calendar start, long ms) {
        start.add(Calendar.MILLISECOND, (int) ms);
        long remTimeInMs = start.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        SimpleDateFormat s = new SimpleDateFormat("mm");
        return s.format(remTimeInMs);
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

    @SuppressLint("SimpleDateFormat")
    public static Calendar zdfEpgStringToDate(String airtime) {
        String data[] = airtime.split("\\+");
        String day = airtime.split("\\+")[0];
        if (day == null) return null;
        day = day.replace("T", " ");
        SimpleDateFormat format  = new SimpleDateFormat("yyy-MM-dd hh:mm:ss");
        SimpleDateFormat offsetFormat  = new SimpleDateFormat("mm:ss");

        Date date = null;
        try {
            date = format.parse(day);
            if (data.length > 1) {
                long offset = offsetFormat.parse(data[1]).getTime();
                date.setTime(date.getTime() + offset);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    //// ARD specific
    @SuppressLint("SimpleDateFormat")
    public static Calendar parseArdString(String data) throws ParseException {
        SimpleDateFormat s = new SimpleDateFormat("hh:mm");
        Date date = s.parse(data);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar;
    }
}
