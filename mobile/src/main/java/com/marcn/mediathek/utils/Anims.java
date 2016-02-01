package com.marcn.mediathek.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;

public class Anims {
    private static int STANDARD_FADE_TIME_IN_MS = 300;

    public static void fadeIn(View view) {
        fadeIn(view, STANDARD_FADE_TIME_IN_MS);
    }

    public static void fadeIn(View view, int ms) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 0.f, 1.0f);
        anim.setDuration(ms);
        anim.start();
    }

    public static void fadeOut(View view) {
        fadeOut(view, STANDARD_FADE_TIME_IN_MS);
    }

    public static void fadeOut(View view, int ms) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 1.f, 0.f);
        anim.setDuration(ms);
        anim.start();
    }

    public static void slightBounce(View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.1f);
        scaleDownX.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float v) {
                v = 2 * v - 1f;
                return -v * v + 1f;
            }
        });
        scaleDownY.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float v) {
                v = 2 * v - 1f;
                return -v * v + 1f;
            }
        });
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }
}
