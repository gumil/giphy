package com.gumil.giphy.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.ImageItem
import com.gumil.giphy.R
import com.gumil.giphy.TestDispatcherRule
import com.gumil.giphy.TestRepository
import com.gumil.giphy.UserItem
import com.gumil.giphy.util.Cache
import io.mockk.Ordering
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.runBlocking
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

    private val savedStateHandle = SavedStateHandle()

    private val viewModel = GiphyListViewModel(TestRepository(), cache, savedStateHandle)

    @Test
    fun actionRefresh() = runBlocking {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.process(flowOf(ListAction.Refresh())).launchIn(this).join()

        verify(exactly = 0) { cache.get<List<GiphyItem>>(any()) }
        verify(ordering = Ordering.ORDERED) {
            observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
            observer.onChanged(ListState.Screen(list, ListState.Mode.REFRESH))
            observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
        }
        confirmVerified(observer)
    }

    @Test
    fun actionLoadMore() = runBlocking {
        val giphies = list.toMutableList().apply { addAll(list) }
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.process(flowOf(ListAction.LoadMore(0))).launchIn(this).join()

        verify(exactly = 1) { cache.save<Any>(any(), any()) }
        verify(ordering = Ordering.ORDERED) {
            observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
            observer.onChanged(ListState.Screen(list, ListState.Mode.LOAD_MORE))
            observer.onChanged(ListState.Screen(giphies, ListState.Mode.IDLE_LOAD_MORE))
        }
        confirmVerified(observer)
    }

    @Test
    fun actionOnItemClick() = runBlocking {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.process(flowOf(ListAction.OnItemClick(list[0]))).launchIn(this).join()

        verify(ordering = Ordering.ORDERED) {
            observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
            observer.onChanged(ListState.GoToDetail(list[0]))
        }
        confirmVerified(observer)
    }

    @Test
    fun actionOnError() = runBlocking {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.process(flowOf(ListAction.OnError(Exception()))).launchIn(this).join()

        verify(ordering = Ordering.ORDERED) {
            observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
            observer.onChanged(ListState.Error(R.string.error_loading))
        }
        confirmVerified(observer)
    }
}
