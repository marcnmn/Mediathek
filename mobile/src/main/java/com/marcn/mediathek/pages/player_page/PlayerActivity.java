package com.marcn.mediathek.pages.player_page;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.transition.Fade;

import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.pages.home.MainActivity;
import com.marcn.mediathek.R;
import com.marcn.mediathek.ui_fragments.PlayerFragment;

public class PlayerActivity extends BaseActivity {

    public static final String INTENT_LIVE_STREAM_URL = "intent-live-stream";
    public PlayerFragment playerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setExitTransition();
        }

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String liveUrl = intent.getStringExtra(INTENT_LIVE_STREAM_URL);
            playerFragment = PlayerFragment.newInstance(liveUrl);
            loadCleanFragment(playerFragment, R.id.content_main, FRAGMENT_NAME_FIRST_PAGE);
        }
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
