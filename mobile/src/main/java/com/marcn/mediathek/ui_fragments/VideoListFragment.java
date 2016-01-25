package com.marcn.mediathek.ui_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.VideoAdapter;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.utils.DateFormat;
import com.marcn.mediathek.utils.ZdfMediathekData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class VideoListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int INT_GRID_UPDATE_THRESHOLD = 15;
    private static final int INT_GRID_UPDATE_COUNT = 100;
    private int mColumnCount = 2;
    private int mLoadedItems = 0;
    private VideoAdapter mVideoAdapter;
    private GridLayoutManager mLayoutManager;

    private Calendar mDay, mLastDay;
    private boolean mIsLoading;
    private OnVideoInteractionListener mListener;

    public VideoListFragment() {
    }

    public static VideoListFragment newInstance(int columnCount) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_livestream_list, container, false);
        if (!(view instanceof RecyclerView)) return view;

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;

        mLayoutManager = new GridLayoutManager(context, mColumnCount);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mVideoAdapter.getItemViewType(position) == VideoAdapter.TYPE_HEADER)
                    return mColumnCount;
                return 1;
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);

        mVideoAdapter = new VideoAdapter(new ArrayList<Video>(), mListener);
        recyclerView.setAdapter(mVideoAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem >= mVideoAdapter.getItemCount() - INT_GRID_UPDATE_THRESHOLD) {
                    downloadMissedChannels(mLoadedItems, INT_GRID_UPDATE_COUNT);
                }
            }
        });

        mDay = new GregorianCalendar();
        mDay.add(Calendar.DAY_OF_YEAR, 1);
        mLastDay = new GregorianCalendar();
        mLastDay.add(Calendar.DAY_OF_YEAR, -7);

        mVideoAdapter.addHeadline(mDay);

        downloadMissedChannels(mLoadedItems, INT_GRID_UPDATE_COUNT);
        return view;
    }

    private void downloadMissedChannels(final int offset, final int count) {
        if (mIsLoading) return;
        mLoadedItems += count;
        mIsLoading = true;
        SimpleDateFormat s = new SimpleDateFormat("ddMMyy");
        final String day = s.format(mDay.getTime());
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Video> videos = ZdfMediathekData.getMissedShows(getContext(), offset, count, day, day);
                if (getActivity() == null || videos == null) return;
                if (videos.isEmpty() && mDay.after(mLastDay)) {
                    mDay.add(Calendar.DAY_OF_MONTH, -1);
                    if (mDay.get(Calendar.DAY_OF_YEAR) == new GregorianCalendar().get(Calendar.DAY_OF_YEAR))
                        videos.add(new Video("Heute"));
                    else
                        videos.add(new Video(DateFormat.calendarToHeadlineFormat(mDay)));
                    mLoadedItems = 0;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoAdapter.updateValues(videos);
                        mIsLoading = false;
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
}
