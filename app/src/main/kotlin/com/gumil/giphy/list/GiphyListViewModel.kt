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

    val state: LiveData<ListState> get() = _state

    private val _state by lazy { kaskade.stateDamLiveData() }

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
                loadTrending(ListState.Mode.REFRESH)
            }

            on<ListAction.LoadMore> {
                loadTrending(ListState.Mode.LOAD_MORE)
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
        mode: ListState.Mode
    ): Observable<ListState> = this
        .map {
            val action = it.action
            val offset = when (action) {
                is ListAction.LoadMore -> action.offset
                is ListAction.Refresh -> 0
                else -> throw IllegalStateException("Invalid state")
            }
            offset to it.state as ListState.Screen
        }
        .flatMap { actionState ->
            repository.getTrending(actionState.first)
                .map { giphies ->
                    val items = giphies.map { it.mapToItem() }
                    val (list, modeIdle) = if (mode == ListState.Mode.LOAD_MORE) {
                        mutableListOf<GiphyItem>().apply {
                            addAll(actionState.second.giphies)
                            addAll(items)
                        } to ListState.Mode.IDLE_LOAD_MORE
                    } else {
                        items to ListState.Mode.IDLE_REFRESH
                    }
                    ListState.Screen(list, modeIdle)
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