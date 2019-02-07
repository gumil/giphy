package com.gumil.giphy.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.util.load
import com.gumil.giphy.util.setHeight
import com.gumil.giphy.util.showSnackbar
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.android.viewmodel.ext.viewModel

internal class GiphyDetailFragment : Fragment() {

    private val viewModel: GiphyDetailViewModel by viewModel()

    private lateinit var compositeDisposable: CompositeDisposable

    private var currentState: DetailState.Screen? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable = CompositeDisposable()

        (savedInstanceState ?: arguments)?.getParcelable<DetailState.Screen>(ARG_STATE)?.let {
            viewModel.restore(it)
        }

        viewModel.state.observe(this, Observer<DetailState> { it?.render() })

        compositeDisposable.add(viewModel.process(actions()))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentState?.let { outState.putParcelable(ARG_STATE, it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
        viewModel.state.removeObservers(this)
    }

    private fun actions(): Observable<DetailAction> =
        getGifButton.clicks().map { DetailAction.GetRandomGif }

    private fun DetailState.render(): Unit? = when (this) {
        is DetailState.Screen -> {
            currentState = this
            activity?.title = giphy.title

            originalImage.post {
                val width = originalImage.width
                val height = width * giphy.image.height / giphy.image.width

                originalImage.setHeight(height)
                originalImage.load(giphy.image.original, {
                    placeholder(R.drawable.placeholder).override(width, height)
                }, DrawableTransitionOptions.withCrossFade())
            }

            val user = giphy.user
            userImage.load(user?.avatarUrl, {
                centerCrop().placeholder(R.drawable.placeholder)
            }, DrawableTransitionOptions.withCrossFade())

            userContainer.setVisible(user != null)
            username.setVisible(user?.displayName != null &&
                user.displayName.trim().isBlank().not())
            username.text = user?.displayName
        }
        is DetailState.Error -> showSnackbar(message)
    }

    private fun View.setVisible(visible: Boolean) {
        visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    companion object {
        private const val ARG_STATE = "arg_state"

        fun getBundle(giphyItem: GiphyItem): Bundle = Bundle().apply {
            putParcelable(ARG_STATE, DetailState.Screen(giphyItem))
        }
    }
}