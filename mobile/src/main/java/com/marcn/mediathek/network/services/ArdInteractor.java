package com.marcn.mediathek.network.services;

import com.marcn.mediathek.model.ard.ArdLive;
import com.marcn.mediathek.model.ard.ArdVideo;
import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.model.pages.ArdPage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

public class ArdInteractor {
    private ArdService mService;

    @Inject
    public ArdInteractor(ArdService service) {
        mService = service;
    }

    public Observable<ArdPage> loadHome() {
        return mService.getOverviewPage()
                .map(Response::body)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Stream>> loadAllAtoZ() {
        List<Stream> streams = new ArrayList<>();
        return mService.getAllAtoZ()
                .map(Response::body)
                .map(ArdPage::getAtoZSeries)
                .flatMap(Observable::from)
                .filter(asset -> asset instanceof ArdLive)
                .cast(ArdLive.class)
                .collect(() -> streams, (b, s) -> streams.add(s))
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Stream>> loadLivestreams() {
        List<Stream> streams = new ArrayList<>();
        return mService.getLivePage()
                .map(Response::body)
                .map(ArdPage::getLiveSreamList)
                .flatMap(Observable::from)
                .filter(asset -> asset instanceof ArdLive)
                .cast(ArdLive.class)
                .collect(() -> streams, (b, s) -> streams.add(s))
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> loadVideo(String videoId) {
        return mService.getVideo(videoId)
                .map(Response::body)
                .map(ArdVideo::getUrl)
                .subscribeOn(Schedulers.io());
    }
}
