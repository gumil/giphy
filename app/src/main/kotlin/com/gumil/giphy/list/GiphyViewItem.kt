package com.gumil.giphy.list

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import coil.api.load
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.util.ViewItem
import com.gumil.giphy.util.setHeight

internal class GiphyViewItem : ViewItem<GiphyItem> {
    override var onItemClick: ((GiphyItem) -> Unit)? = null

    override val layout: Int = R.layout.item_giphy

    @SuppressLint("CheckResult")
    override fun bind(view: View, item: GiphyItem) {
        super.bind(view, item)
        (view as? ImageView)?.let { imageView ->
            imageView.post {
                imageView.setHeight(item.image.height)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP

                imageView.load(item.image.resized) {
                    placeholder(R.drawable.placeholder)
                }
            }
        }
    }
}
