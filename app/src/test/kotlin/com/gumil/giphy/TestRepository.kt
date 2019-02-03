package com.gumil.giphy

import com.gumil.giphy.network.Downsized
import com.gumil.giphy.network.FixedWidthDownsampled
import com.gumil.giphy.network.Giphy
import com.gumil.giphy.network.Images
import com.gumil.giphy.network.Original
import com.gumil.giphy.network.User
import com.gumil.giphy.network.repository.Repository
import com.gumil.giphy.util.just
import io.reactivex.Observable

internal class TestRepository : Repository {
    override fun getTrending(offset: Int, limit: Int): Observable<List<Giphy>> {
        return listOf(
            Giphy(
                null,
                Images(
                    Original(
                        "https://media2.giphy.com/media/TaNz4CeKR7O1y/giphy.gif",
                        "498",
                        "276"
                    ), FixedWidthDownsampled(
                        "https://media2.giphy.com/media/TaNz4CeKR7O1y/200w_d.gif"
                    ), Downsized(
                        "https://media2.giphy.com/media/TaNz4CeKR7O1y/giphy-downsized.gif"
                    )
                ),
                "amused GIF"
            ),
            Giphy(
                User("avatar", "profile", "name"),
                Images(
                    Original(
                        "original",
                        "50",
                        "50"
                    ), FixedWidthDownsampled(
                        "downsampled"
                    ), Downsized(
                        "downsized"
                    )
                ),
                "title"
            )
        ).just()
    }

    override fun getRandomGif(): Observable<Giphy> {
        return Giphy(
            null,
            Images(
                Original(
                    "https://media2.giphy.com/media/TaNz4CeKR7O1y/giphy.gif",
                    "498",
                    "276"
                ), FixedWidthDownsampled(
                    "https://media2.giphy.com/media/TaNz4CeKR7O1y/200w_d.gif"
                ), Downsized(
                    "https://media2.giphy.com/media/TaNz4CeKR7O1y/giphy-downsized.gif"
                )
            ),
            "amused GIF"
        ).just()
    }
}