package com.marcn.mediathek.base_objects;

import android.content.Context;

import com.marcn.mediathek.R;
import com.marcn.mediathek.utils.Constants;

public class Channel {
    public final String title;

    public Channel(String title) {
        this.title = title;
    }

    public String getLiveM3U8 (Context c) {
        switch (title) {
            // ZDF Sender
            case Constants.TITLE_CHANNEL_ZDF: return c.getString(R.string.zdf_hd_m3u8);
            case Constants.TITLE_CHANNEL_PHOENIX: return c.getString(R.string.phoenix_m3u8);
            case Constants.TITLE_CHANNEL_ZDF_KULTUR: return c.getString(R.string.zdf_kultur_m3u8);
            case Constants.TITLE_CHANNEL_ZDF_INFO: return c.getString(R.string.zdf_info_m3u8);
            case Constants.TITLE_CHANNEL_3SAT: return c.getString(R.string.drei_sat_m3u8);
            case Constants.TITLE_CHANNEL_ZDF_NEO: return c.getString(R.string.zdf_neo_m3u8);
            // ARTE
            case Constants.TITLE_CHANNEL_ARTE: return c.getString(R.string.arte_m3u8);
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return c.getString(R.string.ard_m3u8);
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return c.getString(R.string.ard_alpha_m3u8);
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return c.getString(R.string.tagesschau_m3u8);
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return c.getString(R.string.swr_m3u8);
            case Constants.TITLE_CHANNEL_WDR: return c.getString(R.string.wdr_m3u8);
            case Constants.TITLE_CHANNEL_MDR: return c.getString(R.string.mdr_m3u8);
            case Constants.TITLE_CHANNEL_NDR: return c.getString(R.string.ndr_m3u8);
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return c.getString(R.string.br_m3u8);
            case Constants.TITLE_CHANNEL_SR: return c.getString(R.string.sr_m3u8);
            case Constants.TITLE_CHANNEL_HR: return c.getString(R.string.hr_m3u8);
            case Constants.TITLE_CHANNEL_RBB: return c.getString(R.string.rbb_m3u8);
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return c.getString(R.string.kika_m3u8);
            default: return null;
        }
    }

    public int getLogoResId() {
        switch (title) {
            // ZDF Sender
            case Constants.TITLE_CHANNEL_ZDF: return R.drawable.ic_zdf;
            case Constants.TITLE_CHANNEL_PHOENIX: return R.drawable.ic_phoenix;
            case Constants.TITLE_CHANNEL_ZDF_KULTUR: return R.drawable.ic_zdf_kultur;
            case Constants.TITLE_CHANNEL_ZDF_INFO: return R.drawable.ic_zdf_info;
            case Constants.TITLE_CHANNEL_3SAT: return R.drawable.ic_3sat;
            case Constants.TITLE_CHANNEL_ZDF_NEO: return R.drawable.ic_zdf_neo;
            // ARTE
            case Constants.TITLE_CHANNEL_ARTE: return R.drawable.ic_arte;
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return R.drawable.ic_ard;
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return R.drawable.ic_ard_alpha;
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return R.drawable.ic_tagesschau24;
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return R.drawable.ic_swr;
            case Constants.TITLE_CHANNEL_WDR: return R.drawable.ic_wdr;
            case Constants.TITLE_CHANNEL_MDR: return R.drawable.ic_mdr;
            case Constants.TITLE_CHANNEL_NDR: return R.drawable.ic_ndr;
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return R.drawable.ic_br;
            case Constants.TITLE_CHANNEL_SR: return R.drawable.ic_sr;
            case Constants.TITLE_CHANNEL_HR: return R.drawable.ic_hr;
            case Constants.TITLE_CHANNEL_RBB: return R.drawable.ic_rbb;
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return R.drawable.ic_kika;
            default: return -1;
        }
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Channel))
            return false;
        Channel ls = (Channel) o;
        return ls.title.equals(this.title);
    }
}
