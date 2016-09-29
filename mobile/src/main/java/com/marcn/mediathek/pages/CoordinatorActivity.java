package com.marcn.mediathek.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.view.MenuItem;

import com.marcn.mediathek.R;

import butterknife.BindView;

public abstract class CoordinatorActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Nullable
    @BindView(R.id.coordinator_app_bar_layout)
    AppBarLayout mAppBarLayout;

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasAppBarLayout()) {
            setContentView(R.layout.activity_with_app_bar);
        } else {
            setContentView(R.layout.activity_wo_app_bar);
        }

        ViewStubCompat viewStub = (ViewStubCompat) findViewById(R.id.coordinator_content);
        viewStub.setLayoutResource(getContentResource());
        viewStub.inflate();

        setUpActivity(savedInstanceState);
        initLayout();
    }

    private void initLayout() {
        mNavigationView.setNavigationItemSelectedListener(this);
        if (hasAppBarLayout()) {
            setSupportActionBar(mToolbar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawer.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    protected boolean hasAppBarLayout() {
        return true;
    }

    protected abstract int getContentResource();

    protected abstract void setUpActivity(Bundle savedInstanceState);

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_live:
                mNavigationManager.goToLiveStream();
                break;
            case R.id.nav_missed:
                mNavigationManager.gotToAllMissed();
                break;
            case R.id.nav_all_series:
                mNavigationManager.gotToAtoZ();
                break;
        }
        mNavigationView.setCheckedItem(id);
        return true;
    }
}
