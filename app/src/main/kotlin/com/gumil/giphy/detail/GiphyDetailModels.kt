package com.gumil.giphy.detail

import com.gumil.giphy.GiphyItem
import io.gumil.kaskade.Action
import io.gumil.kaskade.State

internal sealed class DetailState : State {
    data class Screen(
        val giphy: GiphyItem
    ) : DetailState()

    data class Error(
        val message: Int
    ) : DetailState()
}

internal sealed class DetailAction : Action {
    object GetRandomGif : DetailAction()

    data class OnError(
        val throwable: Throwable
    ) : DetailAction()
}