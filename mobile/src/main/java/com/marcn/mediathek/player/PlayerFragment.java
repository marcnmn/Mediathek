package com.marcn.mediathek.player;

import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.marcn.mediathek.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

import static com.marcn.mediathek.utils.LayoutTasks.hideSystemUI;
import static com.marcn.mediathek.utils.LayoutTasks.isImmersiveEnabled;
import static com.marcn.mediathek.utils.LayoutTasks.isInLandscape;

public class PlayerFragment extends Fragment implements Player.Listener,
        TextureView.SurfaceTextureListener, PlayerControls {

    @BindView(R.id.video_frame)
    AspectRatioFrameLayout mVideoFrame;

    @BindView(R.id.surface_view)
    TextureView mSurfaceView;

    private String mStreamUrl;
    private Player mPlayer;
    private boolean mPlayerNeedsPrepare;
    private VideoControllerView mVideoControllerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, view);
        setUpControls();
        return view;
    }

    private void setUpControls() {
        mSurfaceView.setSurfaceTextureListener(this);
        mVideoControllerView = new VideoControllerView(getContext());
        mVideoControllerView.setAnchorView(mVideoFrame);
        if (isInLandscape(getContext()) && !isImmersiveEnabled(getActivity())) {
            hideSystemUI(getActivity());
        }
    }

    private void toggleControlsVisibility() {
        if (mVideoControllerView.isShowing()) {
            mVideoControllerView.hide();
        } else {
            mVideoControllerView.show();
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (mPlayer == null) {
            mPlayer = new Player(PlayerUtils.createRendererBuilder(getContext(), mStreamUrl));
            mPlayerNeedsPrepare = true;
            mPlayer.addListener(this);
            mVideoControllerView.setMediaPlayer(mPlayer.getPlayerControl());
            mVideoControllerView.setEnabled(true);
        }
        if (mPlayerNeedsPrepare) {
            mPlayer.prepare();
            mPlayerNeedsPrepare = false;
        }

        SurfaceTexture surfaceTexture = mSurfaceView.getSurfaceTexture();
        Surface surface = new Surface(surfaceTexture);
        mPlayer.setSurface(surface);
        mPlayer.setPlayWhenReady(playWhenReady);
    }

    private void releasePlayer() {
        new Thread(() -> {
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
        }).start();
    }

    // Android lifecycle handling
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                !isImmersiveEnabled(getActivity())) {
            hideSystemUI(getActivity());
        } else if (isImmersiveEnabled(getActivity())) {
            hideSystemUI(getActivity());
        }
    }

    // Player events
    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            mVideoControllerView.show(Integer.MAX_VALUE);
        }
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    // Surface Events
    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        mVideoFrame.setAspectRatio(height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mStreamUrl != null) {
            preparePlayer(true);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // nop
    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // nop
    }

    @Override
    public void play(@NonNull String streamUrl) {
        mStreamUrl = streamUrl;
        if (mSurfaceView.getSurfaceTexture() != null) {
            preparePlayer(true);
        }
    }

    @Override
    public void resume() {
        mPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        mPlayer.setPlayWhenReady(false);
    }

    @Override
    public void seekTo(long positionMs) {
        mPlayer.seekTo(positionMs);
    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @OnTouch(R.id.video_frame)
    boolean onFrameTouched(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toggleControlsVisibility();
        }
        return true;
    }
}
