package com.gumil.giphy.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GiphyApi {

    @GET("v1/gifs/trending?api_key=$API_KEY")
    fun getTrending(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10
    ): Observable<GiphyListResponse>

    @GET("/v1/gifs/random?api_key=$API_KEY")
    fun getRandomGif(): Observable<GiphyResponse>

    companion object {
         private const val API_KEY = "add api key here"
    }
}