package com.gumil.giphy.detail

import android.os.Parcelable
import com.gumil.giphy.GiphyItem
import io.gumil.kaskade.Action
import io.gumil.kaskade.SingleEvent
import io.gumil.kaskade.State
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