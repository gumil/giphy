package com.gumil.giphy.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.ImageItem
import com.gumil.giphy.R
import com.gumil.giphy.TestRepository
import com.gumil.giphy.UserItem
import com.gumil.giphy.util.just
import com.gumil.giphy.TrampolineSchedulerRule
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class GiphyListViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val trampolineRule = TrampolineSchedulerRule()

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

    private val viewModel = GiphyListViewModel(TestRepository())

    @Test
    fun actionRefresh() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.process(ListAction.Refresh.just())

        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_REFRESH)) }
        confirmVerified(observer)
    }

    @Test
    fun actionLoadMore() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.process(ListAction.LoadMore(0).just())

        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.LOAD_MORE)) }
        verify(exactly = 1) { observer.onChanged(ListState.Screen(list, ListState.Mode.IDLE_LOAD_MORE)) }
        confirmVerified(observer)
    }

    @Test
    fun actionOnItemClick() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.process(ListAction.OnItemClick(list[0]).just())

        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.GoToDetail(list[0])) }
        confirmVerified(observer)
    }

    @Test
    fun actionOnError() {
        val observer = mockk<Observer<ListState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        viewModel.process(ListAction.OnError(Exception()).just())

        verify(exactly = 1) { observer.onChanged(ListState.Screen(emptyList(), ListState.Mode.IDLE_REFRESH)) }
        verify(exactly = 1) { observer.onChanged(ListState.Error(R.string.error_loading)) }
        confirmVerified(observer)
    }
}