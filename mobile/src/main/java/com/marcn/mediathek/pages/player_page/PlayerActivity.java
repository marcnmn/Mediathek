package com.marcn.mediathek.pages.player_page;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.transition.Fade;

import com.marcn.mediathek.R;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.pages.home.MainActivity;
import com.marcn.mediathek.ui_fragments.PlayerFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends BaseActivity {

    public static final String INTENT_LIVE_STREAM_URL = "intent-live-stream";
    public PlayerFragment playerFragment;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    private PlayerFragment mPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        hideWindowBackground();

        mNavigationView.setNavigationItemSelectedListener(this);
        mPlayerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.player_fragment);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String liveUrl = intent.getStringExtra(INTENT_LIVE_STREAM_URL);
            mPlayerFragment.setStreamUrl(liveUrl);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPlayerFragment.updateLayout(newConfig);
    }

    @Override
    public void navigationIdReceived(int id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_LIVE_DRAWER_ITEM, id);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void setExitTransition() {
        getWindow().setExitTransition(new Fade());
    }
}
