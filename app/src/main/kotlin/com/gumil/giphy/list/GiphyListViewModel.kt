package com.gumil.giphy.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.mapToItem
import com.gumil.giphy.network.repository.Repository
import com.gumil.giphy.util.Cache
import dev.gumil.kaskade.ActionState
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.coroutines.coroutines
import dev.gumil.kaskade.flow.Emitter
import dev.gumil.kaskade.livedata.stateDamLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

internal class GiphyListViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val cache: Cache
) : ViewModel() {

    private val job = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val kaskade by lazy { createKaskade() }

    private var initialAction: ListAction.Refresh? = ListAction.Refresh()

    val state: LiveData<ListState> get() = _state

    private val _state by lazy { kaskade.stateDamLiveData() }

    fun restore(limit: Int = ListAction.DEFAULT_LIMIT) {
        initialAction?.let {
            kaskade.process(it.copy(limit = limit))
        }
        initialAction = null
    }

    fun process(actions: Emitter<ListAction>) {
        return actions.subscribe { kaskade.process(it) }
    }

    private fun createKaskade() = Kaskade.create<ListAction, ListState>(ListState.Screen()) {
        coroutines(uiScope) {
            onFlow<ListAction.Refresh> {
                flatMapConcat {
                    if (it.action.limit > ListAction.DEFAULT_LIMIT) {
                        cache.get<List<GiphyItem>>(KEY_GIPHIES)?.let { giphies ->
                            return@flatMapConcat flow {
                                emit(ListState.Screen(giphies, ListState.Mode.IDLE_REFRESH))
                            }
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
                state.copy(loadingMode = mode)
            }
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
        job.cancel()
    }

    companion object {
        private const val KEY_GIPHIES = "key giphies"
    }
}
