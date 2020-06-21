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

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionEmitter = MutableEmitter()

        arguments?.getParcelable<DetailState.Screen>(ARG_STATE)?.let {
            viewModel.restore(it)
        }

        binding.getGifButton.setOnClickListener {
            actionEmitter.sendValue(DetailAction.GetRandomGif)
        }

        viewModel.state.observe(viewLifecycleOwner, Observer<DetailState> { it?.render() })

        viewModel.process(actionEmitter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionEmitter.unsubscribe()
        viewModel.state.removeObservers(this)
        _binding = null
    }

    private fun DetailState.render(): Unit? = when (this) {
        is DetailState.Screen -> {
            currentState = this
            activity?.title = giphy.title

            binding.originalImage.post {
                val width = binding.originalImage.width
                val height = width * giphy.image.height / giphy.image.width

                binding.originalImage.setHeight(height)
                binding.originalImage.load(giphy.image.original) {
                    placeholder(R.drawable.placeholder)
                    crossfade(true)
                }
            }

            val user = giphy.user
            binding.userImage.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.userImage.load(user?.avatarUrl) {
                placeholder(R.drawable.placeholder)
                crossfade(true)
            }

            binding.userContainer.setVisible(user != null)
            binding.username.setVisible(user?.displayName != null &&
                user.displayName.trim().isBlank().not())
            binding.username.text = user?.displayName
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
