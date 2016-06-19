package com.marcn.mediathek.utils;

import android.content.Context;

import com.marcn.mediathek.base_objects.StationOld;
import com.marcn.mediathek.stations.Station;

import java.util.ArrayList;

public class Constants {
    // Thumbnail Qualities
    public static final int SIZE_THUMB_SMALL_X = 178;
    public static final int SIZE_THUMB_SMALL_Y = 100;

    public static final int SIZE_THUMB_BIG_X = 4 * SIZE_THUMB_SMALL_X;
    public static final int SIZE_THUMB_BIG_Y = 4 * SIZE_THUMB_SMALL_Y;

    public static final int SIZE_THUMB_MEDIUM_X = 2 * SIZE_THUMB_SMALL_X;
    public static final int SIZE_THUMB_MEDIUM_Y = 2 * SIZE_THUMB_SMALL_Y;


    // Video Qualities
    public static final int QUALITY_AUDIO_ONLY = 0;

    public static final int QUALITY_MOBILE = 1;
    public static final int QUALITY_LOW = 2;
    public static final int QUALITY_MED = 3;
    public static final int QUALITY_HIGH = 4;
    public static final int QUALITY_VERY_HIGH = 5;

    public static final int QUALITY_AUTO = 9;
    public static final int QUALITY_LOWEST = 10;
    public static final int QUALITY_MAX = 11;

    // CHANNEL NAMES
    public static final String TITLE_CHANNEL_ZDF = "ZDF";
    public static final String TITLE_CHANNEL_PHOENIX = "Phoenix";
    public static final String TITLE_CHANNEL_ZDF_KULTUR = "ZDF.kultur";
    public static final String TITLE_CHANNEL_ZDF_INFO = "ZDFinfo";
    public static final String TITLE_CHANNEL_3SAT = "3sat";
    public static final String TITLE_CHANNEL_ZDF_NEO = "ZDFneo";

    public static final String TITLE_CHANNEL_ARTE = "Arte";

    public static final String TITLE_CHANNEL_ARD = "Das Erste";
    public static final String TITLE_CHANNEL_ARD_ALPHA = "ARD-alpha";
    public static final String TITLE_CHANNEL_TAGESSCHAU = "tagesschau24";

    public static final String TITLE_CHANNEL_SWR = "SWR";
    public static final String TITLE_CHANNEL_MDR = "MDR";
    public static final String TITLE_CHANNEL_NDR = "NDR";
    public static final String TITLE_CHANNEL_WDR = "WDR";

    public static final String TITLE_CHANNEL_BR = "BR";
    public static final String TITLE_CHANNEL_HR = "HR";
    public static final String TITLE_CHANNEL_SR = "SR";
    public static final String TITLE_CHANNEL_RBB = "RBB";

    public static final String TITLE_CHANNEL_KIKA = "KiKA";

    public static final String LIVE_STREAM_CHANNEL_ZDF = "http://zdf1314-lh.akamaihd.net/i/de14_v1@392878/index_3056_av-p.m3u8?sd=10&amp;dw=0&amp;rebase=on&amp;hdntl=";
    public static final String LIVE_STREAM_CHANNEL_PHOENIX = "http://zdf0910-lh.akamaihd.net/i/de09_v1@392871/master.m3u8";
    public static final String LIVE_STREAM_CHANNEL_ZDF_KULTUR = "http://zdf1112-lh.akamaihd.net/i/de11_v1@392881/master.m3u8?dw=0";
    public static final String LIVE_STREAM_CHANNEL_ZDF_INFO = "http://zdf1112-lh.akamaihd.net/i/de12_v1@392882/master.m3u8?dw=0";
    public static final String LIVE_STREAM_CHANNEL_3SAT = "http://zdf0910-lh.akamaihd.net/i/dach10_v1@392872/master.m3u8?dw=0";
    public static final String LIVE_STREAM_CHANNEL_ZDF_NEO = "http://zdf1314-lh.akamaihd.net/i/de13_v1@392877/master.m3u8?dw=0";

    public static final String LIVE_STREAM_CHANNEL_ARTE = "http://delive.artestras.cshls.lldns.net/artestras/contrib/delive.m3u8";

    public static final String LIVE_STREAM_CHANNEL_ARD = "http://daserste_live-lh.akamaihd.net/i/daserste_de@91204/master.m3u8";
    public static final String LIVE_STREAM_CHANNEL_ARD_ALPHA = "http://livestreams.br.de/i/bralpha_germany@119899/master.m3u8";
    public static final String LIVE_STREAM_CHANNEL_TAGESSCHAU = "http://tagesschau-lh.akamaihd.net/i/tagesschau_1@119231/master.m3u8";

