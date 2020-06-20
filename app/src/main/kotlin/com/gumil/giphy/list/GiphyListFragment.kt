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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.databinding.FragmentListBinding
import com.gumil.giphy.detail.GiphyDetailFragment
import com.gumil.giphy.network.repository.Repository
import com.gumil.giphy.util.Cache
import com.gumil.giphy.util.FooterItem
import com.gumil.giphy.util.ItemAdapter
import com.gumil.giphy.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.gumil.kaskade.flow.MutableEmitter
import javax.inject.Inject

@AndroidEntryPoint
internal class GiphyListFragment : Fragment() {

    @Inject lateinit var repository: Repository
    @Inject lateinit var cache: Cache

    private val viewModel by viewModels<GiphyListViewModel>()

    private val giphyViewItem = GiphyViewItem()
    private val adapter = ItemAdapter(giphyViewItem).apply {
        footerItem = FooterItem(R.layout.item_progress)
    }

    private lateinit var actionEmitter: MutableEmitter<ListAction>

    private var pendingRestore: Parcelable? = null

    private var isLoading = true

    private lateinit var binding: FragmentListBinding

    private val swipeRefreshLayout by lazy { binding.swipeRefreshLayout }
    private val recyclerView by lazy { binding.recyclerView }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionEmitter = MutableEmitter()

        initializeViews()

        viewModel.state.observe(viewLifecycleOwner, Observer<ListState> { it?.render() })
        viewModel.process(actionEmitter)
        viewModel.restore(savedInstanceState?.getInt(ARG_LIMIT) ?: ListAction.DEFAULT_LIMIT)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        pendingRestore ?: run {
            pendingRestore = savedInstanceState?.getParcelable(ARG_RECYCLER_LAYOUT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_RECYCLER_LAYOUT, recyclerView.layoutManager?.onSaveInstanceState())
        outState.putInt(ARG_LIMIT, adapter.itemCount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionEmitter.unsubscribe()
        viewModel.state.removeObservers(this)
    }

    private fun initializeViews() {
        recyclerView.layoutManager = StaggeredGridLayoutManager(COLUMNS, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (pendingRestore == null && dy > 0 && !isLoading) {
                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val visibleItemCount = recyclerView.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItem = layoutManager.findFirstVisibleItemPositions(null).first()

                    totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD
                    actionEmitter.sendValue(ListAction.LoadMore(adapter.list.size))
                    isLoading = true
                }
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            actionEmitter.sendValue(ListAction.Refresh())
        }

        giphyViewItem.onItemClick = {
            actionEmitter.sendValue(ListAction.OnItemClick(it))
        }
    }

    private fun ListState.render(): Unit? = when (this) {
        is ListState.Screen -> {
            when(loadingMode) {
                ListState.Mode.REFRESH -> { swipeRefreshLayout.isRefreshing = true }
                ListState.Mode.LOAD_MORE -> adapter.showFooter()
                ListState.Mode.IDLE_LOAD_MORE -> {
                    adapter.addItems(giphies)
                    isLoading = false

                    restoreRecyclerView(giphies)
                }
                ListState.Mode.IDLE_REFRESH -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.list = giphies
                    isLoading = false

                    restoreRecyclerView(giphies)
                }
            }
        }
        is ListState.Error -> {
            swipeRefreshLayout.isRefreshing = false
            showSnackbar(errorMessage)
        }
        is ListState.GoToDetail -> view
            ?.findNavController()
            ?.navigate(R.id.action_giphyListFragment_to_giphyDetailFragment,
                GiphyDetailFragment.getBundle(giphy))
            ?.also {
                pendingRestore = recyclerView.layoutManager?.onSaveInstanceState()
            }
    }

    private fun restoreRecyclerView(giphies: List<GiphyItem>) {
        if (adapter.itemCount > 0 && giphies.isNotEmpty()) {
            pendingRestore?.let {
                recyclerView.post {
                    recyclerView.layoutManager?.onRestoreInstanceState(it)
                    (recyclerView.layoutManager as StaggeredGridLayoutManager).invalidateSpanAssignments()
                    pendingRestore = null
                }
            }
        }
    }

    companion object {
        private const val ARG_RECYCLER_LAYOUT = "arg_recycler_layout"
        private const val ARG_LIMIT = "arg_limit"
        private const val COLUMNS = 2
        private const val VISIBLE_THRESHOLD = 2
    }
}
