package com.gumil.giphy.network.repository

import com.gumil.giphy.network.ApiFactory
import com.gumil.giphy.network.Downsized
import com.gumil.giphy.network.FixedWidthDownsampled
import com.gumil.giphy.network.Giphy
import com.gumil.giphy.network.Images
import com.gumil.giphy.network.Original
import com.gumil.giphy.network.createMockResponse
import com.gumil.giphy.network.readFromFile
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Test

internal class GiphyRepositoryTest {
    private val mockServer = MockWebServer().apply { start() }

    private val giphyApi = ApiFactory.createGiphyApi(true, mockServer.url("/").toString())

    private val repository = GiphyRepository(giphyApi, "")

    @Test
    fun getTrending() {
        val subscriber = TestObserver<List<Giphy>>()
        mockServer.enqueue(createMockResponse(readFromFile("giphies.json")))
        repository.getTrending(0, 10).subscribe(subscriber)

        subscriber.awaitTerminalEvent()
        subscriber.assertNoErrors()
        subscriber.assertNoTimeout()
        subscriber.assertValueCount(1)
        subscriber.assertComplete()
        assertEquals(3, subscriber.events.size)

        val list = subscriber.values().first()
        assertEquals(10, list.size)
        assertEquals("GIF by pamelaespino", list.first().title)
        assertEquals("happy holi GIF by Greetings", list.last().title)
    }

    @Test
    fun getRandomGif() {
        val subscriber = TestObserver<Giphy>()
        mockServer.enqueue(createMockResponse(readFromFile("random.json")))
        repository.getRandomGif().subscribe(subscriber)

        subscriber.awaitTerminalEvent()
        subscriber.assertNoErrors()
        subscriber.assertNoTimeout()
        subscriber.assertValueCount(1)
        subscriber.assertComplete()
        assertEquals(3, subscriber.events.size)

        assertEquals(
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
            )
            , subscriber.values().first()
        )
    }
}