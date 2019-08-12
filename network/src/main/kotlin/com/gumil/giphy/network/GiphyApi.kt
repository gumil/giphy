package com.gumil.giphy.network

import retrofit2.http.GET
import retrofit2.http.Query

internal interface GiphyApi {

    @GET("v1/gifs/trending")
    suspend fun getTrending(
        @Query("api_key") apiKey: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10
    ): GiphyListResponse

    @GET("/v1/gifs/random")
    suspend fun getRandomGif(
        @Query("api_key") apiKey: String
    ): GiphyResponse
}