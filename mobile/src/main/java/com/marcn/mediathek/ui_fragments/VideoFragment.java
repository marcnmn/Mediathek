package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.VideoAdapter;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.stations.Station;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

public class VideoFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_CHANNEL_TITLE = "channel-title";
    private static final String ARG_ASSET_ID = "asset-id";

    private static final int INT_UPDATE_THRESHOLD = 10;
    private static final int INT_UPDATE_COUNT = 50;

    //    private VideoWidgetAdapter mVideoAdapter;
    private VideoAdapter mVideoAdapter;
    private LayoutManager mLayoutManager;
    private OnVideoInteractionListener mListener;

    private Station mStation;
    private String mHeaderTitle;
    private String mAssetId;
    private RelativeLayout mRootView;
    private boolean mIsLoading;
    private boolean mAllLoaded;
    private int mLoadedItems;
    private RecyclerView mRecyclerView;
    private boolean nestedScrolling = true;

    public static VideoFragment newInstance(String channelTitle, String assetId, String widgetTitle) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNEL_TITLE, channelTitle);
        args.putString(ARG_TITLE, widgetTitle);
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
            mHeaderTitle = getArguments().getString(ARG_TITLE, null);
            mAssetId = getArguments().getString(ARG_ASSET_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = (RelativeLayout) inflater.inflate(R.layout.fragment_sendungen_abisz, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.list);

        if ((getActivity()) != null && mHeaderTitle != null)
            ((BaseActivity) getActivity()).setActionBarTitle(mHeaderTitle);

        mLayoutManager = new LayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mVideoAdapter = new VideoAdapter(new ArrayList<>(), mListener);
        mRecyclerView.setAdapter(mVideoAdapter);

        mRecyclerView.addOnScrollListener(onScrollListener);
        mRecyclerView.setNestedScrollingEnabled(nestedScrolling);

        downloadVideos();
        return mRootView;
    }

    private void downloadVideos() {
        if (getActivity() == null || mIsLoading || mAllLoaded) return;
        setIsLoading(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Episode> episodes = fetchEpisodes();
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (episodes == null || episodes.isEmpty()) {
                            mAllLoaded = true;
                            setIsLoading(false);
                            return;
                        }
//                        mVideoAdapter.updateValues(episodes);
                        mLoadedItems += episodes.size();
//                        if (mWidgetType == ZdfVideoFragment.WIDGET_TYPE_SENDUNG_LAST)
//                            mVideoAdapter.addHeaders();
                        setIsLoading(false);
                    }
                });
            }
        }).start();
    }

    private ArrayList<Episode> fetchEpisodes() {
        if (mStation == null) return null;
        if (mHeaderTitle == null && mAssetId != null)
            return mStation.fetchSeriesEpisodes(mAssetId, INT_UPDATE_COUNT, mLoadedItems);
        else if (mHeaderTitle != null && mAssetId == null)
            return mStation.fetchCategoryEpisodes(mHeaderTitle, INT_UPDATE_COUNT, mLoadedItems);
        else
            return null;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            if (lastVisibleItem >= mVideoAdapter.getItemCount() - INT_UPDATE_THRESHOLD)
                downloadVideos();
        }
    };

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

    private void setIsLoading(boolean loading) {
        mIsLoading = loading;
        if (mVideoAdapter != null)
            mVideoAdapter.setLoading(loading);
    }

    public void setNestedScrollingEnabled(boolean b) {
        nestedScrolling = b;
        if (mRecyclerView != null)
            mRecyclerView.setNestedScrollingEnabled(nestedScrolling);
    }
}
