package com.marcn.mediathek.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api
{
    @GET("mediathek/sendungverpasst/{date}")
    Call<Zdf> zdfListMissedEpisodes(@Path("date") String date);
}
