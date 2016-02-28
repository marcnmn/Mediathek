package com.marcn.mediathek.ui_fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.marcn.mediathek.BaseActivity;
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.stations.Station;
import com.marcn.mediathek.stations.ZdfGroup;
import com.marcn.mediathek.adapter.VideoWidgetAdapter;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.StationUtils.ZdfUtils;

import java.io.IOException;
import java.util.ArrayList;

public class VideoWidgetFragment extends Fragment {
    public static final int WIDGET_TYPE_SENDUNG_LAST = 0;
    public static final int WIDGET_TYPE_SENDUNG_MOST_POPULAR = 1;
    public static final int WIDGET_TYPE_SENDUNG_FURTHER = 2;
    public static final int WIDGET_TYPE_TIPPS = 3;

    private static final String ARG_OBJECT_JSON = "object-json";
    private static final String ARG_WIDGET_TYPE = "widget-type";
    private static final String ARG_WIDGET_TITLE = "widget-title";
    private static final String ARG_CHANNEL_TITLE = "channel-title";
    private static final String ARG_WIDGET_URL = "widget-url";
    private static final String ARG_ASSET_ID = "asset-id";

    private static final int VIDEO_ITEM_COUNT = 10;

    private VideoWidgetAdapter mVideoAdapter;
    private LinearLayoutManager mLayoutManager;
    private OnVideoInteractionListener mListener;

    private Station mStation;

    private Series mSeries;
    private int mWidgetType;
    private String mBaseUrl;
    private String mHeaderTitle;
    private String mAssetId;
    private RelativeLayout mRootView;

    public static VideoWidgetFragment newInstance(String assetId, int type) {
        VideoWidgetFragment fragment = new VideoWidgetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WIDGET_TITLE, assetId);
        args.putString(ARG_ASSET_ID, assetId);
        args.putInt(ARG_WIDGET_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public static VideoWidgetFragment newInstance(String channelTitle, String assetId, String widgetTitle) {
        VideoWidgetFragment fragment = new VideoWidgetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNEL_TITLE, channelTitle);
        args.putString(ARG_WIDGET_TITLE, widgetTitle);
        args.putString(ARG_ASSET_ID, assetId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Gson gson = new Gson();
            String sendung = getArguments().getString(ARG_OBJECT_JSON, "");
            mSeries = gson.fromJson(sendung, Series.class);
            if (mSeries != null)
                mAssetId = mSeries.assetId + "";

            String title = getArguments().getString(ARG_CHANNEL_TITLE, "");
            mStation = Station.createStation(title);
            mHeaderTitle = getArguments().getString(ARG_WIDGET_TITLE, "");
            mAssetId = getArguments().getString(ARG_ASSET_ID, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_sidescroll_videos, container, false);

        Context context = mRootView.getContext();
        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerViewVideos);
        mRootView.findViewById(R.id.recyclerViewVideos).setVisibility(View.GONE);

        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);

        mVideoAdapter = new VideoWidgetAdapter(new ArrayList<Episode>(), mListener);
        recyclerView.setAdapter(mVideoAdapter);

        if (mRootView.findViewById(R.id.textHeader) != null)
            ((TextView) mRootView.findViewById(R.id.textHeader)).setText(mHeaderTitle);

        View buttonMore = mRootView.findViewById(R.id.buttonMore);
        if (buttonMore != null)
            buttonMore.setOnClickListener(new View.OnClickListener() {
                BaseActivity activity = (BaseActivity) getActivity();
                @Override
                public void onClick(View v) {
                    activity.onMoreClicked(mAssetId, mWidgetType);
                }
            });

        // TODO nested scrolling in a good manner
        recyclerView.setNestedScrollingEnabled(false);

        downloadVideos2();
        return mRootView;
    }

    private void downloadVideos() {
        if (getActivity() == null || mBaseUrl == null) return;
        final String request = mBaseUrl + "?maxLength=" + VIDEO_ITEM_COUNT + "&id=" + mAssetId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Episode> episodes;
                try {
                    episodes = ZdfUtils.fetchVideoList(request);
                    if (getActivity() == null || episodes == null) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVideoAdapter.updateValues(episodes);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                TransitionManager.beginDelayedTransition(mRootView, new Slide());
                                mRootView.findViewById(R.id.recyclerViewVideos).setVisibility(View.VISIBLE);
                                mRootView.findViewById(R.id.buttonMore).setVisibility(View.VISIBLE);
                                mRootView.findViewById(R.id.textHeader).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    private void downloadVideos2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Episode> episodes = fetchVideoList();
                final ArrayList<Episode> episodes1
                        = mStation.fetchWidgetEpisodes(mHeaderTitle, mAssetId, VIDEO_ITEM_COUNT);
                if (getActivity() == null || episodes == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoAdapter.updateValues(episodes);
                        animateInFromBottom();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVideoInteractionListener) {
            mListener = (OnVideoInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private ArrayList<Episode> fetchVideoList() {
        if (mStation instanceof ZdfGroup) {
            return ((ZdfGroup) mStation).getMostRecentEpisodes(0, VIDEO_ITEM_COUNT, Integer.parseInt(mAssetId));
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateInFromBottom() {
        TransitionManager.beginDelayedTransition(mRootView, new Slide());
        mRootView.findViewById(R.id.recyclerViewVideos).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.buttonMore).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.textHeader).setVisibility(View.VISIBLE);
    }
}
