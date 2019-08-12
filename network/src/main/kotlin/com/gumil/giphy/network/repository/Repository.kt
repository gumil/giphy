package com.gumil.giphy.network.repository

import com.gumil.giphy.network.Giphy

interface Repository {

    suspend fun getTrending(offset: Int = 0, limit: Int = 20): List<Giphy>

    suspend fun getRandomGif(): Giphy
}