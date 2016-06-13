package com.marcn.mediathek.network.services;

import com.marcn.mediathek.model.base.Stream;
import com.marcn.mediathek.model.pages.ZdfPage;
import com.marcn.mediathek.model.zdf.ZdfLive;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

public class ZdfInteractor {
    private ZdfService mService;

    @Inject
    public ZdfInteractor(ZdfService service) {
        mService = service;
    }

    public Observable<ZdfPage> loadZdfPage() {
        return mService.getOverviewPage()
                .map(Response::body)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Stream>> loadZdfStreams() {
        List<Stream> streams = new ArrayList<>();
        return mService.getOverviewPage()
                .map(Response::body)
                .map(z -> z.getTeaserList(ZdfPage.CATEGORY_TYPE_LIVE))
                .flatMap(Observable::from)
                .filter(asset -> asset instanceof ZdfLive)
                .filter(asset -> !((ZdfLive) asset).getId().equals("0"))
                .cast(ZdfLive.class)
                .collect(() -> streams, (b, s) -> streams.add(s))
                .subscribeOn(Schedulers.io());
    }

    public Observable<ZdfPage> loadMissed(String date) {
        return mService.getMissedShows(date)
                .map(Response::body)
                .subscribeOn(Schedulers.io());
    }

    public Observable<ZdfPage> loadAtoZ(String start, String end) {
        return mService.getAtoZ(start, end)
                .map(Response::body)
                .subscribeOn(Schedulers.io());
    }

    public Observable<ZdfPage> loadRubrics(long id) {
        String value = id >= 0 ? Long.toString(id) : "";
        return mService.getRubrics(value)
                .map(Response::body)
                .subscribeOn(Schedulers.io());
    }

    public Observable<ZdfPage> loadStations(long id, int page) {
        String value = id >= 0 ? Long.toString(id) : "";
        return mService.getStations(value, page)
                .map(Response::body)
                .subscribeOn(Schedulers.io());
    }

    public Observable<ZdfPage> loadThemes(long id, int page) {
        String value = id >= 0 ? Long.toString(id) : "";
        return mService.getThemes(value, page)
                .map(Response::body)
                .subscribeOn(Schedulers.io());
    }

    public Observable<ZdfPage> loadEpg() {
        return mService.getEpg()
                .map(Response::body)
                .subscribeOn(Schedulers.io());
    }
}
