package com.gumil.giphy.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.ImageItem
import com.gumil.giphy.R
import com.gumil.giphy.TestDispatcherRule
import com.gumil.giphy.TestRepository
import com.gumil.giphy.collectInTest
import io.mockk.Ordering
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

class GiphyDetailViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get:Rule
    val trampolineRule = TestDispatcherRule()

    private val viewModel = GiphyDetailViewModel(TestRepository(), SavedStateHandle())

    private val giphy = GiphyItem("amused GIF", null,
        ImageItem("https://media2.giphy.com/media/TaNz4CeKR7O1y/giphy-downsized.gif",
            "https://media2.giphy.com/media/TaNz4CeKR7O1y/200w_d.gif",
            498, 276)
    )

    @Test
    fun actionLoadGif() = runBlockingTest {
        viewModel.restore(DetailState.Screen(giphy))
        val observer = mockk<(DetailState) -> Unit>(relaxed = true)
        val observerJob = viewModel.state.take(1).collectInTest(this, observer)

        viewModel.dispatch(flowOf(DetailAction.GetRandomGif)).launchIn(this).join()
        observerJob.join()

        verify(exactly = 1) { observer.invoke(DetailState.Screen(giphy)) }
        confirmVerified(observer)
    }

    @Test
    fun actionOnError() = runBlocking {
        viewModel.restore(DetailState.Error(1))
        val observer = mockk<(DetailState) -> Unit>(relaxed = true)
        val observerJob = viewModel.state.take(2).collectInTest(this, observer)

        viewModel.dispatch(flowOf(DetailAction.OnError(Exception()))).launchIn(this).join()
        observerJob.join()

        verify(ordering = Ordering.ORDERED) {
            observer.invoke(DetailState.Error(1))
            observer.invoke(DetailState.Error(R.string.error_loading_single))
        }
        confirmVerified(observer)
    }
}
