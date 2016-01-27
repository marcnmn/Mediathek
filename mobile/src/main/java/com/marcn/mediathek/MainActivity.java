package com.marcn.mediathek;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.marcn.mediathek.base_objects.Channel;
import com.marcn.mediathek.ui_fragments.LiveStreamsFragment;
import com.marcn.mediathek.ui_fragments.SendungenAbisZFragment;
import com.marcn.mediathek.ui_fragments.VideoListFragment;

public class MainActivity extends BaseActivity {

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
        navigationView.setItemIconTintList(null);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            int navId = intent.getIntExtra(INTENT_LIVE_DRAWER_ITEM, -1);
            navigationIdReceived(navId);
        } else {
            loadCleanFragment(LiveStreamsFragment.newInstance(1), R.id.content_main);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    void navigationIdReceived(int id) {
        if (id == R.id.nav_live) {
            loadCleanFragment(new LiveStreamsFragment());
        } else if (id == R.id.nav_gallery) {
            loadCleanFragment(new VideoListFragment());
        } else if (id == R.id.nav_zdf_mediathek) {
            loadCleanFragment(new SendungenAbisZFragment());
        } else if (id == R.id.nav_arte_mediathek) {
        } else if (id == R.id.nav_ard_mediathek) {
        } else if (id == R.id.nav_manage) {
        }

        Channel channel = null;
        switch (id) {
            case R.id.nav_ard: channel = new Channel(getString(R.string.ard_name)); break;
            case R.id.nav_zdf: channel = new Channel(getString(R.string.zdf_name)); break;
            case R.id.nav_arte: channel = new Channel(getString(R.string.arte_name)); break;
            case R.id.nav_3sat: channel = new Channel(getString(R.string.drei_sat_name)); break;
            case R.id.nav_phoenix: channel = new Channel(getString(R.string.phoenix_name)); break;
            case R.id.nav_zdf_neo: channel = new Channel(getString(R.string.zdf_neo_name)); break;
        }
        if (channel != null)
            startChannelActivity(channel);
    }

    private void loadCleanFragment(Fragment fragment) {
        loadCleanFragment(fragment, R.id.content_main);
    }
}
