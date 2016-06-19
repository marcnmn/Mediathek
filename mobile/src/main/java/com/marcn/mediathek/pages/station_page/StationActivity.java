package com.marcn.mediathek.pages.station_page;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.marcn.mediathek.R;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.di.Injector;
import com.marcn.mediathek.model.base.Episode;
import com.marcn.mediathek.model.pages.ZdfPage;
import com.marcn.mediathek.model.zdf.ZdfAsset;
import com.marcn.mediathek.model.zdf.ZdfEpisode;
import com.marcn.mediathek.model.zdf.ZdfLive;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.ActivityComponent;
import com.marcn.mediathek.pages.CoordinatorActivity;
import com.marcn.mediathek.pages.home.MainActivity;
import com.marcn.mediathek.utils.Anims;
import com.marcn.mediathek.views.SideScroller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class StationActivity extends CoordinatorActivity
        implements Injector<ActivityComponent> {

    public static final String INTENT_STATION_TITLE = "station-title";
    public static final String INTENT_STATION_TYPE = "station-type";
    public static final String INTENT_STATION_ID = "station-id";

    @Inject
    ArdInteractor mArdInteractor;

    @Inject
    ZdfInteractor mZdfInteractor;

    @BindView(R.id.widgetContainer)
    LinearLayout mWidgetContainer;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.imageThumbnail)
    ImageView mThumbnail;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    private String mId;
    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Override
    protected int getContentResource() {
        return R.layout.content_player;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        ButterKnife.bind(this);
        InjectHelper.setupPage(this);

        Anims.fadeOut(mFab, 0);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String title = intent.getStringExtra(INTENT_STATION_TITLE);
            String type = intent.getStringExtra(INTENT_STATION_TYPE);
            mId = intent.getStringExtra(INTENT_STATION_ID);
            mToolbar.setTitle(title);
            loadZdfData();
        }
    }

    @Override
    protected void setUpActivity(Bundle savedInstanceState) {

    }

    @Override
    public void injectWith(ActivityComponent component) {
        component.inject(this);
    }

    private void loadZdfData() {
        Observable<ZdfPage> zdfPageObservable = mZdfInteractor.loadStations(Long.parseLong(mId), 0);
        mSubscription.add(zdfPageObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError));
    }

    private void onSuccess(ZdfPage zdfPage) {
        List<ZdfAsset> live = zdfPage.getTeaserList(ZdfPage.CATEGORY_TYPE_LIVE);
        if (live.get(0) instanceof ZdfLive) {
            Glide.with(this)
                    .load(((ZdfLive) live.get(0)).getThumbUrl())
                    .centerCrop()
                    .into(mThumbnail);
        }

        List<ZdfAsset> meistgesehen = zdfPage.getTeaserList(ZdfPage.CATEGORY_TYPE_MOST_WATCHED);
        addWidget(meistgesehen);
        List<ZdfAsset> neu = zdfPage.getTeaserList(ZdfPage.CATEGORY_TYPE_NEW);
        addWidget(neu);
        List<ZdfAsset> tipps = zdfPage.getTeaserList(ZdfPage.CATEGORY_TYPE_TIPPS);
        addWidget(tipps);
    }

    private void addWidget(List<ZdfAsset> assets) {
        List<Episode> episodes = new ArrayList<>();
        for (ZdfAsset asset : assets) {
            if (asset instanceof ZdfEpisode) {
                episodes.add((Episode) asset);
            }
        }
        SideScroller sideScroller = new SideScroller(this);
        sideScroller.setTitle("Das ist ein Test");
        sideScroller.setItems(episodes);
        mWidgetContainer.addView(sideScroller);
    }

    private void onError(Throwable throwable) {
        String asdf = "asdf";
    }

//    private void loadWidgets() {
//        if (mStation == null || mStation.getTopLevelCategories() == null) return;
//
//        if (mStation.getTopLevelCategories() != null) {
//            for (String key : mStation.getTopLevelCategories().keySet()) {
//                loadWidget(key, INT_WIDGET_VIDEO);
//            }
//        }
//
//        if (mStation.getSeriesWidgets() != null) {
//            for (String key : mStation.getSeriesWidgets().keySet()) {
//                loadWidget(key, INT_WIDGET_SERIES);
//            }
//        }
//    }

//    private void loadWidget(String widgetKey, int type) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        Fragment frag;
//        if (type == INT_WIDGET_VIDEO)
//            frag = VideoWidgetFragment.newInstance(mStation.getTitle(), null, widgetKey);
//        else
//            frag = SeriesWidgetFragment.newInstance(mStation.getTitle(), null, widgetKey);
//        transaction.add(R.id.widgetContainer, frag, widgetKey);
//        transaction.commit();
//    }

