package com.marcn.mediathek.pages.series_page;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.pages.home.MainActivity;
import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.stations.ArdGroup;
import com.marcn.mediathek.stations.Station;
import com.marcn.mediathek.ui_fragments.VideoFragment;
import com.marcn.mediathek.ui_fragments.VideoWidgetFragment;
import com.marcn.mediathek.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;

public class SeriesActivity extends BaseActivity {

    public static final String INTENT_SENDUNG_JSON = "sendung-json";
//    public static final String INTENT_SENDUNG_ID = "sendung-id";

    private Series mSeries;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private VideoFragment mVideoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendung);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String json = intent.getStringExtra(INTENT_SENDUNG_JSON);
            Gson gson = new Gson();
            mSeries = gson.fromJson(json, Series.class);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().addListener(transitionListener);
        } else if (mSeries != null) {
            setupHeaderView(mSeries);
        }

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (mSeries != null)
                        mCollapsingToolbarLayout.setTitle(mSeries.title);
                    if (mVideoFragment != null)
                        mVideoFragment.setNestedScrollingEnabled(true);
                    isShow = true;
                } else if(isShow) {
                    mCollapsingToolbarLayout.setTitle("");
                    if (mVideoFragment != null)
                        mVideoFragment.setNestedScrollingEnabled(false);
                    isShow = false;
                }
            }
        });

        getIntentThumbnail();
//        getIntentThumbnail();
        loadWidgets();
    }

    private void setupHeaderView(Series series) {
        if (series == null) return;
//        if (findViewById(R.id.imageChannel) != null)
//            ((ImageView) findViewById(R.id.imageChannel)).setImageResource(series.station.getLogoResId());
        if (findViewById(R.id.textTitle) != null)
            ((TextView) findViewById(R.id.textTitle)).setText(series.title);

        if (findViewById(R.id.textDetail) != null)
            ((TextView) findViewById(R.id.textDetail)).setText(series.detail);

        if (findViewById(R.id.imageChannel) != null)
            ((TextView) findViewById(R.id.imageChannel)).setText(series.station.title);

        ImageView thumbnail = (ImageView) findViewById(R.id.imageThumbnail);
        if (thumbnail != null && series.thumb_url_high != null)
            Picasso.with(this)
                    .load(series.thumb_url_high)
                    .placeholder(thumbnail.getDrawable())
                    .config(Bitmap.Config.RGB_565)
                    .resize(Constants.SIZE_THUMB_BIG_X, Constants.SIZE_THUMB_BIG_Y)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(thumbnail);

    }

    private void getIntentThumbnail() {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(openFileInput("thumbnail"));
            ((ImageView) findViewById(R.id.imageThumbnail)).setImageBitmap(bitmap);
            themeActivity(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void themeActivity(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        Palette.Swatch swatch = p.getDarkVibrantSwatch() != null ?
                p.getDarkVibrantSwatch() : p.getDarkMutedSwatch();
        if (swatch != null) {
            float[] hsl = swatch.getHsl();
            hsl[2] *= 0.98f;

            mCollapsingToolbarLayout.setStatusBarScrimColor(Color.HSVToColor(hsl));
            mCollapsingToolbarLayout.setContentScrimColor(swatch.getRgb());
            mCollapsingToolbarLayout.setBackgroundColor(swatch.getRgb());
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public void navigationIdReceived(int id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_LIVE_DRAWER_ITEM, id);
        startActivity(intent);
    }

    private void loadWidgets() {
        if (mSeries == null) return;
        Station station = Station.createStation(mSeries.getStationTitle());
        if (station == null || station.getEpisodeWidgets() == null) return;

        if (station instanceof ArdGroup) {
            loadSeriesWidget();
            return;
        }

        for (String key : station.getEpisodeWidgets().keySet()) {
            loadWidget(key);
        }
    }

    private void loadWidget(String widgetKey) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        VideoWidgetFragment f = VideoWidgetFragment.newInstance(mSeries.getStationTitle(),
                mSeries.assetId + "", widgetKey);
        transaction.add(R.id.widgetContainer, f, widgetKey);
        transaction.commit();
    }

    private void loadSeriesWidget() {
        if (findViewById(R.id.list_placeholder) == null) return;

        findViewById(R.id.list_placeholder).setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mVideoFragment = VideoFragment.newInstance(mSeries.getStationTitle(), mSeries.getAssetId(), null);
        transaction.replace(R.id.list_placeholder, mVideoFragment, mSeries.getAssetId());
        transaction.commit();
        mVideoFragment.setNestedScrollingEnabled(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setExitTransition() {
        getWindow().setExitTransition(new Explode());
    }

    private Transition.TransitionListener transitionListener = new Transition.TransitionListener() {
        @Override
        public void onTransitionStart(Transition transition) {}

        @Override
        public void onTransitionEnd(Transition transition) {
            if (mSeries != null)
                setupHeaderView(mSeries);
        }

        @Override
        public void onTransitionCancel(Transition transition) {}

        @Override
        public void onTransitionPause(Transition transition) {}

        @Override
        public void onTransitionResume(Transition transition) {}
    };
}