    public static final String LIVE_STREAM_CHANNEL_SWR = "http://swrbw-lh.akamaihd.net/i/swrbw_live@196738/master.m3u8";
    public static final String LIVE_STREAM_CHANNEL_MDR = "http://mdr_th_hls-lh.akamaihd.net/i/livetvmdrthueringen_de@106903/master.m3u8";
    public static final String LIVE_STREAM_CHANNEL_NDR = "http://ndr_fs-lh.akamaihd.net/i/ndrfs_nds@119224/master.m3u8";
    public static final String LIVE_STREAM_CHANNEL_WDR = "http://wdr_fs_geo-lh.akamaihd.net/i/wdrfs_geogeblockt@112044/master.m3u8";

    public static final String LIVE_STREAM_CHANNEL_BR = "http://livestreams.br.de/i/bfsnord_germany@119898/master.m3u8";
    public static final String LIVE_STREAM_CHANNEL_SR = "http://live2_sr-lh.akamaihd.net/i/sr_universal02@107595/master.m3u8";
    public static final String LIVE_STREAM_CHANNEL_RBB = "http://rbb_live-lh.akamaihd.net/i/rbb_brandenburg@107638/master.m3u8";

    public static final String LIVE_STREAM_CHANNEL_KIKA = "http://kika_geo-lh.akamaihd.net/i/livetvkika_de@75114/master.m3u8";

    public static final String LIVE_STREAM_EPG_URL = "http://sofa01.zdf.de/epgservice/";
    public static final String LIVE_STREAM_EPG_ZDF_NAME = "zdf";
    public static final String LIVE_STREAM_EPG_PHOENIX_NAME = "phoenix";
    public static final String LIVE_STREAM_EPG_ZDF_KULTUR_NAME = "zdfkultur";
    public static final String LIVE_STREAM_EPG_ZDF_INFO_NAME = "zdfinfo";
    public static final String LIVE_STREAM_EPG_3SAT_NAME = "3sat";
    public static final String LIVE_STREAM_EPG_ZDF_NEO_NAME = "zdfneo";
    public static final String LIVE_STREAM_EPG_ARTE_NAME = "arte";

    public static final String[] station_name_list = {
            TITLE_CHANNEL_ZDF,
            TITLE_CHANNEL_PHOENIX,
            TITLE_CHANNEL_ZDF_KULTUR,
            TITLE_CHANNEL_ZDF_INFO,
            TITLE_CHANNEL_ZDF_NEO,
            TITLE_CHANNEL_ARTE,
            TITLE_CHANNEL_ARD,
            TITLE_CHANNEL_SWR,
            TITLE_CHANNEL_NDR,
            TITLE_CHANNEL_3SAT,
            TITLE_CHANNEL_WDR,
            TITLE_CHANNEL_MDR,
            TITLE_CHANNEL_ARD_ALPHA,
            TITLE_CHANNEL_BR,
            TITLE_CHANNEL_SR,
            TITLE_CHANNEL_HR,
            TITLE_CHANNEL_TAGESSCHAU};

    public static ArrayList<Station> getAllChannels() {
        ArrayList<Station> stations = new ArrayList<>();
        for (String title : station_name_list)
            stations.add(Station.createStation(title));
        return stations;
    }

    public static ArrayList<StationOld> getAllOldChannels(Context c) {
        ArrayList<StationOld> ls = new ArrayList<>();
        if (c == null) return ls;

        ls.add(new StationOld(Constants.TITLE_CHANNEL_ZDF)); // ZDF

        ls.add(new StationOld(Constants.TITLE_CHANNEL_PHOENIX)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_ZDF_KULTUR)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_ZDF_INFO)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_3SAT)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_ZDF_NEO)); // ZDF

        ls.add(new StationOld(Constants.TITLE_CHANNEL_ARTE)); // ZDF

        ls.add(new StationOld(Constants.TITLE_CHANNEL_ARD)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_ARD_ALPHA)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_TAGESSCHAU)); // ZDF

        ls.add(new StationOld(Constants.TITLE_CHANNEL_SWR)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_WDR)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_MDR)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_NDR)); // ZDF

        ls.add(new StationOld(Constants.TITLE_CHANNEL_BR)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_SR)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_HR)); // ZDF
        ls.add(new StationOld(Constants.TITLE_CHANNEL_RBB)); // ZDF

        ls.add(new StationOld(Constants.TITLE_CHANNEL_KIKA)); // ZDF
        return ls;
    }
}
