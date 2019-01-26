package com.gumil.giphy.data.repository

import com.gumil.giphy.data.Giphy
import io.reactivex.Observable

interface Repository {

    fun getTrending(offset: Int = 0, limit: Int = 10): Observable<List<Giphy>>

    fun getRandomGif(): Observable<Giphy>
}