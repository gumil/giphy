package com.gumil.giphy.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.ImageItem
import com.gumil.giphy.R
import com.gumil.giphy.TestRepository
import com.gumil.giphy.UserItem
import com.gumil.giphy.TestDispatcherRule
import com.gumil.giphy.util.Cache
import dev.gumil.kaskade.flow.MutableEmitter
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class GiphyListViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val trampolineRule = TestDispatcherRule()

    private val list = listOf(
        GiphyItem("amused GIF", null,
            ImageItem("https://media2.giphy.com/media/TaNz4CeKR7O1y/giphy-downsized.gif",
                "https://media2.giphy.com/media/TaNz4CeKR7O1y/200w_d.gif",
                498, 276)
        ),
        GiphyItem("title",
            UserItem("avatar", "profile", "name"),
            ImageItem("downsized", "downsampled", 50, 50))
    )

    private val cache = mockk<Cache>(relaxed = true)

    private val viewModel = GiphyListViewModel(TestRepository(), cache)

    @Test
    fun actionRefresh() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        val emitter = MutableEmitter<ListAction>()
        viewModel.process(emitter)
        emitter.sendValue(ListAction.Refresh())

        verify(exactly = 0) { cache.get<List<GiphyItem>>(any()) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH)) }
        confirmVerified(observer)
    }

    @Test
    fun actionLoadMore() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        val emitter = MutableEmitter<ListAction>()
        viewModel.process(emitter)
        emitter.sendValue(ListAction.LoadMore(0))

        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.LOAD_MORE)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_LOAD_MORE)) }
        verify(exactly = 1) { cache.save<Any>(any(), any()) }
        confirmVerified(observer)
    }

    @Test
    fun actionOnItemClick() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        val emitter = MutableEmitter<ListAction>()
        viewModel.process(emitter)
        emitter.sendValue(ListAction.OnItemClick(list[0]))

        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.GoToDetail(list[0])) }
        confirmVerified(observer)
    }

    @Test
    fun actionOnError() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        val emitter = MutableEmitter<ListAction>()
        viewModel.process(emitter)
        emitter.sendValue(ListAction.OnError(Exception()))

        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Error(R.string.error_loading)) }
        confirmVerified(observer)
    }

    @Test
    fun `restore does not have saved instance should emit refresh`() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.restore()

        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH)) }
        confirmVerified(observer)
    }

    @Test
    fun `restore does have saved instance and first call should emit refresh`() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.restore()

        verify(exactly = 0) { cache.get<List<GiphyItem>>(any()) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH)) }
        confirmVerified(observer)
    }

    @Test
    fun `restore does have saved instance and two calls should not emit refresh twice`() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.restore()
        viewModel.restore()

        verify(exactly = 0) { cache.get<List<GiphyItem>>(any()) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH)) }
        confirmVerified(observer)
    }

    @Test
    fun `restore with limit greater than default not in cache`() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)
        every { cache.get<List<GiphyItem>>(any()) } returns null

        viewModel.restore(30)

        verify(exactly = 1) { cache.get<List<GiphyItem>>(any()) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH)) }
        confirmVerified(observer)
    }

    @Test
    fun `restore with limit greater than default and in cache`() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)
        every { cache.get<List<GiphyItem>>(any()) } returns list

        viewModel.restore(30)

        verify(exactly = 1) { cache.get<List<GiphyItem>>(any()) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 0) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.REFRESH)) }
        confirmVerified(observer)
    }
}