package com.marcn.mediathek;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.marcn.mediathek.base_objects.Station;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.ui_fragments.VideoWidgetFragment;
import com.marcn.mediathek.utils.Anims;
import com.marcn.mediathek.utils.Storage;
import com.marcn.mediathek.utils.Transitions;
import com.marcn.mediathek.utils.XmlParser;
import com.squareup.picasso.Picasso;

public class ChannelActivity extends BaseActivity
        implements AppBarLayout.OnOffsetChangedListener {

    public static final String INTENT_SENDER_JSON = "station-json";

    private Station mStation;
    private LiveStream mLiveStream;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private boolean mToolbarIsShown = false;
    private int mToolbarScrollRange = -1;
    private Context mContext;
    private ImageView mThumbnail;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = toolbar.getContext();
        mThumbnail = (ImageView) findViewById(R.id.imageThumbnail);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        Anims.fadeOut(mFab, 0);

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
            mStation = gson.fromJson(json, Station.class);
        }

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        boolean test = mThumbnail.hasTransientState();

//        getIntentThumbnail();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadWidgets();
                downloadLiveStreamData();
            }
        }, 500);
    }

    private void downloadLiveStreamData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLiveStream = XmlParser.getLivestreamFromChannel(mContext, mStation);
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

    private void setupHeaderView(final LiveStream liveStream) {
        if (liveStream == null) return;
        if (findViewById(R.id.imageChannel) != null)
            ((TextView) findViewById(R.id.imageChannel)).setText(mStation.title);
//            ((ImageView) findViewById(R.id.imageChannel)).setImageResource(sendung.station.getLogoResId());
        if (findViewById(R.id.textTitle) != null)
            ((TextView) findViewById(R.id.textTitle)).setText(liveStream.getTitle());

        if (findViewById(R.id.textDetail) != null)
            ((TextView) findViewById(R.id.textDetail)).setText(liveStream.detail);

        if (mFab != null) {
            Anims.fadeIn(mFab);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityOptions activityOptions = prepareInternalPlayerTransition();
                    playVideoWithInternalPlayer(liveStream.getLiveM3U8(), activityOptions);
                }
            });
            mFab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    playVideoExternal(liveStream.getLiveM3U8(), mStation.title, Episode.ACTION_SHARE_VIDEO_DIALOG);
                    return true;
                }
            });
        }

        if (mThumbnail != null && liveStream.thumb_url != null)
            Picasso.with(mContext)
                    .load(liveStream.thumb_url)
                    .config(Bitmap.Config.RGB_565)
                    .into(mThumbnail);
    }

    @SuppressWarnings("unchecked")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ActivityOptions prepareInternalPlayerTransition() {
        mThumbnail.setTransitionName("thumb");
        Transitions.saveBitmapFromImageView(this, mThumbnail, Transitions.PLAYER_THUMBNAIL);
        return ActivityOptions.makeSceneTransitionAnimation(this,
                new Pair<View, String>(mThumbnail, ""),
                new Pair<View, String>(mFab, ""));
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    void setExitTransition() {
        getWindow().setExitTransition(new Explode());
    }

    private void prepareBitmap() {
        Bitmap bmp = ((BitmapDrawable) mThumbnail.getDrawable()).getBitmap();
        Storage.saveBitmapOnDisk(this, bmp);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ActivityOptions createOptions() {
        mThumbnail.setTransitionName("thumb");
        Pair<View, String> pair1 = Pair.create((View) mThumbnail, "");
        Pair<View, String> pair2 = Pair.create((View) mFab, "");
        Pair[] pairs = new Pair[2];
        pairs[0] = pair1;
        pairs[1] = pair2;

        return ActivityOptions.makeSceneTransitionAnimation(this, pairs);
    }
}
