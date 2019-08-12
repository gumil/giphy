package com.gumil.giphy.util

import android.view.View

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

    override var onItemClick: ((Nothing)  -> Unit)? = null

    override fun bind(view: View, item: Nothing) {
        //no bindings
    }
}
