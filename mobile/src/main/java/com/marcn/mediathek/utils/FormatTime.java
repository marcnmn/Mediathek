package com.marcn.mediathek.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FormatTime {

    @SuppressLint("SimpleDateFormat")
    public static String calendarToEpisodeStartFormat(Calendar c) {
        String day;
        int dayDiff = c.get(Calendar.DAY_OF_YEAR)
                - Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        switch (dayDiff) {
            case 2: day = "Übermorgen"; break;
            case 1: day = "Morgen"; break;
            case 0: day = "Heute"; break;
            case -1: day = "Gestern"; break;
            case -2: day = "Vorgestern"; break;
            default:
                SimpleDateFormat s = new SimpleDateFormat("EEE, d.MM");
                day = s.format(c.getTime());
                break;
        }

        SimpleDateFormat s = new SimpleDateFormat("HH:mm");
        String time = s.format(c.getTime());

        return day + " - " + time;
    }

    @SuppressLint("SimpleDateFormat")
    public static int remainingMinutes(Calendar start, long ms) {
        long remMs = start.getTimeInMillis() + ms - System.currentTimeMillis();
        return (int) Math.max((remMs / 60000), 0);
    }

    @SuppressLint("SimpleDateFormat")
    public static Calendar zdfAirtimeStringToDate(String airtime) {
        //String day = airtime.split(" ")[0];
        SimpleDateFormat format  = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = null;
        try {
            date = format.parse(airtime);
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
        SimpleDateFormat format  = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        SimpleDateFormat offsetFormat  = new SimpleDateFormat("mm:ss");

        Date date = null;
        try {
            date = format.parse(day);
            if (data.length > 1) {
                long min = Long.parseLong(data[1].split(":")[0]);
                date.setTime(date.getTime() + min * 60000);
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

    public static String getMissedHeader(Calendar data) {
        int dayDiff = data.get(Calendar.DAY_OF_YEAR)
                - Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        switch (dayDiff) {
            case 2: return "Übermorgen";
            case 1: return "Morgen";
            case 0: return "Heute";
            case -1: return "Gestern";
            case -2: return "Vorgestern";
            default: return calendarToHeadlineFormat(data);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String calendarToHeadlineFormat(Calendar c) {
        SimpleDateFormat s = new SimpleDateFormat("EEEE, d. MMMM");
        return s.format(c.getTime());
    }
}
