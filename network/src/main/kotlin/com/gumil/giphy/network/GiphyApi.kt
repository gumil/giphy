package com.gumil.giphy.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GiphyApi {

    @GET("v1/gifs/trending")
    fun getTrending(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10
    ): Observable<GiphyListResponse>

    @GET("/v1/gifs/random")
    fun getRandomGif(
        @Query("api_key") apiKey: String
    ): Observable<GiphyResponse>
}