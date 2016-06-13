package com.marcn.mediathek.network.services;

import com.marcn.mediathek.model.pages.ArtePage;

import retrofit2.Response;
import retrofit2.http.GET;
import rx.Observable;

public interface ArteService {

    @GET("mediathek/start-highlights?page=0")
    Observable<Response<ArtePage>> getOverviewPage();

}
