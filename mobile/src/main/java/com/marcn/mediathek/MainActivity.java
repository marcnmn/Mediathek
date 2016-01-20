package com.marcn.mediathek;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.marcn.mediathek.Interfaces.OnVideoInteractionListener;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.ui_fragments.LiveStreamsFragment;
import com.marcn.mediathek.ui_fragments.VideoListFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LiveStreamsFragment.OnListFragmentInteractionListener,
        OnVideoInteractionListener {

    public static final String INTENT_LIVE_DRAWER_ITEM = "player-drawer-item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            int navId = intent.getIntExtra(INTENT_LIVE_DRAWER_ITEM, -1);
            navigationIdReceived(navId);
        } else {
            loadCleanFragment(LiveStreamsFragment.newInstance(1), false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        navigationIdReceived(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void navigationIdReceived(int id) {
        if (id == R.id.nav_live) {
            loadCleanFragment(new LiveStreamsFragment());
        } else if (id == R.id.nav_gallery) {
            loadCleanFragment(new VideoListFragment());
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
    }

    private void loadCleanFragment(Fragment fragment) {
        loadCleanFragment(fragment, true);
    }

    private void loadCleanFragment(Fragment fragment, boolean backstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.content_main, fragment, "0");
        if (backstack)
            transaction.addToBackStack("0");
        transaction.commit();
    }

    public void setActionBarTitle(int resId) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(resId);
    }

    @Override
    public void onListFragmentInteraction(LiveStream item, View view) {
        if (item.channel == null || item.getLiveM3U8(this) == null) return;

        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.INTENT_LIVE_STREAM_URL, item.getLiveM3U8(this));

        ImageView imageView = (ImageView) view;
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        createImageFromBitmap(bmp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());

            view.setTransitionName("thumb");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(view, "thumb"));
            startActivity(intent, options.toBundle());
        } else
            startActivity(intent);
    }

    @Override
    public void onVideoInteraction(final String url, final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startPlayer(url, view);
            }
        });
    }

    public void startPlayer(String url, View view) {
        if (url == null) return;

        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.INTENT_LIVE_STREAM_URL, url);

        ImageView imageView = (ImageView) view;
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        createImageFromBitmap(bmp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());

            view.setTransitionName("thumb");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(view, "thumb"));
            startActivity(intent, options.toBundle());
        } else
            startActivity(intent);
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "thumbnail";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

}
