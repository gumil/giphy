package com.gumil.giphy.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import timber.log.Timber

internal class GiphyDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag("tantrums").d("giphy = ${arguments?.getParcelable<GiphyItem>(ARG_GIPHY)}")
    }

    companion object {
        private const val ARG_GIPHY = "arg_giphy"

        fun getBundle(giphyItem: GiphyItem): Bundle = Bundle().apply {
            putParcelable(ARG_GIPHY, giphyItem)
        }
    }
}