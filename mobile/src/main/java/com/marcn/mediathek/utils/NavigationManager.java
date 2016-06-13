package com.marcn.mediathek.utils;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.marcn.mediathek.R;
import com.marcn.mediathek.model.ard.ArdLive;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.network.services.ArdInteractor;
import com.marcn.mediathek.network.services.ZdfInteractor;
import com.marcn.mediathek.pages.player_page.PlayerActivity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class NavigationManager {
    private final ArdInteractor mArdInteractor;
    private final ZdfInteractor mZdfInteractor;
    private final Activity mContext;

    CompositeSubscription mSubscription = new CompositeSubscription();

    @Inject
    public NavigationManager(Activity context, ArdInteractor ardInteractor, ZdfInteractor zdfInteractor) {
        mContext = context;
        mArdInteractor = ardInteractor;
        mZdfInteractor = zdfInteractor;
    }

    public void startLiveStream(Stream stream, PlayerType type) {
        if (stream instanceof ArdLive) {
            String id = ((ArdLive) stream).getId();
            mSubscription.add(
                    mArdInteractor.loadVideo(id)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> startPlayer(s, type), this::onError)
            );
        }
    }

    private void startPlayer(String url, PlayerType playerType) {
        switch (playerType) {
            case INTERNAL:
                startInternalPlayer(url);
                break;
            case EXTERNAL:
                startExternalPlayer(url);
                break;
        }
    }

    private void startInternalPlayer(String url) {
        Intent intent = new Intent(mContext, PlayerActivity.class);
        intent.putExtra(PlayerActivity.INTENT_LIVE_STREAM_URL, url);
        mContext.startActivity(intent);
    }

    private void startExternalPlayer(String url) {
        if (url == null) return;
        Intent stream = new Intent(Intent.ACTION_VIEW);
        stream.setDataAndType(Uri.parse(url), "video/*");
        mContext.startActivity(Intent.createChooser(stream,
                mContext.getResources().getText(R.string.send_to_intent)));
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public enum PlayerType {
        INTERNAL,
        EXTERNAL,
        DOWNLOAD
    }
}
