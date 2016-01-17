package com.marcn.mediathek;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.marcn.mediathek.ui_fragments.PlayerFragment;

public class PlayerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String INTENT_LIVE_STREAM_URL = "intent-live-stream";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String liveUrl = intent.getStringExtra(INTENT_LIVE_STREAM_URL);
            loadCleanFragment(PlayerFragment.newInstance(liveUrl), false);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_live) {
            loadMainFragment(id);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void loadMainFragment(int id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_LIVE_DRAWER_ITEM, id);
        startActivity(intent);
    }
}
