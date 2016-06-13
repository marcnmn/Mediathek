package com.marcn.mediathek.network.services;

import com.marcn.mediathek.model.pages.ZdfPage;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ZdfService {

    @GET("mediathek/start-highlights?page=0")
    Observable<Response<ZdfPage>> getOverviewPage();

    @GET("mediathek/sendungverpasst/{date}")
    Observable<Response<ZdfPage>> getMissedShows(@Path("date") String date);

    @GET("mediathek/a-z/{startLetter}/{endLetter}")
    Observable<Response<ZdfPage>> getAtoZ(@Path("startLetter") String start,
                                          @Path("endLetter") String endLetter);

    @GET("mediathek/rubriken/{id}")
    Observable<Response<ZdfPage>> getRubrics(@Path("id") String id);

    @GET("mediathek/sender/{stationId}")
    Observable<Response<ZdfPage>> getStations(@Path("stationId") String stationId,
                                              @Query("page") int pageNo);

    @GET("mediathek/sender/{themeId}")
    Observable<Response<ZdfPage>> getThemes(@Path("themeId") String themeId,
                                            @Query("page") int pageNo);

    @GET("epg/stations")
    Observable<Response<ZdfPage>> getEpg();

}
