package com.gumil.giphy.list

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.databinding.FragmentListBinding
import com.gumil.giphy.detail.GiphyDetailFragment
import com.gumil.giphy.util.FooterItem
import com.gumil.giphy.util.ItemAdapter
import com.gumil.giphy.util.itemClick
import com.gumil.giphy.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.recyclerview.scrollEvents
import reactivecircus.flowbinding.swiperefreshlayout.refreshes

@AndroidEntryPoint
internal class GiphyListFragment : Fragment() {

    private val viewModel by viewModels<GiphyListViewModel>()

    private val giphyViewItem = GiphyViewItem()
    private val adapter = ItemAdapter(giphyViewItem).apply {
        footerItem = FooterItem(R.layout.item_progress)
    }

    private var pendingRestore: Parcelable? = null

    private var isLoading = true

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var job: Job
    private val uiScope: CoroutineScope get() = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()

        job = Job()

        viewModel.state.observe(viewLifecycleOwner, Observer<ListState> { it?.render() })
        viewModel.process(actions()).launchIn(uiScope)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        pendingRestore ?: run {
            pendingRestore = savedInstanceState?.getParcelable(ARG_RECYCLER_LAYOUT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_RECYCLER_LAYOUT, _binding?.recyclerView?.layoutManager?.onSaveInstanceState())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
        viewModel.state.removeObservers(this)
        binding.recyclerView.adapter = null
        _binding = null
    }

    private fun initializeViews() {
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(COLUMNS, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.adapter = adapter
    }

    private fun actions() = flowOf(
        binding.recyclerView.scrollEvents()
            .filter { pendingRestore == null }
            .filter { it.dy > 0 }
            .filter { !isLoading }
            .filter {
                val layoutManager = it.view.layoutManager as StaggeredGridLayoutManager
                val visibleItemCount = binding.recyclerView.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPositions(null).first()

                totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD
            }
            .map { ListAction.LoadMore(adapter.list.size) }
            .onEach { isLoading = true },
        binding.swipeRefreshLayout.refreshes().map { ListAction.Refresh() },
        giphyViewItem.itemClick().map { ListAction.OnItemClick(it) }
    ).flattenMerge()

    private fun ListState.render(): Unit? = when (this) {
        is ListState.Screen -> {
            when(loadingMode) {
                ListState.Mode.REFRESH -> { binding.swipeRefreshLayout.isRefreshing = true }
                ListState.Mode.LOAD_MORE -> adapter.showFooter()
                ListState.Mode.IDLE_LOAD_MORE -> {
                    adapter.addItems(giphies)
                    isLoading = false

                    restoreRecyclerView(giphies)
                }
                ListState.Mode.IDLE_REFRESH -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    adapter.list = giphies
                    isLoading = false

                    restoreRecyclerView(giphies)
                }
            }
        }
        is ListState.Error -> {
            binding.swipeRefreshLayout.isRefreshing = false
            showSnackbar(errorMessage)
        }
        is ListState.GoToDetail -> view
            ?.findNavController()
            ?.navigate(R.id.action_giphyListFragment_to_giphyDetailFragment,
                GiphyDetailFragment.getBundle(giphy))
            ?.also {
                pendingRestore = binding.recyclerView.layoutManager?.onSaveInstanceState()
            }
    }

    private fun restoreRecyclerView(giphies: List<GiphyItem>) {
        if (adapter.itemCount > 0 && giphies.isNotEmpty()) {
            pendingRestore?.let {
                binding.recyclerView.post {
                    binding.recyclerView.layoutManager?.onRestoreInstanceState(it)
                    (binding.recyclerView.layoutManager as StaggeredGridLayoutManager).invalidateSpanAssignments()
                    pendingRestore = null
                }
            }
        }
    }

    companion object {
        private const val ARG_RECYCLER_LAYOUT = "arg_recycler_layout"
        private const val COLUMNS = 2
        private const val VISIBLE_THRESHOLD = 2
    }
}
