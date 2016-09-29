package com.marcn.mediathek.network.services;

import com.marcn.mediathek.model.ard.ArdVideo;
import com.marcn.mediathek.model.pages.ArdPage;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ArdService {
    @GET("/appdata/servlet/tv?json")
    Observable<Response<ArdPage>> getOverviewPage();

    @GET("/appdata/servlet/tv/live?json")
    Observable<Response<ArdPage>> getLivePage();

    @GET("/appdata/servlet/play/media/{videoId}?devicetype=tablet&features=hls")
    Observable<Response<ArdVideo>> getVideo(@Path("videoId") String videoId);

    @GET("/appdata/servlet/tv/sendungAbisZ?json")
    Observable<Response<ArdPage>> getAllAtoZ();

}

