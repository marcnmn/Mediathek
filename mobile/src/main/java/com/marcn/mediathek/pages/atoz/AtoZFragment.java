package com.marcn.mediathek.pages.atoz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marcn.mediathek.R;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.model.pages.ZdfPage;
import com.marcn.mediathek.model.zdf.ZdfSeries;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.ActivityComponent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AtoZFragment extends Fragment implements Injector<ActivityComponent> {
    private static final int INT_UPDATE_THRESHOLD = 15;
    private static final String ARG_LETTER = "arg-letter";

    @Inject
    ZdfInteractor mZdfInteractor;

    @Inject
    ArdInteractor mArdInteractor;

    @Inject
    SeriesAdapter mAdapter;

    @BindView(R.id.list)
    RecyclerView mRecyclerView;

    private boolean mIsLoading;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private GridLayoutManager mLayoutManager;

    public static AtoZFragment newInstance(String letter) {
        Bundle args = new Bundle();
        args.putString(ARG_LETTER, letter);
        AtoZFragment fragment = new AtoZFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectHelper.setupPage(this);
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

        setUpRecycler();
        fetchShows();
        return view;
    }

    private void setUpRecycler() {
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void fetchShows() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        String letter = getArguments().getString(ARG_LETTER);
        Observable<ZdfPage> zdfPageObservable = mZdfInteractor.loadAtoZ(letter, letter);
        mSubscription.add(zdfPageObservable
                .map(ZdfPage::getSeries)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError));

        mSubscription.add(mArdInteractor.loadAllAtoZ()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::ardSuccess, this::onError));
    }

    private void ardSuccess(List<Stream> streams) {
        System.out.println(streams);
    }

    private void onSuccess(ArrayList<ZdfSeries> series) {
        mIsLoading = false;
        mAdapter.updateValues(series);
//        mVideoAdapter.updateValues(zdfEpisodes);
//        mCurrentDay.add(Calendar.DAY_OF_MONTH, -1);
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
//            if (mLayoutManager.findLastCompletelyVisibleItemPosition()
//                    >= mVideoAdapter.getItemCount() - INT_UPDATE_THRESHOLD) {
//                downloadMissedVideos();
//            }
        }
    };
}
