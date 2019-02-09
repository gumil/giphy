package com.gumil.giphy.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.mapToItem
import com.gumil.giphy.network.repository.Repository
import com.gumil.giphy.util.Cache
import com.gumil.giphy.util.applySchedulers
import com.gumil.giphy.util.just
import io.gumil.kaskade.ActionState
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.livedata.stateDamLiveData
import io.gumil.kaskade.rx.rx
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

internal class GiphyListViewModel(
    private val repository: Repository,
    private val cache: Cache
) : ViewModel() {

    private val disposables = CompositeDisposable()

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

    fun process(actions: Observable<ListAction>): Disposable {
        return actions.subscribe { kaskade.process(it) }
    }

    private fun createKaskade() = Kaskade.create<ListAction, ListState>(ListState.Screen()) {
        rx({
            object : DisposableObserver<ListState>() {
                override fun onComplete() {
                    Timber.d("flow completed")
                }

                override fun onNext(state: ListState) {
                    Timber.d("currentState = $state")
                }

                override fun onError(e: Throwable) {
                    Timber.e(e, "Flow was interrupted")
                    process(ListAction.OnError(e).just())
                }
            }.also { disposables.add(it) }
        }) {
            on<ListAction.Refresh> {
                flatMap {
                    if (it.action.limit > ListAction.DEFAULT_LIMIT) {
                        cache.get<List<GiphyItem>>(KEY_GIPHIES)?.let { giphies ->
                            return@flatMap ListState.Screen(giphies, ListState.Mode.IDLE_REFRESH).just()
                        }
                    }

                    loadTrending(ListState.Mode.REFRESH, limit = it.action.limit) { _, list ->
                        ListState.Screen(list, ListState.Mode.IDLE_REFRESH)
                    }
                }
            }

            on<ListAction.LoadMore> {
                flatMap {
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

    private fun <A : ListAction> Observable<ActionState<A, ListState>>.loadTrending(
        mode: ListState.Mode,
        offset: Int = 0,
        limit: Int = 10,
        listStateFunction: (ListState.Screen, List<GiphyItem>) -> ListState.Screen
    ): Observable<ListState> = this
        .map { it.state as ListState.Screen }
        .flatMap { state ->
            repository.getTrending(offset, limit)
                .map { giphies ->
                    val items = giphies.map { it.mapToItem() }
                    listStateFunction(state, items)
                }
                .startWith(state.copy(loadingMode = mode))
        }
        .ofType(ListState::class.java)
        .onErrorReturn {
            Timber.e(it, "Error loading gifs")
            ListState.Error(R.string.error_loading)
        }
        .applySchedulers()

    override fun onCleared() {
        super.onCleared()
        kaskade.unsubscribe()
        disposables.clear()
    }

    companion object {
        private const val KEY_GIPHIES = "key giphies"

        fun createModule() = module {
            viewModel { GiphyListViewModel(get(), get()) }
        }
    }
}