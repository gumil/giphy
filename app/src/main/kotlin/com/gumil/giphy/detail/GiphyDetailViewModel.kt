package com.gumil.giphy.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gumil.giphy.R
import com.gumil.giphy.mapToItem
import com.gumil.giphy.network.repository.Repository
import com.gumil.giphy.util.applySchedulers
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

internal class GiphyDetailViewModel(
    private val repository: Repository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private lateinit var kaskade: Kaskade<DetailAction, DetailState>

    val state: LiveData<DetailState> get() = _state

    private val _state by lazy { kaskade.stateDamLiveData() }

    fun restore(detailState: DetailState) {
        if (::kaskade.isInitialized.not()) {
            kaskade = createKaskade(detailState)
        }
    }

    private fun createKaskade(detailState: DetailState) = Kaskade.create<DetailAction, DetailState>(detailState) {
        rx({
            object : DisposableObserver<DetailState>() {
                override fun onComplete() {
                    Timber.d("flow completed")
                }

                override fun onNext(state: DetailState) {
                    Timber.d("currentState = $state")
                }

                override fun onError(e: Throwable) {
                    Timber.e(e, "Flow was interrupted")
                    kaskade.process(DetailAction.OnError(e))
                }
            }.also { disposables.add(it) }
        }) {
            on<DetailAction.GetRandomGif> {
                flatMap { repository.getRandomGif() }
                    .map { DetailState.Screen(it.mapToItem()) }
                    .ofType(DetailState::class.java)
                    .applySchedulers()
            }
        }

        on<DetailAction.OnError> {
            DetailState.Error(R.string.error_loading_single)
        }
    }

    fun process(actions: Observable<DetailAction>): Disposable {
        return actions.subscribe { kaskade.process(it) }
    }

    override fun onCleared() {
        super.onCleared()
        kaskade.unsubscribe()
        disposables.clear()
    }

    companion object {
        fun creatModule() = module {
            viewModel { GiphyDetailViewModel(get()) }
        }
    }
}