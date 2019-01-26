package com.gumil.giphy.network.repository

import com.gumil.giphy.network.Giphy
import com.gumil.giphy.network.GiphyApi
import io.reactivex.Observable

internal class GiphyRepository(
    private val giphyApi: GiphyApi
) : Repository {

    override fun getTrending(offset: Int, limit: Int): Observable<List<Giphy>> {
        return giphyApi.getTrending(offset, limit).map { it.data }
    }

    override fun getRandomGif(): Observable<Giphy> {
        return giphyApi.getRandomGif().map { it.data }
    }
}