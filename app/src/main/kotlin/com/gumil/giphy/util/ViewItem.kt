package com.gumil.giphy.util

import android.view.View
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import reactivecircus.flowbinding.common.checkMainThread
import reactivecircus.flowbinding.common.safeOffer
import timber.log.Timber

internal interface ViewItem<M> {

    var onItemClick: ((M) -> Unit)?

    val layout: Int

    fun bind(view: View, item: M) {
        view.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }
}

internal data class FooterItem(
        override val layout: Int
) : ViewItem<Nothing> {

    override var onItemClick: ((Nothing) -> Unit)? = null

    override fun bind(view: View, item: Nothing) {
        //no bindings
    }
}

internal fun <M> ViewItem<M>.itemClick() = callbackFlow<M> {
    checkMainThread()
    onItemClick = {
        safeOffer(it)
    }
    awaitClose {
        Timber.tag("tantrums").d("nasara aman")
        onItemClick = null
    }
}.conflate()
