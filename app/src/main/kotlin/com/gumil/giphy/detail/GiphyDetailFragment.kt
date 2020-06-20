package com.gumil.giphy.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import coil.api.load
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.databinding.FragmentDetailBinding
import com.gumil.giphy.util.setHeight
import com.gumil.giphy.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.gumil.kaskade.flow.MutableEmitter

@AndroidEntryPoint
internal class GiphyDetailFragment : Fragment() {

    private val viewModel: GiphyDetailViewModel by viewModels()

    private lateinit var actionEmitter: MutableEmitter<DetailAction>

    private var currentState: DetailState.Screen? = null

    private lateinit var binding: FragmentDetailBinding

    private val getGifButton by lazy { binding.getGifButton }
    private val originalImage by lazy { binding.originalImage }
    private val userImage by lazy { binding.userImage }
    private val userContainer by lazy { binding.userContainer }
    private val username by lazy { binding.username }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionEmitter = MutableEmitter()

        (savedInstanceState ?: arguments)?.getParcelable<DetailState.Screen>(ARG_STATE)?.let {
            viewModel.restore(it)
        }

        getGifButton.setOnClickListener {
            actionEmitter.sendValue(DetailAction.GetRandomGif)
        }

        viewModel.state.observe(viewLifecycleOwner, Observer<DetailState> { it?.render() })

        viewModel.process(actionEmitter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentState?.let { outState.putParcelable(ARG_STATE, it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionEmitter.unsubscribe()
        viewModel.state.removeObservers(this)
    }

    private fun DetailState.render(): Unit? = when (this) {
        is DetailState.Screen -> {
            currentState = this
            activity?.title = giphy.title

            originalImage.post {
                val width = originalImage.width
                val height = width * giphy.image.height / giphy.image.width

                originalImage.setHeight(height)
                originalImage.load(giphy.image.original) {
                    placeholder(R.drawable.placeholder)
                    crossfade(true)
                }
            }

            val user = giphy.user
            userImage.scaleType = ImageView.ScaleType.CENTER_CROP
            userImage.load(user?.avatarUrl) {
                placeholder(R.drawable.placeholder)
                crossfade(true)
            }

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
