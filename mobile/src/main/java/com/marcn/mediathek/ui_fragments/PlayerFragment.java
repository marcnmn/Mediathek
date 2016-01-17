package com.marcn.mediathek.ui_fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.marcn.mediathek.R;

public class PlayerFragment extends Fragment {

    private static final String ARG_STREAM_URL = "stream-url";
    private String mStreamUrl;

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
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        VideoView videoView = (VideoView) view.findViewById(R.id.videoView);
        videoView.setVideoPath(mStreamUrl);
        //videoView.setVideoPath("http://nrodl.zdf.de/none/zdf/14/03/140310_grossbritannien_oben_2_neo_1496k_p18v11.webm");
        videoView.start();

        if (isInLandscape() && !isImmersiveEnabled())
            hideSystemUI();

        return view;
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
        int uiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
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
}
