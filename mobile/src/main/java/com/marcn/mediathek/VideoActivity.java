package com.marcn.mediathek;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;

import com.google.android.exoplayer.VideoFrameReleaseTimeHelper;
import com.marcn.mediathek.base_objects.Channel;
import com.marcn.mediathek.ui_fragments.LiveStreamsFragment;
import com.marcn.mediathek.ui_fragments.SendungenAbisZFragment;
import com.marcn.mediathek.ui_fragments.ZdfMissedVideoFragment;
import com.marcn.mediathek.ui_fragments.ZdfVideoFragment;

public class VideoActivity extends BaseActivity {
    public static final String INTENT_VIDEO_TYPE = "video-type";
    public static final String INTENT_VIDEO_ASSET_ID = "asset-id";

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
        navigationView.setItemIconTintList(null);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null && intent.getIntExtra(INTENT_VIDEO_TYPE, -1) != -1) {
            String assetId = intent.getStringExtra(INTENT_VIDEO_ASSET_ID);
            int videoType = intent.getIntExtra(INTENT_VIDEO_TYPE, -1);
            ZdfVideoFragment videoFragment = ZdfVideoFragment.newInstance(assetId, videoType);
            loadCleanFragment(videoFragment, R.id.content_main, FRAGMENT_NAME_FIRST_PAGE);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    void navigationIdReceived(int id) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    void setExitTransition() {
        getWindow().setExitTransition(new Explode());
    }
}
