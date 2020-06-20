package com.gumil.giphy.util

import android.widget.ImageView

internal fun ImageView.setHeight(height: Int) {
    layoutParams = layoutParams.apply {
        this.height = height
    }
}