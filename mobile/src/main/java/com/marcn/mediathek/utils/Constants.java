package com.marcn.mediathek.utils;

import android.content.Context;

import com.marcn.mediathek.base_objects.Channel;

import java.util.ArrayList;

public class Constants {
    // CHANNEL NAMES
    public static final String TITLE_CHANNEL_ZDF = "ZDF";
    public static final String TITLE_CHANNEL_PHOENIX = "Phoenix";
    public static final String TITLE_CHANNEL_ZDF_KULTUR = "ZDF.kultur";
    public static final String TITLE_CHANNEL_ZDF_INFO = "ZDFinfo";
    public static final String TITLE_CHANNEL_3SAT = "3sat";
    public static final String TITLE_CHANNEL_ZDF_NEO = "ZDFneo";

    public static final String TITLE_CHANNEL_ARTE = "Arte";

    public static final String TITLE_CHANNEL_ARD = "ARD";
    public static final String TITLE_CHANNEL_ARD_ALPHA = "ARD Alpha";
    public static final String TITLE_CHANNEL_TAGESSCHAU = "Tagesschau24";

    public static final String TITLE_CHANNEL_SWR = "SWR";
    public static final String TITLE_CHANNEL_MDR = "MDR";
    public static final String TITLE_CHANNEL_NDR = "NDR";
    public static final String TITLE_CHANNEL_WDR = "WDR";

    public static final String TITLE_CHANNEL_BR = "BR";
    public static final String TITLE_CHANNEL_HR = "HR";
    public static final String TITLE_CHANNEL_SR = "SR";
    public static final String TITLE_CHANNEL_RBB = "RBB";

    public static final String TITLE_CHANNEL_KIKA = "KiKA";

    // HTML QUERIES
    public final static String ARD_QUERY = "Das-Erste";
    public final static String ARD_ALPHA_QUERY = "ARD-alpha";
    public final static String BR_QUERY = "Bayerisches-Fernsehen-S%C3%BCd";
    public final static String MDR_QUERY = "MDR-SACHSEN";
    public final static String NDR_QUERY = "NDR-Niedersachsen";
    public final static String RBB_QUERY = "rbb-Berlin";
    public final static String SR_QUERY = "SR-Fernsehen";
    public final static String SWR_QUERY = "SWR-Baden-W%C3%BCrttemberg";
    public final static String WDR_QUERY = "WDR-Fernsehen";
    public final static String TAGESSCHAU_QUERY = "tagesschau24";
    public final static String DREI_SAT_QUERY = "3sat";
    public final static String KIKA_QUERY = "KiKA";

    public static ArrayList<Channel> getBaseChannels(Context c){
        ArrayList<Channel> ls =  new ArrayList<>();
        if (c == null) return ls;

        ls.add(new Channel(Constants.TITLE_CHANNEL_ZDF)); // ZDF

        ls.add(new Channel(Constants.TITLE_CHANNEL_PHOENIX)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_ZDF_KULTUR)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_ZDF_INFO)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_3SAT)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_ZDF_NEO)); // ZDF

        ls.add(new Channel(Constants.TITLE_CHANNEL_ARTE)); // ZDF

        ls.add(new Channel(Constants.TITLE_CHANNEL_ARD)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_ARD_ALPHA)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_TAGESSCHAU)); // ZDF

        ls.add(new Channel(Constants.TITLE_CHANNEL_SWR)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_WDR)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_MDR)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_NDR)); // ZDF

        ls.add(new Channel(Constants.TITLE_CHANNEL_BR)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_SR)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_HR)); // ZDF
        ls.add(new Channel(Constants.TITLE_CHANNEL_RBB)); // ZDF

        ls.add(new Channel(Constants.TITLE_CHANNEL_KIKA)); // ZDF
        return ls;
    }
}
