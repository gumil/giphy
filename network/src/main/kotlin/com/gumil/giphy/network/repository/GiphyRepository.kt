package com.gumil.giphy.network.repository

import com.gumil.giphy.network.Giphy
import com.gumil.giphy.network.GiphyApi
import io.reactivex.Observable

internal class GiphyRepository(
    private val giphyApi: GiphyApi,
    private val apiKey: String
) : Repository {

    override fun getTrending(offset: Int, limit: Int): Observable<List<Giphy>> {
        return giphyApi.getTrending(apiKey, offset, limit).map { it.data }
    }

    override fun getRandomGif(): Observable<Giphy> {
        return giphyApi.getRandomGif(apiKey).map { it.data }
    }
}