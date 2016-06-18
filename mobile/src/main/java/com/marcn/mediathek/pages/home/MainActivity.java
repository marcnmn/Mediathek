package com.marcn.mediathek.pages.home;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.widget.FrameLayout;

import com.marcn.mediathek.R;
import com.marcn.mediathek.ToastManager;
import com.marcn.mediathek.base_objects.StationOld;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.model.ard.ArdVideo;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.model.pages.ZdfPage;
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

public class MainActivity extends BaseActivity implements Injector<ActivityComponent> {

    @Inject
    Context mApplicationContext;

    @Inject
    ToastManager mToastManager;

    @Inject
    ZdfInteractor mZdfInteractor;

    @Inject
    ArdInteractor mArdInteractor;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.content_main)
    FrameLayout mContent;


    public static final String INTENT_LIVE_DRAWER_ITEM = "player-drawer-item";
    private LiveStreamsFragment liveStreamsFragment;
    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectHelper.setupPage(this);
        ButterKnife.bind(this);

        hideWindowBackground();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);

        FragmentManager fm = getSupportFragmentManager();
        liveStreamsFragment = (LiveStreamsFragment) fm.findFragmentByTag(LiveStreamsFragment.FRAGMENT_TAG);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null && intent.getIntExtra(INTENT_LIVE_DRAWER_ITEM, -1) >= 0) {
            int navId = intent.getIntExtra(INTENT_LIVE_DRAWER_ITEM, -1);
            navigationIdReceived(navId);
        } else {
//            navigationIdReceived(R.id.nav_zdf);
            if (liveStreamsFragment == null)
                liveStreamsFragment = LiveStreamsFragment.newInstance(1);
            loadCleanFragment(liveStreamsFragment, R.id.content_main, FRAGMENT_NAME_FIRST_PAGE, LiveStreamsFragment.FRAGMENT_TAG);
        }

//        loadZdfHomePage();
//        loadArdHomePage();
    }

    private void loadArdHomePage() {
        Observable<List<Stream>> ardPageObservable = mArdInteractor.loadLivestreams();
        mSubscription.add(ardPageObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onArdSuccess, this::onError));
    }

    private void onArdSuccess(List<Stream> liveStreams) {
        System.out.println("Success!");
    }

    private void onArdSuccess(ArdVideo ardVideo) {
        System.out.println("Success!");
    }

    private void loadZdfHomePage() {
        Observable<ZdfPage> zdfPageObservable = mZdfInteractor.loadMissed("2016-05-27");
        mSubscription.add(zdfPageObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onZdfSuccess, this::onError));
    }

    private void onZdfSuccess(ZdfPage zdfPage) {
        System.out.println("Success!");
    }

    private void onError(Throwable throwable) {
        System.out.println("Error!");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void navigationIdReceived(int id) {
        if (id == R.id.nav_gallery) {
            loadCleanFragment(new ZdfMissedVideoFragment());
//        } else if (id == R.id.nav_zdf_mediathek) {
//            loadCleanFragment(new SendungenAbisZFragment());
//        } else if (id == R.id.nav_arte_mediathek) {
//        } else if (id == R.id.nav_ard_mediathek) {
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
