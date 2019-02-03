package com.gumil.giphy.util

import android.view.View
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

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

internal class OnItemClickObservable<T>(
        private val viewItem: ViewItem<T>
): Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>?) {
        observer?.let {
            viewItem.onItemClick = Listener(viewItem, it)
        }

    }

    internal class Listener<in T>(
            private val viewItem: ViewItem<T>,
            private val observer: Observer<in T>
    ) : MainThreadDisposable(), Function1<T, Unit> {

        override fun invoke(p1: T) {
            if (!isDisposed) {
                observer.onNext(p1)
            }
        }

        override fun onDispose() {
            viewItem.onItemClick = null
        }
    }
}

internal fun <M> ViewItem<M>.itemClick() = OnItemClickObservable(this)