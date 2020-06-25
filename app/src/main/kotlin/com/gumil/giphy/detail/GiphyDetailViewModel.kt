package com.gumil.giphy.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.ImageItem
import com.gumil.giphy.R
import com.gumil.giphy.mapToItem
import com.gumil.giphy.network.repository.Repository
import com.gumil.giphy.util.stateDamFlow
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.coroutines.coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class GiphyDetailViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private lateinit var kaskade: Kaskade<DetailAction, DetailState>

    val state by lazy {
        kaskade.stateDamFlow(DetailState.Screen(GiphyItem(
            title = "title",
            user = null,
            image = ImageItem(
                "", "", 1, 1
            )
        )))
    }

    init {
        val state = savedStateHandle.get<DetailState.Screen>(KEY_STATE)
        if (state != null) {
            restore(state)
        }
    }

    fun restore(detailState: DetailState) {
        if (::kaskade.isInitialized.not()) {
            kaskade = createKaskade(detailState)
        }
    }

    private fun createKaskade(detailState: DetailState) = Kaskade.create<DetailAction, DetailState>(detailState) {
        coroutines(viewModelScope) {
            onFlow<DetailAction.GetRandomGif> {
                map {
                    repository.getRandomGif()
                }.map {
                    DetailState.Screen(it.mapToItem())
                }.onEach {
                    savedStateHandle.set(KEY_STATE, it)
                }.flowOn(Dispatchers.IO)
            }
        }

        on<DetailAction.OnError> {
            DetailState.Error(R.string.error_loading_single)
        }
    }

    fun process(actions: Flow<DetailAction>): Flow<DetailAction> {
        return actions.onEach { kaskade.process(it) }
    }

    override fun onCleared() {
        super.onCleared()
        kaskade.unsubscribe()
    }

    companion object {
        private const val KEY_STATE = "key state"
    }
}
