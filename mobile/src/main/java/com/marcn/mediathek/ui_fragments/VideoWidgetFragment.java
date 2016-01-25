package com.marcn.mediathek.ui_fragments;

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
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.VideoWidgetAdapter;
import com.marcn.mediathek.base_objects.Sendung;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.utils.ZdfMediathekData;

import java.io.IOException;
import java.util.ArrayList;

public class VideoWidgetFragment extends Fragment {
    public static final int WIDGET_TYPE_LAST = 0;
    public static final int WIDGET_TYPE_MOST_POPULAR = 1;
    public static final int WIDGET_TYPE_FURTHER = 2;

    private static final String ARG_SENDUNG_JSON = "sendung-json";
    private static final String ARG_WIDGET_TYPE = "widget-type";
    private static final int VIDEO_ITEM_COUNT = 10;


    private VideoWidgetAdapter mVideoAdapter;
    private LinearLayoutManager mLayoutManager;
    private OnVideoInteractionListener mListener;

    private Sendung mSendung;
    private int mWidgetType;
    private String mBaseUrl;
    private String mHeaderTitle;
    private RelativeLayout mRootView;

    public static VideoWidgetFragment newInstance(Sendung sendung, int type) {
        VideoWidgetFragment fragment = new VideoWidgetFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String json = gson.toJson(sendung);
        args.putString(ARG_SENDUNG_JSON, json);
        args.putInt(ARG_WIDGET_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Gson gson = new Gson();
            String sendung = getArguments().getString(ARG_SENDUNG_JSON, "");
            mSendung = gson.fromJson(sendung, Sendung.class);

            mWidgetType = getArguments().getInt(ARG_WIDGET_TYPE);
            if (getActivity() == null) return;
            switch (mWidgetType) {
                case WIDGET_TYPE_MOST_POPULAR:
                    mBaseUrl = getString(R.string.zdf_gruppe_video_meist);
                    mHeaderTitle = getString(R.string.video_widget_header_most_popular);
                    break;
                case WIDGET_TYPE_FURTHER:
                    mBaseUrl = getString(R.string.zdf_gruppe_video_weitere);
                    mHeaderTitle = getString(R.string.video_widget_header_further);
                    break;
                default:
                    mBaseUrl = getString(R.string.zdf_gruppe_video_aktuellste);
                    mHeaderTitle = getString(R.string.video_widget_header_last);
            }
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

        mVideoAdapter = new VideoWidgetAdapter(new ArrayList<Video>(), mListener);
        recyclerView.setAdapter(mVideoAdapter);

        if (mRootView.findViewById(R.id.textHeader) != null)
            ((TextView) mRootView.findViewById(R.id.textHeader)).setText(mHeaderTitle);

        recyclerView.setNestedScrollingEnabled(false);

        downloadVideos();
        return mRootView;
    }

    private void downloadVideos() {
        if (getActivity() == null || mBaseUrl == null || mSendung == null) return;
        final String request = mBaseUrl + "?maxLength=" + VIDEO_ITEM_COUNT + "&id=" + mSendung.assetId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Video> videos;
                try {
                    videos = ZdfMediathekData.fetchVideoList(request);
                    if (getActivity() == null || videos == null) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVideoAdapter.updateValues(videos);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                TransitionManager.beginDelayedTransition(mRootView, new Slide());
                                mRootView.findViewById(R.id.recyclerViewVideos).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (IOException ignored) {
                }
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
}
