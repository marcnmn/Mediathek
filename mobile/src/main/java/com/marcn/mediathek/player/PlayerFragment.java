package com.marcn.mediathek.player;

import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaFormat;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.BottomQualityAdapter;
import com.marcn.mediathek.pages.player_page.IPlayer;
import com.marcn.mediathek.pages.player_page.PlayerActivity;
import com.marcn.mediathek.utils.DataUtils;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

import static com.marcn.mediathek.utils.LayoutTasks.hideSystemUI;
import static com.marcn.mediathek.utils.LayoutTasks.isImmersiveEnabled;
import static com.marcn.mediathek.utils.LayoutTasks.isInLandscape;

public class PlayerFragment extends Fragment implements Player.Listener,
        TextureView.SurfaceTextureListener, PlayerControls, IPlayer, IPlayer.Callbacks, BottomQualityAdapter.OnQualityClicked {

    @BindView(R.id.video_frame)
    AspectRatioFrameLayout mVideoFrame;

    @BindView(R.id.surface_view)
    TextureView mSurfaceView;

    private String mStreamUrl;
    private Player mPlayer;
    private boolean mPlayerNeedsPrepare;
    private VideoControllerView mVideoControllerView;
    private IPlayer.Callbacks mCallbacks;
    private int mSelectedVideoTrack = -1;

    private View mBottomBar;
    private BottomSheetBehavior<ViewStubCompat> mBottomSheetBehaviour;
    private RecyclerView mBottomContainer;
    private BottomSheetDialog mBottomSheetDialog;

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
        mVideoControllerView.setCallbacks(this);
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

        if (playbackState == Player.STATE_BUFFERING && mSelectedVideoTrack < 0) {
            mTracks = getMediaFormats();
            mSelectedVideoTrack = mPlayer.getSelectedTrack(Player.TYPE_VIDEO);
        }
    }

    private ArrayList<MediaFormat> mTracks;

    @Override
    public void setCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
        mVideoControllerView.setCallbacks(mCallbacks);
    }

    private int getBestQuality() {
        return mTracks.indexOf(Collections.max(mTracks, (a, b) -> Integer.compare(a.bitrate, b.bitrate)));
    }

    private ArrayList<MediaFormat> getMediaFormats() {
        ArrayList<MediaFormat> tracks = new ArrayList<>();
        for (int i = 0; i < mPlayer.getTrackCount(Player.TYPE_VIDEO); i++) {
            tracks.add(mPlayer.getTrackFormat(Player.TYPE_VIDEO, i));
        }
        return tracks;
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

    public void setBottomBar(ViewStubCompat bottomBar) {
        bottomBar.setLayoutResource(R.layout.bottom_player_setting);
        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomBar);
        mBottomBar = bottomBar.inflate();
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

    @Override
    public void showBottomBar() {
        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mBottomSheetDialog.setContentView(R.layout.bottom_player_setting);
        TextView qualityText = (TextView) mBottomSheetDialog.findViewById(R.id.bottom_player_setting_quality);
        if (qualityText != null) {
            qualityText.setText(getString(R.string.player_bottom_quality, getSelectedQuality()));
            qualityText.setOnClickListener(view -> onQualityClicked());
        }
        mBottomSheetDialog.show();
    }

    void onQualityClicked() {
        mBottomSheetDialog.hide();
        mBottomSheetDialog.setContentView(R.layout.content_recycler);
        RecyclerView recyclerView = (RecyclerView) mBottomSheetDialog.findViewById(R.id.content_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        BottomQualityAdapter adapter = new BottomQualityAdapter(getFilteredQualities());
        adapter.setOnQualityClicked(this);
        recyclerView.setAdapter(adapter);
        mBottomSheetDialog.show();
    }

    @Override
    public void setQuality(int mediaFormat) {
        mBottomSheetDialog.hide();
        ((PlayerActivity) getActivity()).hideBottomBar();
        if (mPlayer != null && mTracks != null) {
            MediaFormat format = DataUtils.filterRedundantTracks(mTracks).get(mediaFormat);
            mPlayer.setSelectedTrack(Player.TYPE_VIDEO, mTracks.indexOf(format));
        }
    }

    @Override
    public String getSelectedQuality() {
        if (mTracks != null) {
            return String.valueOf(mTracks.get(mSelectedVideoTrack).height);
        }
        return null;
    }

    @Override
    public String[] getFilteredQualities() {
        ArrayList<MediaFormat> formats = DataUtils.filterRedundantTracks(mTracks);
        return DataUtils.getFormatNames(formats);
    }

    public void setBottomContainer(RecyclerView bottomContainer) {
        mBottomContainer = bottomContainer;
    }
}
