package com.marcn.mediathek.utils;

import android.animation.ObjectAnimator;
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
}
