package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marcn.mediathek.BaseActivity;
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.stations.Station;
import com.marcn.mediathek.stations.ZdfGroup;
import com.marcn.mediathek.adapter.VideoAdapter;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.utils.LayoutTasks;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;

public class ZdfVideoFragment extends Fragment implements View.OnTouchListener {
    public static final int WIDGET_TYPE_SENDUNG_LAST = 0;
    public static final int WIDGET_TYPE_SENDUNG_MOST_POPULAR = 1;
    public static final int WIDGET_TYPE_SENDUNG_FURTHER = 2;
    public static final int WIDGET_TYPE_TIPPS = 3;

    private static final String ARG_WIDGET_TYPE = "widget-type";
    private static final String ARG_ASSET_ID = "asset-id";

    private static final int INT_UPDATE_THRESHOLD = 10;
    private static final int INT_UPDATE_COUNT = 50;

    private VideoAdapter mVideoAdapter;
    private LayoutManager mLayoutManager;

    private Station station = new ZdfGroup("zdf");

    private int mLoadedItems = 0;
    private boolean mIsLoading, mAllLoaded;
    private OnVideoInteractionListener mListener;
    private RelativeLayout.LayoutParams mScrollLayoutParams;
    private int mWindowHeight;
    private TextView mIndicator;
    private String mAssetId;
    private int mWidgetType;
    private String mBaseUrl;
    private String mHeaderTitle;

    public static ZdfVideoFragment newInstance(String assetId, int type) {
        ZdfVideoFragment fragment = new ZdfVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ASSET_ID, assetId);
        args.putInt(ARG_WIDGET_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAssetId = getArguments().getString(ARG_ASSET_ID, "");
            mWidgetType = getArguments().getInt(ARG_WIDGET_TYPE);
            if (getActivity() == null) return;
            switch (mWidgetType) {
                case WIDGET_TYPE_SENDUNG_MOST_POPULAR:
                    mBaseUrl = getString(R.string.zdf_gruppe_video_meist);
                    mHeaderTitle = getString(R.string.video_widget_header_most_popular);
                    break;
                case WIDGET_TYPE_SENDUNG_FURTHER:
                    mBaseUrl = getString(R.string.zdf_gruppe_video_weitere);
                    mHeaderTitle = getString(R.string.video_widget_header_further);
                    break;
                case WIDGET_TYPE_SENDUNG_LAST:
                    mBaseUrl = getString(R.string.zdf_gruppe_video_aktuellste);
                    mHeaderTitle = getString(R.string.video_widget_header_last);
                    break;
                case WIDGET_TYPE_TIPPS:
                    mBaseUrl = getString(R.string.zdf_gruppe_video_tipps);
                    mHeaderTitle = getString(R.string.video_widget_header_tipps);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sendungen_abisz, container, false);
        Context context = view.getContext();

        if ((getActivity()) != null)
            ((BaseActivity) getActivity()).setActionBarTitle(mHeaderTitle);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        mWindowHeight = LayoutTasks.getWindowHeight(context);

        mLayoutManager = new LayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mVideoAdapter = new VideoAdapter(new ArrayList<Episode>(), mListener);
        recyclerView.setAdapter(mVideoAdapter);

        recyclerView.addOnScrollListener(onScrollListener);

        downloadVideos();
        return view;
    }

    private void downloadVideos() {
        if (getActivity() == null || mIsLoading || mAllLoaded) return;
        setIsLoading(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Episode> episodes;
                episodes = fetchVideoList(INT_UPDATE_COUNT, mLoadedItems);
                if (getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (episodes == null || episodes.isEmpty()) {
                            mAllLoaded = true;
                            setIsLoading(false);
                            return;
                        }

                        mVideoAdapter.updateValues(episodes);
                        mLoadedItems += episodes.size();

                        if (mWidgetType == ZdfVideoFragment.WIDGET_TYPE_SENDUNG_LAST)
                            mVideoAdapter.addHeaders();
                        setIsLoading(false);
                    }
                });
            }
        }).start();
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
    public boolean onTouch(View v, MotionEvent event) {
        float normalizedPosition = 1.7f * (event.getY() - mWindowHeight / 2) + mWindowHeight / 2;
        int fastScrollPosition = (int) (mVideoAdapter.getItemCount() * normalizedPosition / mWindowHeight);
        fastScrollPosition = Math.max(0, Math.min(fastScrollPosition, mVideoAdapter.getItemCount() - 1));
        mLayoutManager.scrollToPosition(fastScrollPosition);

        // Indicator
        mScrollLayoutParams.topMargin = (int) event.getY() - mScrollLayoutParams.height;
        mIndicator.setLayoutParams(mScrollLayoutParams);

        int firstVisible = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (mVideoAdapter.getMember(firstVisible) != null) {
            mIndicator.setText(mVideoAdapter.getMember(firstVisible));
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && firstVisible >= 1)
            mIndicator.setVisibility(View.VISIBLE);
        if (event.getAction() == MotionEvent.ACTION_UP)
            mIndicator.setVisibility(View.INVISIBLE);

        return true;
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

    private void setIsLoading(boolean loading) {
        mIsLoading = loading;
        if (mVideoAdapter != null)
            mVideoAdapter.setLoading(loading);
    }

    private ArrayList<Episode> fetchVideoList(int count, int offset) {
        if (station instanceof ZdfGroup) {
            return ((ZdfGroup) station).getMostRecentEpisodes(offset, count, Integer.parseInt(mAssetId));
        }
        return null;
    }
}
