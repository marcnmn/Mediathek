package com.marcn.mediathek;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.base_objects.Channel;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Sendung;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.utils.Playback;
import com.marcn.mediathek.utils.Storage;
import com.marcn.mediathek.utils.ZdfMediathekData;

import java.io.IOException;
import java.util.TreeMap;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnVideoInteractionListener {

    public static String FRAGMENT_NAME_FIRST_PAGE = "first-fragment";

    abstract void navigationIdReceived(int id);

    void loadCleanFragment(Fragment fragment, int containerId) {
        loadCleanFragment(fragment, containerId, "0");
    }

    void loadCleanFragment(Fragment fragment, int containerId, String name) {
        loadCleanFragment(fragment, containerId, name, name);
    }

    void loadCleanFragment(Fragment fragment, int containerId, String name, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(containerId, fragment, tag);
        transaction.addToBackStack(name);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        int id = item.getItemId();
        navigationIdReceived(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setActionBarTitle(int resId) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(resId);
    }

    @Override
    public void onLiveStreamClicked(final LiveStream liveStream, final View view, final int videoAction) {
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Playback.playByUrl(activity, liveStream.getLiveM3U8(view.getContext()), view, videoAction, liveStream.channel);
            }
        });
    }

    @Override
    public void onVideoClicked(final Video video, final View view, final int videoAction) {
        final Activity activity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TreeMap<Integer, String> s = ZdfMediathekData.getVideoUrl(view.getContext(), video.assetId);
                    if (s != null && !s.isEmpty())
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Playback.playByUrl(activity, s.get(0), view, videoAction, video.title);
                            }
                        });
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    @Override
    public void onSendungClicked(Sendung sendung, View thumbnail, View logo) {
        if (sendung == null) return;

        Intent intent = new Intent(this, SeriesActivity.class);
        Gson gson = new Gson();
        String json = gson.toJson(sendung);
        intent.putExtra(SeriesActivity.INTENT_SENDUNG_JSON, json);

        ImageView imageView = (ImageView) thumbnail;
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Storage.saveBitmapOnDisk(this, bmp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());

            thumbnail.setTransitionName("thumbnail");
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, thumbnail, "thumbnail");
            startActivity(intent, options.toBundle());
        } else
            startActivity(intent);
    }

    @Override
    public void onChannelClicked(Channel channel, View view) {
        if (channel == null) return;
        Toast.makeText(this, channel.title, Toast.LENGTH_SHORT).show();
    }

    void startChannelActivity(Channel channel) {
        if (channel == null) return;

        Intent intent = new Intent(this, ChannelActivity.class);
        Gson gson = new Gson();
        String json = gson.toJson(channel);
        intent.putExtra(ChannelActivity.INTENT_SENDER_JSON, json);

//        ImageView imageView = (ImageView) thumbnail;
//        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        saveBitmapOnDisk(bmp);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setExitTransition(new Explode());
//
//            thumbnail.setTransitionName("thumbnail");
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
//                    Pair.create(thumbnail, "thumbnail"));
//            startActivity(intent, options.toBundle());
//        } else
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
}
