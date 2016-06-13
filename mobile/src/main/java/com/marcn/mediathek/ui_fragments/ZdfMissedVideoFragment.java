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

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.VideoAdapter;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.model.pages.ZdfPage;
import com.marcn.mediathek.model.zdf.ZdfEpisode;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.stations.ZdfGroup;
import com.marcn.mediathek.utils.Constants;
import com.marcn.mediathek.utils.FormatTime;
import com.marcn.mediathek.utils.LayoutTasks;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ZdfMissedVideoFragment extends Fragment implements View.OnTouchListener, Injector<ActivityComponent> {
    private static final int INT_UPDATE_THRESHOLD = 15;
    private static final int INT_UPDATE_COUNT = 100;

    private VideoAdapter mVideoAdapter;
    private LayoutManager mLayoutManager;

    private int mLoadedItems = 0;
    private Calendar mDay, mLastDay;
    private boolean mIsLoading;
    private OnVideoInteractionListener mListener;
    private RelativeLayout.LayoutParams mScrollLayoutParams;
    private int mWindowHeight;
    private TextView mIndicator;

    private ZdfGroup zdf = new ZdfGroup(Constants.TITLE_CHANNEL_ZDF);

    @Inject
    Context mApplicationContext;

    @Inject
    ZdfInteractor mZdfInteractor;

    private CompositeSubscription mSubscription = new CompositeSubscription();

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
        InjectHelper.setupPage(this);

        if ((getActivity()) != null)
            ((BaseActivity) getActivity()).setActionBarResource(R.string.action_title_sendung_verpasst);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        View scrollArea = view.findViewById(R.id.fastScrollArea);
        mWindowHeight = LayoutTasks.getWindowHeight(context);
        mIndicator = (TextView) view.findViewById(R.id.indicator);
        mScrollLayoutParams = (RelativeLayout.LayoutParams) mIndicator.getLayoutParams();

        mLayoutManager = new LayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mVideoAdapter = new VideoAdapter(new ArrayList<ZdfEpisode>(), mListener);
        recyclerView.setAdapter(mVideoAdapter);
//        mVideoAdapter.updateValues(Episode.createHeader(FormatTime.getMissedHeader(mDay)));

        recyclerView.addOnScrollListener(onScrollListener);
        scrollArea.setOnTouchListener(this);

//        downloadMissedVideos(mLoadedItems, INT_UPDATE_COUNT);
        downloadMissedVideos();
        return view;
    }

    private void downloadMissedVideos() {
        if (mIsLoading) {
            return;
        }
        setIsLoading(true);
        String day = FormatTime.zdfCalendarToMissedRequest(mDay);
        Observable<ZdfPage> zdfPageObservable = mZdfInteractor.loadMissed(day);
        mSubscription.add(zdfPageObservable
                .map(ZdfPage::getResultList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError));
    }

    private void onSuccess(ArrayList<ZdfEpisode> zdfEpisodes) {
        mVideoAdapter.updateValues(zdfEpisodes);
        setIsLoading(false);
        switchToNextDay();
    }

    private void onError(Throwable throwable) {
        System.out.println("Error!");
    }

//    private void downloadMissedVideos2() {
//        if (mIsLoading || mDay.before(mLastDay)) return;
//        setIsLoading(true);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final ArrayList<Episode> episodes = zdf.getMostRecentEpisodes(mLoadedItems, INT_UPDATE_COUNT, mDay, mDay);
//                if (getActivity() == null) return;
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if ((episodes == null || episodes.isEmpty()) && mDay.after(mLastDay)) {
//                            switchToNextDay();
//                        } else if (episodes != null) {
//                            mVideoAdapter.updateValues(episodes);
//                            mLoadedItems += episodes.size();
//                        }
//                        setIsLoading(false);
//                    }
//                });
//            }
//        }).start();
//    }


    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            if (lastVisibleItem >= mVideoAdapter.getItemCount() - INT_UPDATE_THRESHOLD) {
                downloadMissedVideos();
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float normalizedPosition = 1.7f * (event.getY() - mWindowHeight / 2) + mWindowHeight / 2;
        int fastScrollPosition = (int) (mVideoAdapter.getItemCount() * normalizedPosition / mWindowHeight);

        if (fastScrollPosition < 0)
            fastScrollPosition = 0;
        else if (fastScrollPosition >= mVideoAdapter.getItemCount())
            fastScrollPosition = mVideoAdapter.getItemCount() - 2;

        mLayoutManager.scrollToPosition(fastScrollPosition);

        // Indicator
        mScrollLayoutParams.topMargin = (int) event.getY() - mScrollLayoutParams.height;
        mIndicator.setLayoutParams(mScrollLayoutParams);

        int firstVisible = mLayoutManager.findFirstCompletelyVisibleItemPosition();
//        Episode episode = mVideoAdapter.getItem(firstVisible);
//        if (episode != null)
//            mIndicator.setText(episode.getAirTimeDay());

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

    private void switchToNextDay() {
        mDay.add(Calendar.DAY_OF_MONTH, -1);
        mVideoAdapter.addHeader(mDay);
        mLoadedItems = 0;
    }

    @Override
    public void injectWith(ActivityComponent component) {
        component.inject(this);
    }
}
