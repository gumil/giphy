package com.gumil.giphy.detail

import android.os.Parcelable
import com.gumil.giphy.GiphyItem
import dev.gumil.kaskade.Action
import dev.gumil.kaskade.SingleEvent
import dev.gumil.kaskade.State
import kotlinx.android.parcel.Parcelize

internal sealed class DetailState : State {

    @Parcelize
    data class Screen(
        val giphy: GiphyItem
    ) : DetailState(), Parcelable

    data class Error(
        val message: Int
    ) : DetailState(), SingleEvent
}

internal sealed class DetailAction : Action {
    object GetRandomGif : DetailAction()

    data class OnError(
        val throwable: Throwable
    ) : DetailAction()
}