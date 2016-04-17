package com.marcn.mediathek.pages.player_page;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;

import com.marcn.mediathek.R;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.ui_fragments.VideoFragment;

public class VideoActivity extends BaseActivity {
    public static final String INTENT_VIDEO_TYPE = "video-type";
    public static final String INTENT_VIDEO_ASSET_ID = "asset-id";
    public static final String INTENT_VIDEO_TITLE = "title";
    public static final String INTENT_VIDEO_CHANNEL_TITLE = "channel-title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

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
//            String assetId = intent.getStringExtra(INTENT_VIDEO_ASSET_ID);
            String title = intent.getStringExtra(INTENT_VIDEO_TITLE);
            String channelTitle = intent.getStringExtra(INTENT_VIDEO_CHANNEL_TITLE);
            VideoFragment videoFragment = VideoFragment.newInstance(channelTitle, null, title);
            loadCleanFragment(videoFragment, R.id.content_main, FRAGMENT_NAME_FIRST_PAGE);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void navigationIdReceived(int id) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void setExitTransition() {
        getWindow().setExitTransition(new Explode());
    }
}
