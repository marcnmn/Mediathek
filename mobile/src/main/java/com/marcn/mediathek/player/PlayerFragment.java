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
import android.widget.ProgressBar;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.marcn.mediathek.R;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.utils.Storage;
import com.marcn.mediathek.views.bottom_bar.BottomBarManager;
import com.marcn.mediathek.views.bottom_bar.BottomItem;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import static com.marcn.mediathek.utils.LayoutTasks.hideSystemUI;
import static com.marcn.mediathek.utils.LayoutTasks.isImmersiveEnabled;
import static com.marcn.mediathek.utils.LayoutTasks.isInLandscape;

public class PlayerFragment extends Fragment implements Injector<ActivityComponent>, Player.Listener,
        TextureView.SurfaceTextureListener, PlayerControls {

    @Inject
    VideoControllerView mVideoControllerView;

    @Inject
    BottomBarManager mBottomBarManager;

    @Inject
    Subject<BottomItem, BottomItem> mSubject;

    @BindView(R.id.player_video_frame)
    AspectRatioFrameLayout mVideoFrame;

    @BindView(R.id.player_surface_view)
    TextureView mSurfaceView;

    @BindView(R.id.player_progress_bar)
    ProgressBar mProgressBar;

    private String mStreamUrl;
    private Player mPlayer;
    private boolean mPlayerNeedsPrepare;
    private boolean mPlayInBackground;
    private boolean mMuteAudio;
    private boolean mHeadphoneMode;
    private CompositeSubscription mSubscription = new CompositeSubscription();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectHelper.setupPage(this);
        mSubscription.add(mSubject.subscribe(this::onBottomItemReceived));
    }

    @Override
    public void injectWith(ActivityComponent component) {
        component.inject(this);
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
        mVideoControllerView.setAnchorView(mVideoFrame);
        if (isInLandscape(getContext()) && !isImmersiveEnabled(getActivity())) {
            hideSystemUI(getActivity());
        }
    }

    @Override
    public void play(@NonNull String streamUrl) {
        mStreamUrl = streamUrl;
        if (mSurfaceView.getSurfaceTexture() != null) {
            preparePlayer(true);
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (mPlayer == null) {
            mPlayerNeedsPrepare = true;
            mPlayer = new Player(PlayerUtils.createRendererBuilder(getContext(), mStreamUrl));
            mPlayer.addListener(this);
            mVideoControllerView.setMediaPlayer(mPlayer.getPlayerControl(), this);
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

    private void onBottomItemReceived(BottomItem bottomItem) {
        switch (bottomItem.getType()) {
            case TOP_LEVEL_QUALITY:
                showQualitySettings();
                break;
            case TOP_LEVEL_DOWNLOAD:
                Storage.downloadFile(getActivity(), mStreamUrl, "Downlaod");
                mBottomBarManager.clearTop();
                break;
            case TOP_LEVEL_AUDIO_BACKGROUND:
                mPlayInBackground = !mPlayInBackground;
                mBottomBarManager.clearTop();
                break;
            case TOP_LEVEL_AUDIO_MUTE:
                mMuteAudio = !mMuteAudio;
                mPlayer.setSelectedTrack(Player.TYPE_AUDIO, mMuteAudio ? Player.TRACK_DISABLED : Player.TRACK_DEFAULT);
                mBottomBarManager.clearTop();
                break;
            case TOP_LEVEL_HEADPHONE_MODE:
                mHeadphoneMode = !mHeadphoneMode;
                mPlayInBackground = mHeadphoneMode;
                mPlayer.setSelectedTrack(Player.TYPE_VIDEO, mHeadphoneMode ? Player.TRACK_DISABLED : Player.TRACK_DEFAULT);
                mBottomBarManager.clearTop();
                break;
            case SELECTED_QUALITY:
                TrackHelper.setBestForResolution(mPlayer, Player.TYPE_VIDEO, bottomItem.getValue());
                mBottomBarManager.clearAll();
                break;
        }
    }

    @Override
    public void showSettings() {
        mBottomBarManager
                .withContext(getActivity())
                .withQualitySetting(TrackHelper.getSelectedFormat(mPlayer, Player.TYPE_VIDEO))
                .withDownloadSetting()
                .withBackgroundSetting(mPlayInBackground)
                .withMuteAudioSetting(mMuteAudio)
                .withHeadphoneMode(mHeadphoneMode)
                .show();
    }

    private void showQualitySettings() {
        mBottomBarManager
                .withContext(getActivity())
                .withEntryList(TrackHelper.getBottomItemList(mPlayer, Player.TYPE_VIDEO))
                .show();
    }

    private void toggleControlsVisibility() {
        if (mVideoControllerView.isShowing()) {
            mVideoControllerView.hide();
        } else {
            mVideoControllerView.show();
        }
    }

    // Android lifecycle handling
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mPlayInBackground) {
            releasePlayer();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        releasePlayer();
        mSubscription.unsubscribe();
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
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case ExoPlayer.STATE_READY:
                mProgressBar.setVisibility(View.GONE);
                break;
            case ExoPlayer.STATE_ENDED:
                mVideoControllerView.show(Integer.MAX_VALUE);
                break;
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

    @OnTouch(R.id.player_video_frame)
    boolean onFrameTouched(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            toggleControlsVisibility();
        }
        return true;
    }
}
