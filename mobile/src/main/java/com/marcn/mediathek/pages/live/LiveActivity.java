package com.marcn.mediathek.pages.live;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Explode;

import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.LiveStreamAdapter;
import com.marcn.mediathek.adapter.base.SortableDragCallback;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.pages.BaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class LiveActivity extends BaseActivity implements Injector<ActivityComponent> {

    private static final String ARG_DATA = "arg-data";

    @Inject
    Context mApplicationContext;

    @Inject
    LiveStreamAdapter mAdapter;

    @Inject
    ZdfInteractor mZdfInteractor;

    @Inject
    ArdInteractor mArdInteractor;

    @Inject
    SortableDragCallback mTouchCallback;

    @BindView(R.id.live_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        InjectHelper.setupPage(this);
        ButterKnife.bind(this);

        hideWindowBackground();
        setUpPage();

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

    private void setUpPage() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void setExitTransition() {
        getWindow().setExitTransition(new Explode());
    }

    @Override
    public void injectWith(ActivityComponent component) {
        component.inject(this);
    }

}
