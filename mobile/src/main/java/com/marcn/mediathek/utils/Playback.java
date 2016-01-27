package com.marcn.mediathek.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.marcn.mediathek.PlayerActivity;
import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.Video;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class Playback {
    public static void playByUrl(final Activity activity, final String url, final View view, final int internalPlayer, final String title) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (internalPlayer) {
                    case Video.ACTION_INTERNAL_PLAYER:
                        startInternalPlayer(activity, url, view);
                        break;
                    case Video.ACTION_EXTERNAL_PLAYER_DIALOG:
                        startExternalPlayerDialog(activity, url, title);
                        break;
                    case Video.ACTION_DEFAULT_EXTERNAL_PLAYER:
                        startDefaultExternalPlayer(activity, url);
                        break;
                    case Video.ACTION_DOWNLOAD:
                        Storage.downloadFile(activity, url, title);
                        break;
                }
            }
        });
    }

    public static void startInternalPlayer(Activity activity, String url, View view) {
        if (url == null) return;
        Intent intent = new Intent(activity, PlayerActivity.class);
        intent.putExtra(PlayerActivity.INTENT_LIVE_STREAM_URL, url);

        ImageView imageView = (ImageView) view;
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Storage.saveBitmapOnDisk(activity, bmp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setExitTransition(new Explode());

            view.setTransitionName("thumb");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                    Pair.create(view, "thumb"));
            activity.startActivity(intent, options.toBundle());
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
