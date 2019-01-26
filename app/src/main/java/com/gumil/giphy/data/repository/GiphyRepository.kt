package com.gumil.giphy.data.repository

import com.gumil.giphy.data.Giphy
import com.gumil.giphy.data.network.GiphyApi
import com.gumil.giphy.util.applySchedulers
import io.reactivex.Observable

internal class GiphyRepository(
    private val giphyApi: GiphyApi
) : Repository {

    override fun getTrending(offset: Int, limit: Int): Observable<List<Giphy>> {
        return giphyApi.getTrending(offset, limit).map {
            it.data
        }.applySchedulers()
    }

    override fun getRandomGif(): Observable<Giphy> {
        return giphyApi.getRandomGif().map {
            it.data
        }.applySchedulers()
    }
}