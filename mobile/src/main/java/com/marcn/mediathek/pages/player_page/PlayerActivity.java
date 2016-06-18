package com.marcn.mediathek.pages.player_page;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;

import com.marcn.mediathek.R;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.player.PlayerControls;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends BaseActivity {

    public static final String INTENT_LIVE_STREAM_URL = "intent-live-stream";

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        hideWindowBackground();

        mNavigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String liveUrl = intent.getStringExtra(INTENT_LIVE_STREAM_URL);
            ((PlayerControls) getSupportFragmentManager().findFragmentById(R.id.player_fragment)).play(liveUrl);
        }
    }
}
