package com.marcn.mediathek.player;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.exoplayer.MediaFormat;
import com.marcn.mediathek.R;
import com.marcn.mediathek.views.bottom_bar.BottomBarManager;
import com.marcn.mediathek.views.bottom_bar.BottomItem;

import java.util.ArrayList;
import java.util.Collections;

class TrackHelper {

    @NonNull
    static ArrayList<MediaFormat> getFormatList(@NonNull Player player, int type) {
        ArrayList<MediaFormat> tracks = new ArrayList<>();
        for (int i = 0; i < player.getTrackCount(type); i++) {
            tracks.add(player.getTrackFormat(type, i));
        }
        return tracks;
    }

    @Nullable
    static int getSelectedFormat(@NonNull Player player, int type) {
        if (player.getSelectedTrack(type) == Player.TRACK_DISABLED) {
            return BottomBarManager.DISABLED;
        }

        return getFormatList(player, type).get(player.getSelectedTrack(type)).height;
    }

    static void setBestForResolution(Player player, int type, int height) {
        ArrayList<MediaFormat> formats = getFormatList(player, type);
        int index = formats.indexOf(Collections.max(getFormatList(player, type), (a, b) -> {
            if (height != b.height) {
                return 1;
            }
            return Integer.compare(a.bitrate, b.bitrate);
        }));
        player.setSelectedTrack(type, index);
    }

    static ArrayList<BottomItem> getBottomItemList(Player player, int type) {
        ArrayList<Integer> resolutions = new ArrayList<>();
        for (MediaFormat format : getFormatList(player, type)) {
            if (resolutions.indexOf(format.height) < 0) {
                resolutions.add(format.height);
            }
        }
        Collections.sort(resolutions, (a, b) -> {
            if (a < 0 || b < 0) {
                return Integer.compare(a, b);
            }
            return Integer.compare(b, a);
        });

        ArrayList<BottomItem> items = new ArrayList<>();
        for (int res : resolutions) {
            items.add(BottomItem.createBottomItem(R.string.player_quality, BottomBarManager.NO_VALUE,
                    res, BottomBarManager.PlayerItemType.SELECTED_QUALITY));
        }

        return items;
    }

    static void disableTrack(Player player, int type) {
        player.setSelectedTrack(type, Player.TRACK_DISABLED);
    }

    static void enableTrack(Player player, int type) {
        player.setSelectedTrack(type, Player.TRACK_DEFAULT);
    }
}
