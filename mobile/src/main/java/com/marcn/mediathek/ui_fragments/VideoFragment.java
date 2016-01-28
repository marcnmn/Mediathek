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
import com.marcn.mediathek.adapter.VideoAdapter2;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.utils.DateFormat;
import com.marcn.mediathek.utils.LayoutTasks;
import com.marcn.mediathek.utils.ZdfMediathekData;
import com.tonicartos.superslim.LayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class VideoFragment extends Fragment implements View.OnTouchListener {

    private static final int INT_UPDATE_THRESHOLD = 15;
    private static final int INT_UPDATE_COUNT = 100;

    private VideoAdapter2 mVideoAdapter;
    private LayoutManager mLayoutManager;

    private int mLoadedItems = 0;
    private Calendar mDay, mLastDay;
    private boolean mIsLoading;
    private OnVideoInteractionListener mListener;
    private RelativeLayout.LayoutParams mScrollLayoutParams;
    private int mWindowHeight;
    private TextView mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDay = new GregorianCalendar();
        mDay.add(Calendar.DAY_OF_YEAR, 1);
        mLastDay = new GregorianCalendar();
        mLastDay.add(Calendar.DAY_OF_YEAR, -7);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sendungen_abisz, container, false);
        Context context = view.getContext();

        if ((getActivity()) != null)
            ((BaseActivity) getActivity()).setActionBarTitle(R.string.action_title_sendungen_abisz);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        View scrollArea = view.findViewById(R.id.fastScrollArea);
        mWindowHeight = LayoutTasks.getWindowHeight(context);
        mIndicator = (TextView) view.findViewById(R.id.indicator);
        mScrollLayoutParams = (RelativeLayout.LayoutParams) mIndicator.getLayoutParams();

        mLayoutManager = new LayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mVideoAdapter = new VideoAdapter2(new ArrayList<Video>(), mListener);
        recyclerView.setAdapter(mVideoAdapter);
        mVideoAdapter.updateValues(Video.createHeaderVideo(DateFormat.calendarToHeadlineFormat(mDay)));

        recyclerView.addOnScrollListener(onScrollListener);
        scrollArea.setOnTouchListener(this);

        downloadVideos(mLoadedItems, INT_UPDATE_COUNT);
        return view;
    }

    private void downloadVideos(final int offset, final int count) {
        if (mIsLoading) return;
        mLoadedItems += count;
        mIsLoading = true;
        mVideoAdapter.setLoading(true);
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
                        videos.add(Video.createHeaderVideo("Heute"));
                    else
                        videos.add(Video.createHeaderVideo(DateFormat.calendarToHeadlineFormat(mDay)));
                    mLoadedItems = 0;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVideoAdapter.updateValues(videos);
                        mVideoAdapter.setLoading(false);
                        mIsLoading = false;
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
            if (lastVisibleItem >= mVideoAdapter.getItemCount() - INT_UPDATE_THRESHOLD) {
                downloadVideos(mLoadedItems, INT_UPDATE_COUNT);
            }
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
}
