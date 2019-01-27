package com.gumil.giphy.network

import com.gumil.giphy.network.repository.GiphyRepository
import com.gumil.giphy.network.repository.Repository
import org.koin.dsl.module

fun createNetworkModule(isDebug: Boolean) = module {
    single<Repository> { GiphyRepository(ApiFactory.createGiphyApi(isDebug)) }
}