package com.marcn.mediathek.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Pair;
import android.view.View;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.pages.player_page.PlayerActivity;

public class Playback {
    public static void playByUrl(final Activity activity, final String url, final View view, final int internalPlayer, final String title) {
        activity.runOnUiThread(() -> {
            switch (internalPlayer) {
                case Episode.ACTION_INTERNAL_PLAYER:
                    startInternalPlayer(activity, url, view);
                    break;
                case Episode.ACTION_SHARE_VIDEO_DIALOG:
                    startExternalPlayerDialog(activity, url, title);
                    break;
                case Episode.ACTION_DEFAULT_EXTERNAL_PLAYER:
                    startDefaultExternalPlayer(activity, url);
                    break;
                case Episode.ACTION_DOWNLOAD:
                    Storage.downloadFile(activity, url, title);
                    break;
            }
        });
    }

    public static void startInternalPlayer(Activity activity, String url, View view) {
        if (url == null) return;
        Intent intent = new Intent(activity, PlayerActivity.class);
        intent.putExtra(PlayerActivity.INTENT_LIVE_STREAM_URL, url);

//        ImageView imageView = (ImageView) view;
//        Bitmap bmp = (imageView.getDrawable()).g();
//        Storage.saveBitmapOnDisk(activity, bmp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            activity.getWindow().setExitTransition(new Explode());

            view.setTransitionName("thumb");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    Pair.create(view, "thumb"));
            activity.startActivity(intent, options.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    public static void startInternalPlayer(Activity activity, String url, ActivityOptions activityOptions) {
        if (url == null) return;
        Intent intent = new Intent(activity, PlayerActivity.class);
        intent.putExtra(PlayerActivity.INTENT_LIVE_STREAM_URL, url);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && activityOptions != null) {
            activity.startActivity(intent, activityOptions.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    public static void startExternalPlayerDialog(Activity activity, String url, String title) {
        if (url == null) return;
        Intent stream = new Intent(Intent.ACTION_VIEW);
        stream.setDataAndType(Uri.parse(url), "video/*");
        activity.startActivity(Intent.createChooser(stream, activity.getResources().getText(R.string.send_to_intent) + " für " + title));
    }

    public static void startDefaultExternalPlayer(Activity activity, String url) {
        if (url == null) return;
        Intent stream = new Intent(Intent.ACTION_VIEW);
        stream.setDataAndType(Uri.parse(url), "video/*");
        activity.startActivity(stream);
    }

    public void shareYatse(Activity activity, String url) {
        Intent sharingIntent = new Intent(Intent.ACTION_VIEW);
        sharingIntent.setDataAndType(Uri.parse(url), "video/*");
        sharingIntent.setClassName("org.leetzone.android.yatsewidgetfree", "org.leetzone.android.yatsewidget.ui.SendToActivity");
        activity.startActivity(sharingIntent);
    }
}
