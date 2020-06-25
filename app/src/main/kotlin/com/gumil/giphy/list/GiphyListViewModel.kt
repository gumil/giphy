package com.gumil.giphy.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.mapToItem
import com.gumil.giphy.network.repository.Repository
import com.gumil.giphy.util.Cache
import com.gumil.giphy.util.stateDamFlow
import com.gumil.giphy.util.stateFlow
import dev.gumil.kaskade.ActionState
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.coroutines.coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

internal class GiphyListViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val cache: Cache,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val kaskade by lazy { createKaskade() }

    val state by lazy { kaskade.stateDamFlow(ListState.Screen()) }

    init {
        val limit = savedStateHandle.get<Int>(KEY_LIMIT) ?: ListAction.DEFAULT_LIMIT
        kaskade.process(ListAction.Refresh(limit = limit))
    }

    fun process(actions: Flow<ListAction>): Flow<ListAction> {
        return actions.onEach { kaskade.process(it) }
    }

    private fun createKaskade() = Kaskade.create<ListAction, ListState>(ListState.Screen()) {
        coroutines(viewModelScope) {
            onFlow<ListAction.Refresh> {
                flatMapConcat {
                    if (it.action.limit > ListAction.DEFAULT_LIMIT) {
                        cache.get<List<GiphyItem>>(KEY_GIPHIES)?.let { giphies ->
                            return@flatMapConcat flowOf(ListState.Screen(giphies, ListState.Mode.IDLE_REFRESH))
                        }
                    }

                    loadTrending(ListState.Mode.REFRESH, limit = it.action.limit) { _, list ->
                        ListState.Screen(list, ListState.Mode.IDLE_REFRESH)
                    }
                }
            }

            onFlow<ListAction.LoadMore> {
                flatMapConcat {
                    loadTrending(ListState.Mode.LOAD_MORE, it.action.offset) { state, list ->
                        ListState.Screen(
                            state.giphies.toMutableList().apply { addAll(list) },
                            ListState.Mode.IDLE_LOAD_MORE
                        ).also { screen ->
                            cache.save(KEY_GIPHIES, screen.giphies)
                        }
                    }
                }
            }
        }

        on<ListAction.OnItemClick> {
            ListState.GoToDetail(action.item)
        }

        on<ListAction.OnError> {
            ListState.Error(R.string.error_loading)
        }
    }

    private suspend fun <A : ListAction> Flow<ActionState<A, ListState>>.loadTrending(
        mode: ListState.Mode,
        offset: Int = 0,
        limit: Int = 10,
        listStateFunction: (ListState.Screen, List<GiphyItem>) -> ListState.Screen
    ): Flow<ListState> { return this
        .map { it.currentState as ListState.Screen }
        .flatMapConcat { state ->
            flow {
                val items = repository.getTrending(offset, limit).map { it.mapToItem() }
                emit(listStateFunction(state, items))
            }.onStart {
                emit(state.copy(loadingMode = mode))
            }
        }
        .onCompletion {
            val currentLimit = savedStateHandle.get<Int>(KEY_LIMIT) ?: 0
            savedStateHandle.set(KEY_LIMIT, currentLimit + limit)
        }
        .catch<ListState> {
            Timber.e(it, "Error loading gifs")
            emit(ListState.Error(R.string.error_loading))
        }
        .flowOn(Dispatchers.IO)
    }

    override fun onCleared() {
        super.onCleared()
        kaskade.unsubscribe()
    }

    companion object {
        private const val KEY_GIPHIES = "key giphies"
        private const val KEY_LIMIT = "key limit"
    }
}
