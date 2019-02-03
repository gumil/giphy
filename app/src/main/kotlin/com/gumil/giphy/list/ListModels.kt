package com.gumil.giphy.list

import com.gumil.giphy.GiphyItem
import io.gumil.kaskade.Action
import io.gumil.kaskade.SingleEvent
import io.gumil.kaskade.State

internal sealed class ListState : State {

    data class Screen(
        val giphies: List<GiphyItem> = emptyList(),
        val loadingMode: Mode = Mode.IDLE_REFRESH
    ) : ListState()

    data class Error(
        val errorMessage: Int
    ) : ListState(), SingleEvent

    data class GoToDetail(
        val giphy: GiphyItem
    ) : ListState(), SingleEvent

    enum class Mode {
        REFRESH, LOAD_MORE, IDLE_REFRESH, IDLE_LOAD_MORE
    }
}

internal sealed class ListAction : Action {

    object Refresh : ListAction()

    data class LoadMore(
        val offset: Int
    ) : ListAction()

    data class OnItemClick(
        val item: GiphyItem
    ) : ListAction()

    data class OnError(
        val throwable: Throwable
    ) : ListAction()
}