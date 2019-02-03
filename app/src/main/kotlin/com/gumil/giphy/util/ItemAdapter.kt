package com.gumil.giphy.util

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

internal class ItemAdapter<M>(
        private val defaultItem: ViewItem<M>,
        private val prefetchDistance: Int = 2
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var footerItem: ViewItem<*>? = null

    private var _footerItem: ViewItem<*>? = null

    var list: List<M>
        get() = _list
        set(value) {
            _list = value.toMutableList()
            currentListSize = 0
            notifyDataSetChanged()
        }

    private var _list: MutableList<M> = mutableListOf()

    private var currentListSize = 0

    var onPrefetch: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            object : RecyclerView.ViewHolder(parent.inflateLayout(viewType)) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < _list.size) {
            defaultItem.bind(holder.itemView, _list[position])
        }

        if (_list.size > currentListSize && position == (_list.size - prefetchDistance)) {
            currentListSize = _list.size
            Handler().post {
                onPrefetch?.invoke()
            }
        }
    }

    override fun getItemCount(): Int = _list.size + (_footerItem?.let { 1 } ?: 0)

    override fun getItemViewType(position: Int): Int {
        return if (position == _list.size) {
            _footerItem?.layout ?: 0
        } else {
            defaultItem.layout
        }
    }

    fun showFooter() {
        _footerItem = footerItem
        notifyItemInserted(currentListSize + 1)
    }

    fun addItems(items: List<M>) {
        _footerItem = null
        _list.addAll(items.minus(_list))
        notifyItemChanged(currentListSize)
        notifyItemRangeInserted(currentListSize + 1, currentListSize + items.size)
    }
}

internal class OnPrefetchObservable(
        private val adapter: ItemAdapter<*>
) : Observable<Unit>() {

    override fun subscribeActual(observer: Observer<in Unit>?) {
        observer?.let {
            adapter.onPrefetch = Listener(adapter, it)
        }
    }

    internal class Listener(
            private val adapter: ItemAdapter<*>,
            private val observer: Observer<in Unit>
    ) : MainThreadDisposable(), Function0<Unit> {

        override fun invoke() {
            if (!isDisposed) {
                observer.onNext(Unit)
            }
        }

        override fun onDispose() {
            adapter.onPrefetch = null
        }
    }
}

internal fun <M> ItemAdapter<M>.prefetch() = OnPrefetchObservable(this)

internal fun ViewGroup.inflateLayout(@LayoutRes layout: Int, addToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(layout, this, addToRoot)