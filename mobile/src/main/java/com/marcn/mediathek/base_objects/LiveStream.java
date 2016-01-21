package com.marcn.mediathek.base_objects;

import android.content.Context;
import com.marcn.mediathek.R;

import java.util.ArrayList;

public class LiveStream {
    public final static int ZDF_MAIN_GROUP = 1617708;
    public final static int ZDF_REST_GROUP = 1822520;
    public final static int ARTE_GROUP = 2;
    public final static int ARD_GROUP = 1;

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

//    public final static String ARD_QUERY = "Das-Erste";
    public final static String ARD_ALPHA_LOGO = "http://www.ardmediathek.de/image/00/22/41/29/08/2123768360/16x9/192";
//    public final static String BR_QUERY = "Bayerisches-Fernsehen-S%C3%BCd";
//    public final static String MDR_QUERY = "MDR-SACHSEN";
//    public final static String NDR_QUERY = "NDR-Niedersachsen";
//    public final static String RBB_QUERY = "rbb-Berlin";
//    public final static String SR_QUERY = "SR-Fernsehen";
//    public final static String SWR_QUERY = "SWR-Baden-W%C3%BCrttemberg";
//    public final static String WDR_QUERY = "WDR-Fernsehen";
//    public final static String TAGESSCHAU_QUERY = "tagesschau24";
//    public final static String DREI_SAT_QUERY = "3sat";
//    public final static String KIKA_QUERY = "KiKA";

    public Channel ch;
    public final String id, queryName;
    public final int originChannelId;
    public final String title;
    private String description, thumb_url;

    public static ArrayList<LiveStream> getBaseLiveStreams (Context c){
        ArrayList<LiveStream> ls =  new ArrayList<>();
        if (c == null) return ls;

        ls.add(new LiveStream("2639200", c.getString(R.string.zdf_name), ZDF_MAIN_GROUP)); // ZDF

        ls.add(new LiveStream("2492878", c.getString(R.string.phoenix_name), ZDF_MAIN_GROUP)); // PHOENIX
        ls.add(new LiveStream("1822544", c.getString(R.string.zdf_kultur_name), ZDF_MAIN_GROUP)); // ZDF KULTUR
        ls.add(new LiveStream("2306126", c.getString(R.string.zdf_info_name), ZDF_MAIN_GROUP)); // ZDF INFO
        ls.add(new LiveStream("1822440", c.getString(R.string.zdf_neo_name), ZDF_MAIN_GROUP)); // ZDF NEO

        ls.add(new LiveStream("6", c.getString(R.string.arte_name), ARTE_GROUP)); //ARTE
        ls.add(new LiveStream("1386804", c.getString(R.string.mdr_name), ARD_GROUP, MDR_QUERY));  // MDR
        ls.add(new LiveStream("21518352", c.getString(R.string.ndr_name), ARD_GROUP, NDR_QUERY));  // NDR
        ls.add(new LiveStream("5904", c.getString(R.string.swr_name), ARD_GROUP, SWR_QUERY));  // SWR
        ls.add(new LiveStream("5902", c.getString(R.string.wdr_name), ARD_GROUP, WDR_QUERY));  // WDR

        ls.add(new LiveStream("208", c.getString(R.string.ard_name), ARD_GROUP, ARD_QUERY));  // ARD
        ls.add(new LiveStream("5868", c.getString(R.string.ard_alpha_name), ARD_GROUP, ARD_ALPHA_QUERY));  // ARD-alpha
        ls.add(new LiveStream("21518950", c.getString(R.string.br_name), ARD_GROUP, BR_QUERY));  // BR
        ls.add(new LiveStream("21518358", c.getString(R.string.rbb_name), ARD_GROUP, RBB_QUERY));  // RBB
        ls.add(new LiveStream("5870", c.getString(R.string.sr_name), ARD_GROUP, SR_QUERY));  // SR
        ls.add(new LiveStream("5878", c.getString(R.string.tagesschau_name), ARD_GROUP, TAGESSCHAU_QUERY));  // TAGESSCHAU24
        ls.add(new LiveStream("5900", c.getString(R.string.drei_sat_name), ARD_GROUP, DREI_SAT_QUERY));  // 3SAT
        ls.add(new LiveStream("5886", c.getString(R.string.kika_name), ARD_GROUP, KIKA_QUERY));  // KIKA
        return ls;
    }

    public LiveStream(String id, String title, int originChannelId) {
        this(id, title, originChannelId, title);
    }

    public LiveStream(String id, String title, int originChannelId, String queryName) {
        this.id = id;
        this.title = title;
        this.originChannelId = originChannelId;
        this.queryName = queryName;
    }

    public String getLiveM3U8 (Context c) {
        switch (id) {
            // ZDF Sender
            case "2639200": return c.getString(R.string.zdf_hd_m3u8);
            case "2492878": return c.getString(R.string.phoenix_m3u8);
            case "1822544": return c.getString(R.string.zdf_kultur_m3u8);
            case "2306126": return c.getString(R.string.zdf_info_m3u8);
            case "5900": return c.getString(R.string.drei_sat_m3u8);
            case "1822440": return c.getString(R.string.zdf_neo_m3u8);
            // ARTE
            case "6": return c.getString(R.string.arte_m3u8);
            // ARD
            case "208": return c.getString(R.string.ard_m3u8);
            case "5868": return c.getString(R.string.ard_alpha_m3u8);
            case "5878": return c.getString(R.string.tagesschau_m3u8);
            // Regional 1
            case "5904": return c.getString(R.string.swr_m3u8);
            case "5902": return c.getString(R.string.wdr_m3u8);
            case "1386804": return c.getString(R.string.mdr_m3u8);
            case "21518352": return c.getString(R.string.ndr_m3u8);
            // Regional 2
            case "21518950": return c.getString(R.string.br_m3u8);
            case "5870": return c.getString(R.string.sr_m3u8);
            case "HR": return c.getString(R.string.hr_m3u8);
            case "21518358": return c.getString(R.string.rbb_m3u8);
            // KiKa
            case "5886": return c.getString(R.string.kika_m3u8);
            default: return null;
        }
    }

    public int getLogoResId () {
        switch (id) {
            // ZDF Sender
            case "2639200": return R.drawable.ic_zdf;
//            case "2492878": return R.drawable.ic_phoenix;
//            case "1822544": return R.drawable.ic_zdf_kultur;
//            case "2306126": return R.drawable.ic_zdf_info;
//            case "1822440": return R.drawable.ic_zdf_neo;
            // ARTE
            case "6": return R.drawable.ic_arte;
            // ARD
            case "208": return R.drawable.ic_ard;
            case "5868": return R.drawable.ic_ard_alpha;
            case "5878": return R.drawable.ic_tagesschau24;
            // Regional 1
            case "5904": return R.drawable.ic_swr;
            case "5902": return R.drawable.ic_wdr;
            case "1386804": return R.drawable.ic_mdr;
            case "21518352": return R.drawable.ic_ndr;
            // Regional 2
            case "21518950": return R.drawable.ic_br;
            case "5870": return R.drawable.ic_sr;
            case "HR": return R.drawable.ic_hr;
//            case "21518358": return R.drawable.ic_rbb;
            case "5900": return R.drawable.ic_3sat;
            // KiKa
            case "5886": return R.drawable.ic_kika;
            default: return -1;
        }
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LiveStream))
            return false;
        LiveStream ls = (LiveStream) o;
        return ls.title.equals(this.title);
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
