package com.marcn.mediathek;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.stations.Station;
import com.marcn.mediathek.ui_fragments.VideoWidgetFragment;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;

public class SeriesActivity extends BaseActivity {

    public static final String INTENT_SENDUNG_JSON = "sendung-json";
//    public static final String INTENT_SENDUNG_ID = "sendung-id";

    private Series mSeries;

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
            setupHeaderView(mSeries);
        }

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
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
                        collapsingToolbarLayout.setTitle(mSeries.shortTitle);
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        getIntentThumbnail();
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
                    .config(Bitmap.Config.RGB_565)
                    .into(thumbnail);

    }

    private void getIntentThumbnail() {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(openFileInput("thumbnail"));
            ((ImageView) findViewById(R.id.imageThumbnail)).setImageBitmap(bitmap);
            Palette p = Palette.from(bitmap).generate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int themeColor = p.getDarkVibrantColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                //getWindow().setStatusBarColor(themeColor);
                if (findViewById(R.id.toolbar_layout) != null) {
                    findViewById(R.id.toolbar_layout).setBackgroundColor(themeColor);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    void navigationIdReceived(int id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_LIVE_DRAWER_ITEM, id);
        startActivity(intent);
    }

    private void loadWidgets() {
        if (mSeries == null) return;
        Station station = Station.createStation(mSeries.getStationTitle());
        if (station == null || station.getEpisodeWidgets() == null) return;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    void setExitTransition() {
        getWindow().setExitTransition(new Explode());
    }
}
