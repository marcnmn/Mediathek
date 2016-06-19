package com.marcn.mediathek.pages.live;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.LiveStreamAdapter;
import com.marcn.mediathek.adapter.base.SortableDragCallback;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.pages.CoordinatorActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class LiveActivity extends CoordinatorActivity implements Injector<ActivityComponent> {

    private static final String ARG_DATA = "arg-data";

    @Inject
    Context mApplicationContext;

    @Inject
    ZdfInteractor mZdfInteractor;

    @Inject
    ArdInteractor mArdInteractor;

    @Inject
    SortableDragCallback mTouchCallback;

    @Inject
    LiveStreamAdapter mAdapter;

    @BindView(R.id.content_recycler)
    RecyclerView mRecyclerView;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Override
    protected int getContentResource() {
        return R.layout.content_recycler;
    }

    @Override
    protected void setUpActivity(Bundle savedInstanceState) {
        InjectHelper.setupPage(this);
        ButterKnife.bind(this);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.live_streams));
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
        mTouchCallback.setAdapter(mAdapter);
        new ItemTouchHelper(mTouchCallback).attachToRecyclerView(mRecyclerView);

        if (savedInstanceState != null) {
            mAdapter.addItems(savedInstanceState.getParcelableArrayList(ARG_DATA));
        } else {
            loadLiveStreams();
        }
    }

    @Override
    public void injectWith(ActivityComponent component) {
        component.inject(this);
    }

    private void loadLiveStreams() {
        mSubscription.add(
                Observable.concat(mZdfInteractor.loadZdfStreams(), mArdInteractor.loadLivestreams())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onStreamSuccess, this::onError)
        );
    }

    private void onStreamSuccess(List<Stream> streams) {
        mAdapter.addItems(streams);
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.saveItemOrder();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ARG_DATA, mAdapter.getList());
    }
}
