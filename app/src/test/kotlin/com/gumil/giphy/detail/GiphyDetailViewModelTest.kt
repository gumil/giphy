package com.gumil.giphy.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.ImageItem
import com.gumil.giphy.R
import com.gumil.giphy.TestRepository
import com.gumil.giphy.TestDispatcherRule
import dev.gumil.kaskade.flow.MutableEmitter
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
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
    fun actionLoadGif() {
        viewModel.restore(DetailState.Screen(giphy))
        val observer = mockk<Observer<DetailState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        val emitter = MutableEmitter<DetailAction>()
        viewModel.process(emitter)
        emitter.sendValue(DetailAction.GetRandomGif)

        verify(exactly = 2) { observer.onChanged(DetailState.Screen(giphy)) }

        confirmVerified(observer)
    }

    @Test
    fun actionOnError() {
        viewModel.restore(DetailState.Error(1))
        val observer = mockk<Observer<DetailState>>(relaxed = true)
        viewModel.state.observeForever(observer)

        val emitter = MutableEmitter<DetailAction>()
        viewModel.process(emitter)
        emitter.sendValue(DetailAction.OnError(Exception()))

        verify(exactly = 1) { observer.onChanged(DetailState.Error(1)) }
        verify(exactly = 1) { observer.onChanged(DetailState.Error(R.string.error_loading_single)) }
        confirmVerified(observer)
    }
}