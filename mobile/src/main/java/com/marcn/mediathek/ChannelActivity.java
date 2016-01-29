package com.marcn.mediathek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.marcn.mediathek.base_objects.Channel;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.ui_fragments.VideoWidgetFragment;
import com.marcn.mediathek.utils.XmlParser;
import com.squareup.picasso.Picasso;

public class ChannelActivity extends BaseActivity
        implements AppBarLayout.OnOffsetChangedListener {

    public static final String INTENT_SENDER_JSON = "channel-json";

    private Channel mChannel;
    private LiveStream mLiveStream;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private boolean mToolbarIsShown = false;
    private int mToolbarScrollRange = -1;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = toolbar.getContext();

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
            String json = intent.getStringExtra(INTENT_SENDER_JSON);
            Gson gson = new Gson();
            mChannel = gson.fromJson(json, Channel.class);
        }

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

//        getIntentThumbnail();
        loadWidgets();
        downloadLiveStreamData();
    }

    private void downloadLiveStreamData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLiveStream = XmlParser.getLivestreamFromChannel(mContext, mChannel);
                if (mLiveStream == null) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupHeaderView(mLiveStream);
                    }
                });
            }
        }).start();
    }

    private void loadWidgets() {
        loadWidget(VideoWidgetFragment.WIDGET_TYPE_SENDUNG_LAST, R.id.widgetLast, getString(R.string.zdf_id_startseite));
        loadWidget(VideoWidgetFragment.WIDGET_TYPE_SENDUNG_MOST_POPULAR, R.id.widgetMost, getString(R.string.zdf_id_global));
        loadWidget(VideoWidgetFragment.WIDGET_TYPE_TIPPS, R.id.widgetFurther, "");
    }

    private void loadWidget(int Type, int resId, String assetId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(resId, VideoWidgetFragment.newInstance(assetId, Type));
        transaction.commit();
    }

    private void setupHeaderView(LiveStream liveStream) {
        if (liveStream == null) return;
//        if (findViewById(R.id.imageChannel) != null)
//            ((ImageView) findViewById(R.id.imageChannel)).setImageResource(sendung.channel.getLogoResId());
        if (findViewById(R.id.textTitle) != null)
            ((TextView) findViewById(R.id.textTitle)).setText(liveStream.getTitle());

//        if (findViewById(R.id.textDetail) != null)
//            ((TextView) findViewById(R.id.textDetail)).setText(channel.detail);

        ImageView thumbnail = (ImageView) findViewById(R.id.imageThumbnail);
        if (thumbnail != null && liveStream.thumb_url != null)
            Picasso.with(this)
                    .load(liveStream.thumb_url)
                    .config(Bitmap.Config.RGB_565)
                    .into(thumbnail);
    }

//    private void getIntentThumbnail() {
//        try {
//            Bitmap bitmap = BitmapFactory.decodeStream(openFileInput("thumbnail"));
//            ((ImageView) findViewById(R.id.imageThumbnail)).setImageBitmap(bitmap);
//            Palette p = Palette.from(bitmap).generate();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                int themeColor = p.getDarkVibrantColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//
//                //getWindow().setStatusBarColor(themeColor);
//                if (findViewById(R.id.toolbar_layout) != null) {
//                    findViewById(R.id.toolbar_layout).setBackgroundColor(themeColor);
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    void navigationIdReceived(int id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_LIVE_DRAWER_ITEM, id);
        startActivity(intent);
    }

//    private void loadWidget(int Type, int resId) {
////        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
////        transaction.replace(resId, VideoWidgetFragment.newInstance(mSendung, Type));
////        transaction.commit();
//    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mToolbarScrollRange == -1) {
            mToolbarScrollRange = appBarLayout.getTotalScrollRange();
        }
        if (mToolbarScrollRange + verticalOffset == 0) {
            if (mLiveStream != null)
                mCollapsingToolbarLayout.setTitle(mLiveStream.title);
            mToolbarIsShown = true;
        } else if (mToolbarIsShown) {
            mCollapsingToolbarLayout.setTitle("");
            mToolbarIsShown = false;
        }
    }
}
