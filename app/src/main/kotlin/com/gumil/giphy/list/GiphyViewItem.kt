package com.gumil.giphy.list

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.util.ViewItem
import com.gumil.giphy.util.load
import com.gumil.giphy.util.setHeight

internal class GiphyViewItem : ViewItem<GiphyItem> {
    override var onItemClick: ((GiphyItem) -> Unit)? = null

    override val layout: Int = R.layout.item_giphy

    @SuppressLint("CheckResult")
    override fun bind(view: View, item: GiphyItem) {
        super.bind(view, item)
        (view as? ImageView)?.let {
            it.post {
                it.setHeight(item.image.height)

                it.load(item.image.resized) {
                    centerCrop().placeholder(R.drawable.placeholder)
                            .override(it.width, item.image.height)
                }
            }
        }
    }
}