package com.marcn.mediathek.base_objects;

import android.content.Context;
import com.marcn.mediathek.R;

public class LiveStream {
    public final String id;
    public final String title;
    public String description;
    public final String thumb_url;

    public LiveStream(String id, String title, String thumb_url) {
        this.id = id;
        this.title = title;
        this.thumb_url = thumb_url;
    }

    public String getLiveM3U8 (Context c) {
        switch (title) {
            // ZDF Sender
            case "ZDF": return c.getString(R.string.zdf_hd);
            case "Phoenix": return c.getString(R.string.phoenix);
            case "ZDF.kultur": return c.getString(R.string.zdf_kultur);
            case "ZDFinfo": return c.getString(R.string.zdf_info);
            case "3sat": return c.getString(R.string.drei_sat);
            case "ZDFneo": return c.getString(R.string.zdf_neo);
            // ARTE
            case "ARTE": return c.getString(R.string.arte);
            // ARD
            case "Das-Erste": return c.getString(R.string.ard);
            case "ARD-alpha": return c.getString(R.string.ard_alpha);
            case "tagesschau24": return c.getString(R.string.tagesschau);
            // Regional 1
            case "SWR-Baden-W%C3%BCrttemberg": return c.getString(R.string.swr);
            case "WDR-Fernsehen": return c.getString(R.string.wdr);
            case "MDR-SACHSEN": return c.getString(R.string.mdr);
            case "NDR-Niedersachsen": return c.getString(R.string.ndr);
            // Regional 2
            case "Bayerisches-Fernsehen-S%C3%BCd": return c.getString(R.string.br);
            case "SR-Fernsehen": return c.getString(R.string.sr);
            case "HR": return c.getString(R.string.hr);
            case "rbb-Berlin": return c.getString(R.string.rbb);
            // KiKa
            case "KiKA": return c.getString(R.string.kika);
            default: return null;
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
}