//    private void setupHeaderView() {
//        if (findViewById(R.id.imageChannel) != null)
//            ((TextView) findViewById(R.id.imageChannel)).setText(mStation.toString());
//        if (findViewById(R.id.textTitle) != null)
//            ((TextView) findViewById(R.id.textTitle)).setText(mCurrentEpisode.getTitle());
//
//        if (findViewById(R.id.textDetail) != null)
//            ((TextView) findViewById(R.id.textDetail)).setText(mCurrentEpisode.getDescription());
//
//        if (mFab != null) {
//            Anims.fadeIn(mFab);
//            mFab.setOnClickListener(v -> {
//                ActivityOptions activityOptions = prepareInternalPlayerTransition();
//                playVideoWithInternalPlayer(mStation.getLiveStream().getStreamUrl(), activityOptions);
//            });
//            mFab.setOnLongClickListener(v -> {
//                playVideoExternal(mStation.getLiveStream().getStreamUrl(), mStation.toString(), Episode.ACTION_SHARE_VIDEO_DIALOG);
//                return true;
//            });
//        }
//    }


//    @SuppressWarnings("unchecked")
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private ActivityOptions prepareInternalPlayerTransition() {
//        mThumbnail.setTransitionName("thumb");
//        Transitions.saveBitmapFromImageView(this, mThumbnail, Transitions.PLAYER_THUMBNAIL);
//        return ActivityOptions.makeSceneTransitionAnimation(this,
//                new Pair<>(mThumbnail, ""),
//                new Pair<>(mFab, ""));
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void navigationIdReceived(int id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_LIVE_DRAWER_ITEM, id);
        startActivity(intent);
    }


//    @Override
//    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//        if (mToolbarScrollRange == -1) {
//            mToolbarScrollRange = appBarLayout.getTotalScrollRange();
//        }
//        if (mToolbarScrollRange + verticalOffset == 0) {
//            if (mVideo != null)
//                mCollapsingToolbarLayout.setTitle(mVideo.title);
//            mToolbarIsShown = true;
//        } else if (mToolbarIsShown) {
//            mCollapsingToolbarLayout.setTitle("");
//            mToolbarIsShown = false;
//        }
//    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void setExitTransition() {
        getWindow().setExitTransition(new Explode());
    }


//    private void loadWidget(int Type, int resId) {
////        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
////        transaction.replace(resId, VideoWidgetFragment.newInstance(mSendung, Type));
////        transaction.commit();
//    }
//    private void prepareBitmap() {
//        Bitmap bmp = ((BitmapDrawable) mThumbnail.getDrawable()).getBitmap();
//        Storage.saveBitmapOnDisk(this, bmp);
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private ActivityOptions createOptions() {
//        mThumbnail.setTransitionName("thumb");
//        Pair<View, String> pair1 = Pair.create((View) mThumbnail, "");
//        Pair<View, String> pair2 = Pair.create((View) mFab, "");
//        Pair[] pairs = new Pair[2];
//        pairs[0] = pair1;
//        pairs[1] = pair2;
//
//        return ActivityOptions.makeSceneTransitionAnimation(this, pairs);
//    }
}

//    private void setupHeaderView(final Video liveStream) {
//        if (liveStream == null) return;
//        if (findViewById(R.id.imageChannel) != null)
//            ((TextView) findViewById(R.id.imageChannel)).setText(mStation.title);
////            ((ImageView) findViewById(R.id.imageChannel)).setImageResource(sendung.station.getLogoResId());
//        if (findViewById(R.id.textTitle) != null)
//            ((TextView) findViewById(R.id.textTitle)).setText(liveStream.getTitle());
//
//        if (findViewById(R.id.textDetail) != null)
//            ((TextView) findViewById(R.id.textDetail)).setText(liveStream.detail);
//
//        if (mFab != null) {
//            Anims.fadeIn(mFab);
//            mFab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ActivityOptions activityOptions = prepareInternalPlayerTransition();
//                    playVideoWithInternalPlayer(liveStream.getLiveM3U8(), activityOptions);
//                }
//            });
//            mFab.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    playVideoExternal(liveStream.getLiveM3U8(), mStation.title, Asset.ACTION_SHARE_VIDEO_DIALOG);
//                    return true;
//                }
//            });
//        }
//
//        if (mThumbnail != null && liveStream.thumb_url != null)
//            Picasso.with(mContext)
//                    .load(liveStream.thumb_url)
//                    .config(Bitmap.Config.RGB_565)
//                    .into(mThumbnail);
//    }
//    private void themeActivity(Bitmap bitmap) {
//        Palette p = Palette.from(bitmap).generate();
//        Palette.Swatch swatch = p.getDarkVibrantSwatch() != null ?
//                p.getDarkVibrantSwatch() : p.getDarkMutedSwatch();
//        if (swatch != null) {
//            float[] hsl = swatch.getHsl();
//            hsl[2] *= 0.98f;
//
//            mCollapsingToolbarLayout.setStatusBarScrimColor(Color.HSVToColor(hsl));
//            mCollapsingToolbarLayout.setContentScrimColor(swatch.getRgb());
//            mCollapsingToolbarLayout.setBackgroundColor(swatch.getRgb());
//        }
//    }
