package com.gumil.giphy.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.ImageItem
import com.gumil.giphy.R
import com.gumil.giphy.TestDispatcherRule
import com.gumil.giphy.TestRepository
import com.gumil.giphy.UserItem
import com.gumil.giphy.collectInTest
import com.gumil.giphy.util.Cache
import io.mockk.Ordering
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Before
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

    @Before
    fun setUp() = runBlocking {
        yield() // Make sure to finish up initialization
    }

    @Test
    fun actionRefresh() = runBlocking {
        val observer = mockk<(ListState) -> Unit>(relaxed = true)
        val observerJob = viewModel.state.take(3).collectInTest(this, observer)

        viewModel.dispatch(flowOf(ListAction.Refresh())).launchIn(this).join()
        observerJob.join()

        verify(exactly = 0) { cache.get<List<GiphyItem>>(any()) }
        coVerify(ordering = Ordering.ORDERED) {
            observer.invoke(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
            observer.invoke(ListState.Screen(list, ListState.Mode.REFRESH))
            observer.invoke(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
        }
        confirmVerified(observer)
    }

    @Test
    fun actionLoadMore() = runBlocking {
        val giphies = list.toMutableList().apply { addAll(list) }
        val observer = mockk<(ListState) -> Unit>(relaxed = true)
        val observerJob = viewModel.state.take(3).collectInTest(this, observer)

        viewModel.dispatch(flowOf(ListAction.LoadMore(0))).launchIn(this).join()
        observerJob.join()

        verify(exactly = 1) { cache.save<Any>(any(), any()) }
        coVerify(ordering = Ordering.ORDERED) {
            observer.invoke(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
            observer.invoke(ListState.Screen(list, ListState.Mode.LOAD_MORE))
            observer.invoke(ListState.Screen(giphies, ListState.Mode.IDLE_LOAD_MORE))
        }
        confirmVerified(observer)
    }

    @Test
    fun actionOnItemClick() = runBlocking {
        val observer = mockk<(ListState) -> Unit>(relaxed = true)
        val observerJob = viewModel.state.take(2).collectInTest(this, observer)

        viewModel.dispatch(flowOf(ListAction.OnItemClick(list[0]))).launchIn(this).join()
        observerJob.join()

        coVerify(ordering = Ordering.ORDERED) {
            observer.invoke(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
            observer.invoke(ListState.GoToDetail(list[0]))
        }
        confirmVerified(observer)
    }

    @Test
    fun actionOnError() = runBlocking {
        val observer = mockk<(ListState) -> Unit>(relaxed = true)
        val observerJob = viewModel.state.take(2).collectInTest(this, observer)

        viewModel.dispatch(flowOf(ListAction.OnError(Exception()))).launchIn(this).join()
        observerJob.join()

        coVerify(ordering = Ordering.ORDERED) {
            observer.invoke(ListState.Screen(list, ListState.Mode.IDLE_REFRESH))
            observer.invoke(ListState.Error(R.string.error_loading))
        }
        confirmVerified(observer)
    }
}
