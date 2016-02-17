package com.marcn.mediathek.base_objects;

import com.marcn.mediathek.utils.Constants;

public class Station {
    public static final int ZDF_GOURP = 0;
    public static final int ARTE_GOURP = 1;
    public static final int ARD_GOURP = 2;

    public final String title;
    private final int group;

//    public Station(String title) {
//        this(title, -1);
//    }

    public Station(String title) {
        this.title = title;
        this.group = getGroupFromName(title);
    }

    public LiveStream getLiveStream() {
        String id = getChannelId();
        String query = getLiveStreamQueryString();
        return new LiveStream(id, query, -1);
    }

    public String getLiveStreamQueryString() {
        switch (title) {
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return LiveStream.ARD_QUERY;
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return LiveStream.ARD_ALPHA_QUERY;
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return LiveStream.TAGESSCHAU_QUERY;
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return LiveStream.SWR_QUERY;
            case Constants.TITLE_CHANNEL_WDR: return LiveStream.WDR_QUERY;
            case Constants.TITLE_CHANNEL_MDR: return LiveStream.MDR_QUERY;
            case Constants.TITLE_CHANNEL_NDR: return LiveStream.NDR_QUERY;
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return LiveStream.BR_QUERY;
            case Constants.TITLE_CHANNEL_SR: return LiveStream.SR_QUERY;
            case Constants.TITLE_CHANNEL_RBB: return LiveStream.RBB_QUERY;
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return LiveStream.KIKA_QUERY;
        }
        return null;
    }

    public String getChannelId() {
        switch (title) {
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return "208";
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return "5868";
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return "5878";
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return "5904";
            case Constants.TITLE_CHANNEL_WDR: return "5902";
            case Constants.TITLE_CHANNEL_MDR: return "1386804";
            case Constants.TITLE_CHANNEL_NDR: return "21518352";
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return "21518950";
            case Constants.TITLE_CHANNEL_SR: return "5870";
            case Constants.TITLE_CHANNEL_RBB: return "21518358";
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return "5886";
        }
        return "";
    }

    public static int getGroupFromName(String name) {
        switch (name) {
            // ZDF Sender
            case Constants.TITLE_CHANNEL_ZDF: return ZDF_GOURP;
            case Constants.TITLE_CHANNEL_PHOENIX: return ZDF_GOURP;
            case Constants.TITLE_CHANNEL_ZDF_KULTUR: return ZDF_GOURP;
            case Constants.TITLE_CHANNEL_ZDF_INFO: return ZDF_GOURP;
            case Constants.TITLE_CHANNEL_3SAT: return ZDF_GOURP;
            case Constants.TITLE_CHANNEL_ZDF_NEO: return ZDF_GOURP;
            // ARTE
            case Constants.TITLE_CHANNEL_ARTE: return ARTE_GOURP;
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return ARD_GOURP;
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return ARD_GOURP;
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return ARD_GOURP;
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return ARD_GOURP;
            case Constants.TITLE_CHANNEL_WDR: return ARD_GOURP;
            case Constants.TITLE_CHANNEL_MDR: return ARD_GOURP;
            case Constants.TITLE_CHANNEL_NDR: return ARD_GOURP;
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return ARD_GOURP;
            case Constants.TITLE_CHANNEL_SR: return ARD_GOURP;
            case Constants.TITLE_CHANNEL_HR: return ARD_GOURP;
            case Constants.TITLE_CHANNEL_RBB: return ARD_GOURP;
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return ARD_GOURP;
            default: return -1;
        }
    }

//    public int getLogoResId() {
//        switch (title) {
//            // ZDF Sender
//            case Constants.TITLE_CHANNEL_ZDF: return R.drawable.ic_zdf;
//            case Constants.TITLE_CHANNEL_PHOENIX: return R.drawable.ic_phoenix;
//            case Constants.TITLE_CHANNEL_ZDF_KULTUR: return R.drawable.ic_zdf_kultur;
//            case Constants.TITLE_CHANNEL_ZDF_INFO: return R.drawable.ic_zdf_info;
//            case Constants.TITLE_CHANNEL_3SAT: return R.drawable.ic_3sat;
//            case Constants.TITLE_CHANNEL_ZDF_NEO: return R.drawable.ic_zdf_neo;
//            // ARTE
//            case Constants.TITLE_CHANNEL_ARTE: return R.drawable.ic_arte;
//            // ARD
//            case Constants.TITLE_CHANNEL_ARD: return R.drawable.ic_ard;
//            case Constants.TITLE_CHANNEL_ARD_ALPHA: return R.drawable.ic_ard_alpha;
//            case Constants.TITLE_CHANNEL_TAGESSCHAU: return R.drawable.ic_tagesschau24;
//            // Regional 1
//            case Constants.TITLE_CHANNEL_SWR: return R.drawable.ic_swr;
//            case Constants.TITLE_CHANNEL_WDR: return R.drawable.ic_wdr;
//            case Constants.TITLE_CHANNEL_MDR: return R.drawable.ic_mdr;
//            case Constants.TITLE_CHANNEL_NDR: return R.drawable.ic_ndr;
//            // Regional 2
//            case Constants.TITLE_CHANNEL_BR: return R.drawable.ic_br;
//            case Constants.TITLE_CHANNEL_SR: return R.drawable.ic_sr;
//            case Constants.TITLE_CHANNEL_HR: return R.drawable.ic_hr;
//            case Constants.TITLE_CHANNEL_RBB: return R.drawable.ic_rbb;
//            // KiKa
//            case Constants.TITLE_CHANNEL_KIKA: return R.drawable.ic_kika;
//            default: return -1;
//        }
//    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Station))
            return false;
        Station ls = (Station) o;
        return ls.title.equals(this.title);
    }

    public int getGroup() {
        return group;
    }
}
