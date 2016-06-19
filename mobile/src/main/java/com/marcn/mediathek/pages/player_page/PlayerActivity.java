package com.marcn.mediathek.pages.player_page;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.view.View;

import com.marcn.mediathek.R;
import com.marcn.mediathek.pages.CoordinatorActivity;
import com.marcn.mediathek.player.PlayerFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends CoordinatorActivity {
    public static final String INTENT_LIVE_STREAM_URL = "intent-live-stream";

    @BindView(R.id.bottom_sheet)
    ViewStubCompat mBottomSheet;

    @BindView(R.id.bottom_sheet_2)
    RecyclerView mBottomSheet2;

//    @BindView(R.id.bottom_player_container)
//    LinearLayout mBottomContainer;

    private BottomSheetBehavior<View> mBottomBehaviour;

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
        ButterKnife.bind(this);

        PlayerFragment playerFragment
                = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.player_fragment);

        mBottomBehaviour = BottomSheetBehavior.from(mBottomSheet2);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String liveUrl = intent.getStringExtra(INTENT_LIVE_STREAM_URL);
            playerFragment.setBottomBar(mBottomSheet);
            playerFragment.setBottomContainer(mBottomSheet2);
            playerFragment.play(liveUrl);
        }
    }

    public void showBottomBar() {
        mBottomBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void hideBottomBar() {
        mBottomBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
