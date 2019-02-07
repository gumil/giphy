package com.gumil.giphy.util

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.request.RequestOptions

@SuppressLint("CheckResult")
internal fun ImageView.load(
    url: String?,
    requestOptions: (RequestOptions.() -> RequestOptions)? = null,
    transitionOptions: TransitionOptions<*, Drawable>? = null
) {
    url?.let { imageUrl ->
        visibility = View.VISIBLE
        Glide.with(context)
            .load(Uri.parse(imageUrl))
            .apply {
                requestOptions?.invoke(RequestOptions())?.let {
                    apply(it)
                }

                transitionOptions?.let { transition(it) }
            }
            .into(this)
    } ?: let { visibility = View.GONE }
}

@SuppressLint("CheckResult")
internal fun ImageView.load(
    url: String?,
    requestOptions: (RequestOptions.() -> RequestOptions)? = null
) {
    load(url, requestOptions, null)
}

internal fun ImageView.setHeight(height: Int) {
    layoutParams = layoutParams.apply {
        this.height = height
    }
}