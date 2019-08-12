package com.gumil.giphy.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gumil.giphy.R
import com.gumil.giphy.mapToItem
import com.gumil.giphy.network.repository.Repository
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.coroutines.coroutines
import dev.gumil.kaskade.flow.Emitter
import dev.gumil.kaskade.livedata.stateDamLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal class GiphyDetailViewModel(
    private val repository: Repository,
    scope: CoroutineScope? = null
) : ViewModel() {

    private val job = scope?.coroutineContext?.get(Job) ?: Job()

    private val uiScope = scope ?: CoroutineScope(Dispatchers.Main + job)

    private lateinit var kaskade: Kaskade<DetailAction, DetailState>

    val state: LiveData<DetailState> get() = _state

    private val _state by lazy { kaskade.stateDamLiveData() }

    fun restore(detailState: DetailState) {
        if (::kaskade.isInitialized.not()) {
            kaskade = createKaskade(detailState)
        }
    }

    private fun createKaskade(detailState: DetailState) = Kaskade.create<DetailAction, DetailState>(detailState) {
        coroutines(uiScope) {
            onFlow<DetailAction.GetRandomGif> {
                map {
                    repository.getRandomGif()
                }.map {
                    DetailState.Screen(it.mapToItem())
                }.flowOn(Dispatchers.IO)
            }
        }

        on<DetailAction.OnError> {
            DetailState.Error(R.string.error_loading_single)
        }
    }

    fun process(actions: Emitter<DetailAction>) {
        return actions.subscribe { kaskade.process(it) }
    }

    override fun onCleared() {
        super.onCleared()
        kaskade.unsubscribe()
        job.cancel()
    }

    companion object {
        fun creatModule() = module {
            viewModel { GiphyDetailViewModel(get()) }
        }
    }
}