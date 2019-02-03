package com.gumil.giphy.util

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

internal fun Fragment.showSnackbar(stringRes: Int) {
    view?.let { Snackbar.make(it, stringRes, Snackbar.LENGTH_SHORT).show() }
}