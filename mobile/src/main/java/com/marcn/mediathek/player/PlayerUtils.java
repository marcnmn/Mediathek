package com.marcn.mediathek.player;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer.util.Util;

/**
 * Created by marcneumann on 17.06.16.
 */
public class PlayerUtils {

    public static final int TYPE_DASH = 0;
    public static final int TYPE_SS = 1;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;

    private static final String EXT_DASH = ".mpd";
    private static final String EXT_SS = ".ism";
    private static final String EXT_HLS = ".m3u8";


    public static Player.RendererBuilder createRendererBuilder(Context context, String url) {
        String userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
        Uri uri = Uri.parse(url);
        int contentType = inferContentType(uri, null);
        switch (contentType) {
            case TYPE_HLS:
                return new HlsRendererBuilder(context, userAgent, url);
            case TYPE_OTHER:
                return new ExtractorRendererBuilder(context, userAgent, uri);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    public static int inferContentType(Uri uri, String fileExtension) {
        String lastPathSegment = !TextUtils.isEmpty(fileExtension) ? "." + fileExtension
                : uri.getLastPathSegment();
        if (lastPathSegment == null) {
            return TYPE_OTHER;
        } else if (lastPathSegment.endsWith(EXT_DASH)) {
            return TYPE_DASH;
        } else if (lastPathSegment.endsWith(EXT_SS)) {
            return TYPE_SS;
        } else if (lastPathSegment.endsWith(EXT_HLS)) {
            return TYPE_HLS;
        } else {
            return TYPE_OTHER;
        }
    }
}
