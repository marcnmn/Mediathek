package com.marcn.mediathek.pages.player_page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;

import com.marcn.mediathek.R;
import com.marcn.mediathek.di.InjectHelper;
import com.marcn.mediathek.pages.CoordinatorActivity;
import com.marcn.mediathek.player.PlayerFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends CoordinatorActivity {
    public static final String INTENT_LIVE_STREAM_URL = "intent-live-stream";

    @Override
    protected boolean hasAppBarLayout() {
        return false;
    }

    @Override
    protected int getContentResource() {
        return R.layout.content_player;
    }

    @Override
    protected void setUpActivity(Bundle savedInstanceState) {
        InjectHelper.setupPage(this);
        ButterKnife.bind(this);
        hideWindowBackground();

        PlayerFragment playerFragment
                = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.player_fragment);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String liveUrl = intent.getStringExtra(INTENT_LIVE_STREAM_URL);
            playerFragment.play(liveUrl);
        }
    }
}
