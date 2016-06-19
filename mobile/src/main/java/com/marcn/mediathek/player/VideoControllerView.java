/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marcn.mediathek.player;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SeekBar;
import android.widget.TextView;

import com.marcn.mediathek.R;
import com.marcn.mediathek.pages.player_page.IPlayer;
import com.marcn.mediathek.utils.Anims;

import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoControllerView extends FrameLayout {
    private static final String TAG = "VideoControllerView";
    private static final int INT_DEFAULT_TIMEOUT = 3000;
    private static final int INT_DEFAULT_INITIAL_TIMEOUT = 1500;

    private MediaPlayerControl mPlayer;
    private Context mContext;
    private ViewGroup mAnchor;
    private View mRoot;
    private boolean mShowing;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    @BindView(R.id.pause)
    ImageView mPauseButton;

    @BindView(R.id.quality)
    ImageView mSettingsButton;

    @BindView(R.id.share)
    ImageView mShareButton;

    @BindView(R.id.mediacontroller_progress)
    SeekBar mProgress;

    @BindView(R.id.progressContainer)
    View mProgressContainer;

    @BindView(R.id.textEndTime)
    TextView mEndTime;

    @BindView(R.id.textCurrentTime)
    TextView mCurrentTime;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private boolean mDragging;

    private IPlayer.Callbacks mCallbacks;

    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = null;
        mContext = context;

        Log.i(TAG, TAG);
    }

    public VideoControllerView(Context context) {
        super(context);
        mContext = context;

        Log.i(TAG, TAG);
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
        super.onFinishInflate();
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void setCallbacks(IPlayer.Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        LayoutParams frameParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        View v = makeControllerView();
        addView(v, frameParams);
    }

    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.media_controller, null);
        ButterKnife.bind(this, mRoot);

        initControllerView(mRoot);
        return mRoot;
    }

    private void initControllerView(View v) {
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setEnabled(true);
        mProgress.setMax(1000);

        if (mAnchor != null) {
            mAnchor.addView(this);
        }

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    private void disableUnsupportedButtons() {
        try {
            if (!mPlayer.canPause()) {
                mPauseButton.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ignored) {
        }
    }

    public void show() {
        show(INT_DEFAULT_TIMEOUT);
    }


    public void showFirstTime() {
        show(INT_DEFAULT_INITIAL_TIMEOUT);
    }

    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {
            mRoot.setVisibility(VISIBLE);
            mPauseButton.requestFocus();

            setProgress();
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(this, "alpha", 0.f, 1.f);
            fadeIn.start();
            Anims.slightBounce(mPauseButton);
            mShowing = true;
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            Message msg = mHandler.obtainMessage(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void hide() {
        if (mAnchor == null) {
            return;
        }

        if (mShowing) {
            mHandler.removeMessages(SHOW_PROGRESS);
            Anims.fadeOut(this);
            mShowing = false;
        }
    }

    @SuppressWarnings("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing && mPlayer.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(INT_DEFAULT_TIMEOUT);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mPlayer == null) {
            return true;
        }

        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show(INT_DEFAULT_TIMEOUT);
                if (mPauseButton != null) {
                    mPauseButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mPlayer.isPlaying()) {
                mPlayer.start();
                updatePausePlay();
                show(INT_DEFAULT_TIMEOUT);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
                show(INT_DEFAULT_TIMEOUT);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show(INT_DEFAULT_TIMEOUT);
        return super.dispatchKeyEvent(event);
    }

    public void updatePausePlay() {
        if (mRoot == null || mPauseButton == null || mPlayer == null) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_pause_circle);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_play_circle);
        }
    }

    private void doPauseResume() {
        if (mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(VideoControllerView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(VideoControllerView.class.getName());
    }

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);
            mDragging = true;
            resetHideTimeout();

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / 1000L;
            mPlayer.seekTo((int) newposition);
//            if (mCurrentTime != null)
//                mCurrentTime.setText(stringForTime( (int) newposition));
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(INT_DEFAULT_TIMEOUT);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();

        if (duration <= 0)
            mProgressContainer.setVisibility(GONE);
        else
            mProgressContainer.setVisibility(VISIBLE);

        if (duration > 0) {
            // use long to avoid overflow
            long pos = 1000L * position / duration;
            mProgress.setProgress((int) pos);
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        mEndTime.setText(stringForTime(duration));
        mCurrentTime.setText(stringForTime(position));
        return position;
    }

    private void resetHideTimeout() {
        resetHideTimeout(INT_DEFAULT_TIMEOUT);
    }

    private void resetHideTimeout(int timeout) {
        mHandler.removeMessages(FADE_OUT);
        Message msg = mHandler.obtainMessage(FADE_OUT);
        mHandler.sendMessageDelayed(msg, timeout);
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);

        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @OnClick(R.id.pause)
    void onClick(View v) {
        if (!mShowing) {
            show(INT_DEFAULT_TIMEOUT);
        } else {
            resetHideTimeout();
            doPauseResume();
        }
    }

    @OnClick(R.id.quality)
    void onQualitySelected() {
        if (mCallbacks != null) {
            mCallbacks.showBottomBar();
        }
    }
}