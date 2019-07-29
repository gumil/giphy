package com.gumil.giphy.network

import com.gumil.giphy.network.repository.GiphyRepository
import com.gumil.giphy.network.repository.Repository
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val NAME_API_KEY = "apiKey"

fun createNetworkModule(isDebug: Boolean) = module {
    single<Repository> { GiphyRepository(ApiFactory.createGiphyApi(isDebug), get(named(NAME_API_KEY))) }
}