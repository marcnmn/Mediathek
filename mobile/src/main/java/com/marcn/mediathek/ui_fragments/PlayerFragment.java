package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.Util;
import com.marcn.mediathek.R;
import com.marcn.mediathek.player.ExtractorRendererBuilder;
import com.marcn.mediathek.player.HlsRendererBuilder;
import com.marcn.mediathek.player.Player;
import com.marcn.mediathek.player.Player.RendererBuilder;
import com.marcn.mediathek.player.VideoControllerView;
import com.marcn.mediathek.utils.LayoutTasks;

import java.io.FileNotFoundException;

public class PlayerFragment extends Fragment implements Player.Listener, TextureView.SurfaceTextureListener, Transition.TransitionListener {

    public static final int     TYPE_DASH = 0;
    public static final int     TYPE_SS = 1;
    public static final int     TYPE_HLS = 2;
    public static final int     TYPE_OTHER = 3;

    private static final String EXT_DASH = ".mpd";
    private static final String EXT_SS = ".ism";
    private static final String EXT_HLS = ".m3u8";

    private static final int    MSG_MAX_TIME = 1000;
    private static final int    MSG_HIDE_THUMBNAIL = 1;
    private static final int    SHOW_PROGRESS = 2;

    private static final String ARG_STREAM_URL = "stream-url";
    private String mStreamUrl;

    private ImageView shutterView;
    private AspectRatioFrameLayout videoFrame;
    private TextureView surfaceView;
    private View progressBar;
    private Player player;
    private boolean playerNeedsPrepare;
    private VideoControllerView mediaController;

    private boolean enableBackgroundAudio;
    private boolean mTransitionEnded;

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
            // mStreamUrl = "http://nrodl.zdf.de/de/zdf/14/12/141211_osmanen1_inf_1496k_p18v11.webm";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        Context context = view.getContext();

        videoFrame = (AspectRatioFrameLayout) view.findViewById(R.id.video_frame);
//        videoFrame = (AspectRatioFrameLayout) view.findViewById(R.id.video_frame);
        shutterView = (ImageView) view.findViewById(R.id.shutter);
        progressBar = view.findViewById(R.id.progress_bar_player);

        surfaceView = (TextureView) view.findViewById(R.id.surface_view);
        surfaceView.setSurfaceTextureListener(this);

        mediaController = new VideoControllerView(context);
        mediaController.setAnchorView(videoFrame);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((FrameLayout)view).setTransitionGroup(true);
            videoFrame.setTransitionGroup(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().getEnterTransition().addListener(this);
        } else {
            mTransitionEnded = true;
        }

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoFrame.getLayoutParams();
        layoutParams.height = (int) (LayoutTasks.getWindowWidth(context) * 9.0 / 16);
        videoFrame.setLayoutParams(layoutParams);

        getIntentThumbnail(context);
        enableBackgroundAudio = true;

        if (isInLandscape() && !isImmersiveEnabled())
            hideSystemUI();

        videoFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //videoFrame.performClick();
                }
                return true;
            }
        });

        return view;
    }

    private void getIntentThumbnail(Context context) {
        if (context == null) return;
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.openFileInput("thumbnail"));
            shutterView.setImageBitmap(bitmap);
            Palette p = Palette.from(bitmap).generate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setStatusBarColor(p.getDarkVibrantColor(ContextCompat.getColor(context, R.color.colorPrimaryDark)));
            }
        } catch (FileNotFoundException | OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public void startPlayBack() {
//        contentType = intent.getIntExtra(CONTENT_TYPE_EXTRA,
//                inferContentType(contentUri, intent.getStringExtra(CONTENT_EXT_EXTRA)));
//        contentId = intent.getStringExtra(CONTENT_ID_EXTRA);
//        provider = intent.getStringExtra(PROVIDER_EXTRA);
//        configureSubtitleView();
        preparePlayer(true);
    }

    private RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(getActivity(), "ExoPlayerDemo");
        Uri uri = Uri.parse(mStreamUrl);
        int contentType = inferContentType(uri, null);
        switch (contentType) {
//            case TYPE_SS:
//                return new SmoothStreamingRendererBuilder(this, null, contentUri.toString(),
//                        new SmoothStreamingTestMediaDrmCallback());
//            case TYPE_DASH:
//                return new DashRendererBuilder(this, userAgent, contentUri.toString(),
//                        new WidevineTestMediaDrmCallback(contentId, provider));
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
//            eventLogger = new EventLogger();
//            eventLogger.startSession();
//            player.addListener(eventLogger);
//            player.setInfoListener(eventLogger);
//            player.setInternalErrorListener(eventLogger);
//            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
//            debugViewHelper.start();
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
    public void onResume() {
        super.onResume();
        if (player != null)
            player.setBackgrounded(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        shutterView.setVisibility(View.VISIBLE);

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
        switch(playbackState) {
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
            case ExoPlayer.STATE_READY:
                if (mTransitionEnded)
                    hideThumbnail();
//                text += "ready";
//                mHandler.sendEmptyMessage(MSG_HIDE_THUMBNAIL);
//
// new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        TransitionManager.beginDelayedTransition(videoFrame, new Fade());
//                        shutterView.setVisibility(View.GONE);
//                        progressBar.setVisibility(View.GONE);
//                        mediaController.show();
//                    }
//                }, 300);
//                break;
            default:
//                text += "unknown";
                break;
        }
//        playerStateTextView.setText(text);
//        updateButtonVisibilities();
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
//        shutterView.setVisibility(View.GONE);
        videoFrame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }


    public void releasePlayer() {
        if (player != null) {
//            debugViewHelper.stop();
//            debugViewHelper = null;
//            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
//            eventLogger.endSession();
//            eventLogger = null;
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

    private void toggleControlsVisibility()  {
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
                        hideThumbnail();
                        msg = obtainMessage(MSG_HIDE_THUMBNAIL);
                        sendMessageDelayed(msg, hideFreq);
                    }
                    if (shutterView.getVisibility() == View.GONE && progressBar.getVisibility() == View.GONE)
                        mediaController.show();
                    break;
            }
        }
    };

    private void hideThumbnail(){
        TransitionManager.beginDelayedTransition(videoFrame, new Fade());
        shutterView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showControls() {
        mediaController.show();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startPlayBack();
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

    @Override
    public void onTransitionStart(Transition transition) {

    }

    @Override
    public void onTransitionEnd(Transition transition) {
        mTransitionEnded = true;
        if (player.getPlaybackState() == ExoPlayer.STATE_READY)
            mHandler.sendEmptyMessage(MSG_HIDE_THUMBNAIL);
    }

    @Override
    public void onTransitionCancel(Transition transition) {

    }

    @Override
    public void onTransitionPause(Transition transition) {

    }

    @Override
    public void onTransitionResume(Transition transition) {

    }
}
