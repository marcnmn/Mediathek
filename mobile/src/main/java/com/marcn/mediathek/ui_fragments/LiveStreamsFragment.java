package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.BaseActivity;
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.LiveStreamAdapter;
import com.marcn.mediathek.adapter.LiveStreamAdapter2;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.base_objects.LiveStreams;
import com.marcn.mediathek.stations.Station;
import com.marcn.mediathek.stations.ZdfGroup;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.EpgUtils;
import com.marcn.mediathek.utils.XmlParser;

import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LiveStreamsFragment extends Fragment {
    public static String FRAGMENT_TAG = "livestream-fragment";

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnVideoInteractionListener mListener;
    private LiveStreams mLiveStreams;
    private LiveStreamAdapter2 mLiveStreamAdapter;

    public LiveStreamsFragment() {
    }

    public static LiveStreamsFragment newInstance(int columnCount) {
        LiveStreamsFragment fragment = new LiveStreamsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_livestream_list, container, false);
        if (!(view instanceof RecyclerView)) return view;

        Context context = view.getContext();
        RecyclerView mRecyclerView = (RecyclerView) view;

        if ((getActivity()) != null)
            ((BaseActivity) getActivity()).setActionBarResource(R.string.action_title_live_streams);

        mColumnCount = context.getResources().getInteger(R.integer.live_streams);

        GridLayoutManager mLayoutManager = new GridLayoutManager(context, mColumnCount);
        if (mColumnCount <= 2)
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Station s = mLiveStreamAdapter.getItem(position);
                if (s == null) return 1;
                if (s.getTitle().equals(Constants.TITLE_CHANNEL_ZDF)
                        || s.getTitle().equals(Constants.TITLE_CHANNEL_ARTE)
                        || s.getTitle().equals(Constants.TITLE_CHANNEL_ARD)
                        || s.getTitle().equals(Constants.TITLE_CHANNEL_3SAT)
                        || s.getTitle().equals(Constants.TITLE_CHANNEL_KIKA))
                    return mColumnCount;
                else return 1;
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<Station> stations = Constants.getAllChannels();
        for (Station station : stations) {
            if (station == null) continue;
            station.fetchCurrentEpisode()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(e -> updateStation(station, e));
        }

        if (mLiveStreamAdapter == null) {
            mLiveStreamAdapter = new LiveStreamAdapter2(stations, mListener);
            mRecyclerView.setAdapter(mLiveStreamAdapter);
        } else {
            mRecyclerView.setAdapter(mLiveStreamAdapter);
        }

        return view;
    }

    private void updateStation(Station station, Episode episode) {
        if (mLiveStreamAdapter != null)
            mLiveStreamAdapter.updateStation(station, episode);
    }

    @Override
    public void onResume() {
        if (mLiveStreamAdapter != null && mListener != null) {
            mLiveStreamAdapter.setVideoClickListener(mListener);
        }
        super.onResume();
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
