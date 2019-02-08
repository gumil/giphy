package com.gumil.giphy.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

internal class ItemAdapter<M>(
        private val defaultItem: ViewItem<M>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var footerItem: ViewItem<*>? = null

    private var _footerItem: ViewItem<*>? = null

    var list: List<M>
        get() = _list
        set(value) {
            _list = value.toMutableList()
            notifyDataSetChanged()
        }

    private var _list: MutableList<M> = mutableListOf()

    private val currentListSize get() = _list.size

    var onPrefetch: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            object : RecyclerView.ViewHolder(parent.inflateLayout(viewType)) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < _list.size) {
            defaultItem.bind(holder.itemView, _list[position])
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
        notifyItemInserted(currentListSize)
    }

    fun addItems(items: List<M>) {
        _footerItem = null

        if (items.size == _list.size) return

        _list.addAll(items.minus(_list))
        notifyItemRangeChanged(currentListSize - 1, currentListSize + 1)
        notifyItemRangeInserted(currentListSize + 1, currentListSize + items.size)
    }
}

internal fun ViewGroup.inflateLayout(@LayoutRes layout: Int, addToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(layout, this, addToRoot)