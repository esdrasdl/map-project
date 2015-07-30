package com.gmail.esdrasdl.maps.data.net;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by esdras on 29/07/15.
 */
public interface API {
    static final String API_BASE_URL = "http://pastebin.com/";

    @GET("/raw.php")
    void getFavoriteList(@Query("i") String param, Callback<FavoritesEntity> callback);
}
