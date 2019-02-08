package com.gumil.giphy.network.repository

import com.gumil.giphy.network.Giphy
import io.reactivex.Observable

interface Repository {

    fun getTrending(offset: Int = 0, limit: Int = 20): Observable<List<Giphy>>

    fun getRandomGif(): Observable<Giphy>
}