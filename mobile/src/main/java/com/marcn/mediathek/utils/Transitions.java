package com.marcn.mediathek.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.transition.Explode;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.List;

public class Transitions {
    public static String PLAYER_THUMBNAIL = "thumb";

    public static void saveBitmapFromImageView(Context context, ImageView imageView, String fileName) {
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Storage.saveBitmapOnDisk(context, bmp, fileName);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ActivityOptions createOptions(Activity activity, Pair<View, String> ... pairs) {
        return ActivityOptions.makeSceneTransitionAnimation(activity, pairs);
    }
}
