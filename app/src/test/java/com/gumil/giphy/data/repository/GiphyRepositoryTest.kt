package com.gumil.giphy.data.repository

import com.gumil.giphy.createMockResponse
import com.gumil.giphy.data.Downsized
import com.gumil.giphy.data.FixedWidthDownsampled
import com.gumil.giphy.data.Giphy
import com.gumil.giphy.data.Images
import com.gumil.giphy.data.Original
import com.gumil.giphy.data.network.ApiFactory
import com.gumil.giphy.readFromFile
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

internal class GiphyRepositoryTest {
    private val mockServer = MockWebServer().apply { start() }

    private val giphyApi = ApiFactory.createGiphyApi(true, mockServer.url("/").toString())

    private val repository = GiphyRepository(giphyApi)

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
    }

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