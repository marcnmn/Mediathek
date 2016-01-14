package com.marcn.mediathek.base_objects;

import android.content.Context;
import com.marcn.mediathek.R;

public class LiveStream {
    public final String id;
    public final String title;
    public final String thumb_url;

    public LiveStream(String id, String title, String thumb_url) {
        this.id = id;
        this.title = title;
        this.thumb_url = thumb_url;
    }

    public String getLiveM3U8 (Context c) {
        switch (id) {
            case "1822600": return c.getString(R.string.zdf_hd);
            case "2492878": return c.getString(R.string.phoenix);
            case "1822544": return c.getString(R.string.zdf_kultur);
            case "1822586": return c.getString(R.string.zdf_info);
            case "2306126": return c.getString(R.string.drei_sat);
            case "1822440": return c.getString(R.string.zdf_neo);
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
