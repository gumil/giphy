package com.gumil.giphy.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.mapToItem
import com.gumil.giphy.network.repository.Repository
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
    private val repository: Repository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val kaskade by lazy { createKaskade() }

    private var initialAction: ListAction? = ListAction.Refresh

    val state: LiveData<ListState> get() = _state

    private val _state by lazy { kaskade.stateDamLiveData() }

    fun restore() {
        initialAction?.let { kaskade.process(it) }
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
                loadTrending(ListState.Mode.REFRESH, { 0 }) { _, list ->
                    ListState.Screen(list, ListState.Mode.IDLE_REFRESH)
                }
            }

            on<ListAction.LoadMore> {
                loadTrending(ListState.Mode.LOAD_MORE, { it.offset }) { state, list ->
                    ListState.Screen(
                        state.giphies.toMutableList().apply { addAll(list) },
                        ListState.Mode.IDLE_LOAD_MORE
                    )
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
        offsetFunction: (A) -> Int,
        listStateFunction: (ListState.Screen, List<GiphyItem>) -> ListState.Screen
    ): Observable<ListState> = this
        .map { offsetFunction(it.action) to it.state as ListState.Screen }
        .flatMap { actionState ->
            repository.getTrending(actionState.first)
                .map { giphies ->
                    val items = giphies.map { it.mapToItem() }
                    listStateFunction(actionState.second, items)
                }
                .startWith(actionState.second.copy(loadingMode = mode))
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
        fun creatModule() = module {
            viewModel { GiphyListViewModel(get()) }
        }
    }
}