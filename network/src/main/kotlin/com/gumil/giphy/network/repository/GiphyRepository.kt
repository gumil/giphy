package com.gumil.giphy.network.repository

import com.gumil.giphy.network.Giphy
import com.gumil.giphy.network.GiphyApi

internal class GiphyRepository(
    private val giphyApi: GiphyApi,
    private val apiKey: String
) : Repository {

    override suspend fun getTrending(offset: Int, limit: Int): List<Giphy> {
        return giphyApi.getTrending(apiKey, offset, limit).data
    }

    override suspend fun getRandomGif(): Giphy {
        return giphyApi.getRandomGif(apiKey).data
    }
}