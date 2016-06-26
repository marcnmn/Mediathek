package com.marcn.mediathek.pages.missed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.VideoAdapter2;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.model.pages.ZdfPage;
import com.marcn.mediathek.model.zdf.ZdfEpisode;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.utils.FormatTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ZdfMissedVideoFragment extends Fragment implements Injector<ActivityComponent> {
    private static final int INT_UPDATE_THRESHOLD = 15;

    @Inject
    ZdfInteractor mZdfInteractor;

    @Inject
    ArdInteractor mArdInteractor;

    @Inject
    VideoAdapter2 mVideoAdapter;

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    private Calendar mCurrentDay;
    private boolean mIsLoading;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectHelper.setupPage(this);
        mCurrentDay = new GregorianCalendar();
        mCurrentDay.add(Calendar.DAY_OF_YEAR, 1);
    }

    @Override
    public void injectWith(ActivityComponent component) {
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sendungen_abisz, container, false);
        ButterKnife.bind(this, view);

        if ((getActivity()) != null) {
            ((BaseActivity) getActivity()).setActionBarResource(R.string.action_title_sendung_verpasst);
        }

        setUpRecycler();
        downloadMissedVideos();
        return view;
    }

    private void setUpRecycler() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mRecyclerView.setAdapter(mVideoAdapter);
    }

    private void downloadMissedVideos() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        String day = FormatTime.zdfCalendarToMissedRequest(mCurrentDay);

        Observable<ZdfPage> zdfPageObservable = mZdfInteractor.loadMissed(day);
        mSubscription.add(zdfPageObservable
                .map(ZdfPage::getResultList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError));
    }

    private void onSuccess(ArrayList<ZdfEpisode> zdfEpisodes) {
        mIsLoading = false;
        mVideoAdapter.updateValues(zdfEpisodes);
        mCurrentDay.add(Calendar.DAY_OF_MONTH, -1);
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (mLinearLayoutManager.findLastCompletelyVisibleItemPosition()
                    >= mVideoAdapter.getItemCount() - INT_UPDATE_THRESHOLD) {
                downloadMissedVideos();
            }
        }
    };
}
