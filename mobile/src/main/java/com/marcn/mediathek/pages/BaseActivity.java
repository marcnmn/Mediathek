package com.marcn.mediathek.pages;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.MediathekApplication;
import com.marcn.mediathek.R;
import com.marcn.mediathek.StationUtils.ArdUtils;
import com.marcn.mediathek.StationUtils.ZdfUtils;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.base_objects.Series;
import com.marcn.mediathek.base_objects.StationOld;
import com.marcn.mediathek.pages.home.MainActivity;
import com.marcn.mediathek.pages.player_page.VideoActivity;
import com.marcn.mediathek.pages.series_page.SeriesActivity;
import com.marcn.mediathek.pages.station_page.StationActivity;
import com.marcn.mediathek.stations.Arte;
import com.marcn.mediathek.utils.NavigationManager;
import com.marcn.mediathek.utils.Playback;
import com.marcn.mediathek.utils.Storage;

import java.io.IOException;
import java.util.TreeMap;

import javax.inject.Inject;

import butterknife.BindView;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnVideoInteractionListener {

    public static String FRAGMENT_NAME_FIRST_PAGE = "first-fragment";

    @Inject
    NavigationManager mNavigationManager;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    protected MediathekApplication mApplication;

//    public abstract void navigationIdReceived(int id);

    //    @Override
    protected void navigationIdReceived(int id) {
        if (id == R.id.nav_live) {
            mNavigationManager.goToLiveStream();
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_LIVE_DRAWER_ITEM, id);
        startActivity(intent);
    }

    protected void setExitTransition() {
        // nop
    }

    protected void loadCleanFragment(Fragment fragment, int containerId) {
        loadCleanFragment(fragment, containerId, "0");
    }

    protected void loadCleanFragment(Fragment fragment, int containerId, String name) {
        loadCleanFragment(fragment, containerId, name, name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = getApplication(this);
    }

    protected void loadCleanFragment(Fragment fragment, int containerId, String name, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(containerId, fragment, tag);
        transaction.addToBackStack(name);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (startPointFragmentOnTop()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                finishAfterTransition();
            else
                finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        if (id == R.id.nav_live) {
            mNavigationManager.goToLiveStream();
            return true;
        }

        navigationIdReceived(id);
        return true;
    }

    public void setActionBarResource(int resId) {
        setActionBarTitle(getString(resId));
    }

    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    @Override
    public void onLiveStreamClicked(final LiveStreamM3U8 video, final View view, final int videoAction) {
        final Activity activity = this;
        setExitTransition();
        runOnUiThread(() -> Playback.playByUrl(activity, video.getStreamUrl(), view, videoAction, video.toString()));
    }

    @Override
    public void playVideoWithInternalPlayer(final String url, final ActivityOptions activityOptions) {
        final Activity activity = this;
        setExitTransition();
        runOnUiThread(() -> Playback.startInternalPlayer(activity, url, activityOptions));
    }

    @Override
    public void playVideoExternal(final String url, final String title, final int videoAction) {
        final Activity activity = this;
        runOnUiThread(() -> Playback.playByUrl(activity, url, null, videoAction, title));
    }

    @Override
    public void onVideoClicked(final Episode episode, final View view, final int videoAction) {
        final Activity activity = this;
        new Thread(() -> {
            try {
                final TreeMap<Integer, String> s;
                int group = StationOld.getGroupFromName(episode.getStationTitle());
                switch (group) {
                    case StationOld.ARTE_GROUP:
                        s = (new Arte()).getVodUrls(episode.getAssetId());
                        break;
                    case StationOld.ZDF_GROUP:
                        s = ZdfUtils.getVideoUrl(view.getContext(), episode.getAssetId());
                        break;
                    default:
                        s = ArdUtils.getVideoUrl(episode.getAssetId());
                        break;
                }

                if (s != null && !s.isEmpty())
                    runOnUiThread(() -> Playback.playByUrl(activity, s.get(s.firstKey()), view, videoAction, episode.getTitle()));
            } catch (IOException ignored) {
            }
        }).start();
    }

    @Override
    public void onSendungClicked(Series series, View thumbnail, View logo) {
        if (series == null) return;

        Intent intent = new Intent(this, SeriesActivity.class);
        Gson gson = new Gson();
        String json = gson.toJson(series);
        intent.putExtra(SeriesActivity.INTENT_SENDUNG_JSON, json);

        ImageView imageView = (ImageView) thumbnail;
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Storage.saveBitmapOnDisk(this, bmp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setExitTransition();

            thumbnail.setTransitionName("thumbnail");
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, thumbnail, "thumbnail");
            startActivity(intent, options.toBundle());
        } else
            startActivity(intent);
    }

    @Override
    public void onChannelClicked(StationOld station, View view) {
        if (station == null) return;
        Toast.makeText(this, station.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreClicked(String assetId, int type) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(VideoActivity.INTENT_VIDEO_ASSET_ID, assetId);
        intent.putExtra(VideoActivity.INTENT_VIDEO_TYPE, type);
        startActivity(intent);
    }

    @Override
    public void onMoreClicked2(String channelTitle, String assetId, String widgetTitle) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(VideoActivity.INTENT_VIDEO_CHANNEL_TITLE, channelTitle);
        intent.putExtra(VideoActivity.INTENT_VIDEO_ASSET_ID, assetId);
        intent.putExtra(VideoActivity.INTENT_VIDEO_TITLE, widgetTitle);
        startActivity(intent);
    }

    public void startChannelActivity(StationOld station) {
        if (station == null) return;
        Intent intent = new Intent(this, StationActivity.class);
        intent.putExtra(StationActivity.INTENT_STATION_TITLE, station.title);
        intent.putExtra(StationActivity.INTENT_STATION_ID, station.getChannelId());
        startActivity(intent);
    }

    private boolean startPointFragmentOnTop() {
        int fragmentCount = getSupportFragmentManager().getBackStackEntryCount() - 1;
        if (fragmentCount < 0) return false;
        FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(fragmentCount);
        if (backEntry == null) return false;
        String str = backEntry.getName();
        return str != null && str.equals(FRAGMENT_NAME_FIRST_PAGE);
    }

    private static MediathekApplication getApplication(Context context) {
        return (MediathekApplication) context.getApplicationContext();
    }

    protected void hideWindowBackground() {
        if (isDestroyed()) {
            return;
        }

        // ensure that all UI changes happened before we clear the background, to avoid ugly flickering
        getSupportFragmentManager().executePendingTransactions();

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
