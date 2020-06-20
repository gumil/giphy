package com.gumil.giphy.network

import com.gumil.giphy.network.repository.GiphyRepository
import com.gumil.giphy.network.repository.Repository

fun createGiphyRepository(isDebug: Boolean, apiKey: String): Repository {
    return GiphyRepository(ApiFactory.createGiphyApi(isDebug), apiKey)
}
