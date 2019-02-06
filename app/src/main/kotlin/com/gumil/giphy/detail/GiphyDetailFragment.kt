package com.gumil.giphy.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R

internal class GiphyDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    companion object {
        private const val ARG_GIPHY = "arg_giphy"

        fun getBundle(giphyItem: GiphyItem): Bundle = Bundle().apply {
            putParcelable(ARG_GIPHY, giphyItem)
        }

        fun getGiphyItem(bundle: Bundle?) = bundle?.getParcelable<GiphyItem>(ARG_GIPHY)
    }
}