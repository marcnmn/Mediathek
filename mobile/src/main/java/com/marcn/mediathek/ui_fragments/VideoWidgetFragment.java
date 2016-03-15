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
//    private static final String ARG_WIDGET_URL = "widget-url";
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
            String title = getArguments().getString(ARG_CHANNEL_TITLE, "");
            mStation = Station.createStation(title);
            mHeaderTitle = getArguments().getString(ARG_WIDGET_TITLE, "");
            mAssetId = getArguments().getString(ARG_ASSET_ID, null);
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
                    activity.onMoreClicked2(mStation.getTitle(), mAssetId, mHeaderTitle);
                }
            });

        // TODO nested scrolling in a good manner
        recyclerView.setNestedScrollingEnabled(false);

        downloadVideos();
        return mRootView;
    }

    private void downloadVideos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Episode> episodes
                        = mStation.fetchWidgetEpisodes(mHeaderTitle, mAssetId, VIDEO_ITEM_COUNT);
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (episodes != null) {
                            mVideoAdapter.updateValues(episodes);
                            animateInFromBottom();
                        } else {
                            removeFragment();
                        }
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

    private void removeFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateInFromBottom() {
        TransitionManager.beginDelayedTransition(mRootView, new Slide());
        mRootView.findViewById(R.id.recyclerViewVideos).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.buttonMore).setVisibility(View.VISIBLE);
        mRootView.findViewById(R.id.textHeader).setVisibility(View.VISIBLE);
    }
}
