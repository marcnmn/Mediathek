package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.Util;
import com.marcn.mediathek.R;
import com.marcn.mediathek.player.ExtractorRendererBuilder;
import com.marcn.mediathek.player.HlsRendererBuilder;
import com.marcn.mediathek.player.Player;
import com.marcn.mediathek.player.Player.RendererBuilder;
import com.marcn.mediathek.player.VideoControllerView;

public class PlayerFragment extends Fragment implements Player.Listener,
        TextureView.SurfaceTextureListener {

    public static final int TYPE_DASH = 0;
    public static final int TYPE_SS = 1;
    public static final int TYPE_HLS = 2;
    public static final int TYPE_OTHER = 3;

    private static final String EXT_DASH = ".mpd";
    private static final String EXT_SS = ".ism";
    private static final String EXT_HLS = ".m3u8";

    private static final int MSG_MAX_TIME = 1000;
    private static final int MSG_HIDE_THUMBNAIL = 1;
    private static final int SHOW_PROGRESS = 2;

    private static final String ARG_STREAM_URL = "stream-url";
    private String mStreamUrl;

    //    private ImageView shutterView;
    private AspectRatioFrameLayout videoFrame;
    private TextureView surfaceView;
    private View progressBar;
    private Player player;
    private boolean playerNeedsPrepare;
    private VideoControllerView mediaController;

    private boolean enableBackgroundAudio;
    private boolean mTransitionEnded;
    private View mRootView;

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(String url) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STREAM_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStreamUrl = getArguments().getString(ARG_STREAM_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_player, container, false);
        Context context = mRootView.getContext();

        videoFrame = (AspectRatioFrameLayout) mRootView.findViewById(R.id.video_frame);
        progressBar = mRootView.findViewById(R.id.progress_bar_player);

        surfaceView = (TextureView) mRootView.findViewById(R.id.surface_view);
        surfaceView.setSurfaceTextureListener(this);

        mediaController = new VideoControllerView(context);
        mediaController.setAnchorView(videoFrame);

        if (isInLandscape() && !isImmersiveEnabled()) {
            hideSystemUI();
        }

        videoFrame.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                toggleControlsVisibility();
            }
            return true;
        });

        return mRootView;
    }

    public void setStreamUrl(String streamUrl) {
        mStreamUrl = streamUrl;
    }

    private RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(getActivity(), "ExoPlayerDemo");
        Uri uri = Uri.parse(mStreamUrl);
        int contentType = inferContentType(uri, null);
        switch (contentType) {
            case TYPE_HLS:
                return new HlsRendererBuilder(getContext(), userAgent, mStreamUrl);
            case TYPE_OTHER:
                return new ExtractorRendererBuilder(getContext(), userAgent, uri);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            player = new Player(getRendererBuilder());
            playerNeedsPrepare = true;
            player.addListener(this);
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }

        SurfaceTexture surfaceTexture = surfaceView.getSurfaceTexture();
        Surface surface = new Surface(surfaceTexture);
        player.setSurface(surface);
        player.setPlayWhenReady(playWhenReady);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isInLandscape() && !isImmersiveEnabled())
            hideSystemUI();
        if (!isInLandscape() && isImmersiveEnabled())
            hideSystemUI();
    }

    private boolean isInLandscape() {
        return getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void hideSystemUI() {
        int newUiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getActivity().getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private boolean isImmersiveEnabled() {
        if (getActivity() == null) return false;
        int uiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        return ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (player != null)
            player.setBackgrounded(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!enableBackgroundAudio) {
            releasePlayer();
        } else {
            player.setBackgrounded(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            mediaController.show(Integer.MAX_VALUE);
        }
//        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch (playbackState) {
            case ExoPlayer.STATE_BUFFERING:
//                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
//                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
//                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
//                text += "preparing";
                break;
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        videoFrame.setAspectRatio(height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }


    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private static int inferContentType(Uri uri, String fileExtension) {
        String lastPathSegment = !TextUtils.isEmpty(fileExtension) ? "." + fileExtension
                : uri.getLastPathSegment();
        if (lastPathSegment == null) {
            return TYPE_OTHER;
        } else if (lastPathSegment.endsWith(EXT_DASH)) {
            return TYPE_DASH;
        } else if (lastPathSegment.endsWith(EXT_SS)) {
            return TYPE_SS;
        } else if (lastPathSegment.endsWith(EXT_HLS)) {
            return TYPE_HLS;
        } else {
            return TYPE_OTHER;
        }
    }

    private void toggleControlsVisibility() {
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            showControls();
        }
    }

    @SuppressWarnings("HandlerLeak")
    private final Handler mHandler = new Handler() {
        int hideMaxTime = 1000;
        int hideFreq = 50;
        int time = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HIDE_THUMBNAIL:
                    if (time <= hideMaxTime) {
                        time += hideFreq;
                        msg = obtainMessage(MSG_HIDE_THUMBNAIL);
                        sendMessageDelayed(msg, hideFreq);
                    }
//                    if (shutterView.getVisibility() == View.GONE && progressBar.getVisibility() == View.GONE)
                    mediaController.show();
//                    break;
            }
        }
    };

    private void showControls() {
        mediaController.show();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        preparePlayer(true);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void updateLayout(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((FrameLayout.LayoutParams) videoFrame.getLayoutParams()).gravity = Gravity.CENTER;
        } else {
            ((FrameLayout.LayoutParams) videoFrame.getLayoutParams()).gravity = Gravity.TOP;
        }
    }
}
