package com.gumil.giphy.data.network

import com.gumil.giphy.BuildConfig
import com.gumil.giphy.data.GiphyListResponse
import com.gumil.giphy.data.GiphyResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GiphyApi {

    @GET("v1/gifs/trending?api_key=${BuildConfig.API_KEY}")
    fun getTrending(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10
    ): Observable<GiphyListResponse>

    @GET("/v1/gifs/random?api_key=${BuildConfig.API_KEY}")
    fun getRandomGif(): Observable<GiphyResponse>
}