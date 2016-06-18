package com.marcn.mediathek.utils;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.marcn.mediathek.R;

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

    public static AnimatorSet elevateUpDown(View view) {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(
                AnimatorInflater.loadAnimator(view.getContext(), R.animator.grid_item_selected),
                AnimatorInflater.loadAnimator(view.getContext(), R.animator.grid_item_unselected)
        );
        set.setTarget(view);
        return set;
    }

    public static void elevateUp(View view) {
        if (view == null) {
            return;
        }
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(),
                R.animator.grid_item_selected);
        set.setTarget(view);
        set.start();
    }

    public static void elevateDown(View view) {
        if (view == null) {
            return;
        }
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(),
                R.animator.grid_item_unselected);
        set.setTarget(view);
        set.start();
    }

    public static void slightBounce(View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.1f);
        scaleDownX.setInterpolator(v -> {
            v = 2 * v - 1f;
            return -v * v + 1f;
        });
        scaleDownY.setInterpolator(v -> {
            v = 2 * v - 1f;
            return -v * v + 1f;
        });
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }

    public static void hideFabWorkaround(View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0f);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.setDuration(0);
        scaleDown.start();
    }

    public static void showFabWorkaround(View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.setDuration(0);
        scaleDown.start();
    }
}
