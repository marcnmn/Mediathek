package com.marcn.mediathek.pages.live;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;

import com.marcn.mediathek.R;
import com.marcn.mediathek.adapter.LiveStreamAdapter;
import com.marcn.mediathek.base_objects.StationOld;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.ui_fragments.LiveStreamsFragment;
import com.marcn.mediathek.ui_fragments.SendungenAbisZFragment;
import com.marcn.mediathek.ui_fragments.ZdfMissedVideoFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class LiveActivity extends BaseActivity implements Injector<ActivityComponent> {

    @Inject
    Context mApplicationContext;

    @Inject
    LiveStreamAdapter mAdapter;

    @Inject
    ZdfInteractor mZdfInteractor;

    @Inject
    ArdInteractor mArdInteractor;

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
        if (savedInstanceState != null) {
            mAdapter.addValues(savedInstanceState.getParcelableArrayList("asdf"));
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
        mAdapter.addValues(streams);
    }

    private void onError(Throwable throwable) {

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void navigationIdReceived(int id) {
        if (id == R.id.nav_live) {
            loadCleanFragment(new LiveStreamsFragment(), R.id.content_main, FRAGMENT_NAME_FIRST_PAGE, LiveStreamsFragment.FRAGMENT_TAG);
        } else if (id == R.id.nav_gallery) {
            loadCleanFragment(new ZdfMissedVideoFragment());
        } else if (id == R.id.nav_all_series) {
            loadCleanFragment(new SendungenAbisZFragment());
        }

        StationOld station = null;
        switch (id) {
            case R.id.nav_zdf:
                station = new StationOld(getString(R.string.zdf_name));
                break;
            case R.id.nav_ard:
                station = new StationOld(getString(R.string.ard_name));
                break;
            case R.id.nav_swr:
                station = new StationOld(getString(R.string.swr_name));
                break;
            case R.id.nav_zdf_neo:
                station = new StationOld(getString(R.string.zdf_neo_name));
                break;
            case R.id.nav_arte:
                station = new StationOld(getString(R.string.arte_name));
                break;
            case R.id.nav_zdf_info:
                station = new StationOld(getString(R.string.zdf_info_name));
                break;
        }
        if (station != null)
            startChannelActivity(station);
    }

    private void loadCleanFragment(Fragment fragment) {
        loadCleanFragment(fragment, R.id.content_main);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("asdf", mAdapter.getList());
//        outState.putSerializable("asdf", mAdapter.getList());
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
