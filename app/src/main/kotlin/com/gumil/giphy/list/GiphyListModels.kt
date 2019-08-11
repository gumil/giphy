package com.gumil.giphy.list

import com.gumil.giphy.GiphyItem
import dev.gumil.kaskade.Action
import dev.gumil.kaskade.SingleEvent
import dev.gumil.kaskade.State

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

    data class Refresh(
        val limit: Int = DEFAULT_LIMIT
    ) : ListAction()

    data class LoadMore(
        val offset: Int
    ) : ListAction()

    data class OnItemClick(
        val item: GiphyItem
    ) : ListAction()

    data class OnError(
        val throwable: Throwable
    ) : ListAction()

    companion object {
        const val DEFAULT_LIMIT = 20
    }
}