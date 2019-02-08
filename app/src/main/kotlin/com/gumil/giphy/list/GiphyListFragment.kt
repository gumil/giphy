package com.gumil.giphy.list

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gumil.giphy.GiphyItem
import com.gumil.giphy.R
import com.gumil.giphy.detail.GiphyDetailFragment
import com.gumil.giphy.util.FooterItem
import com.gumil.giphy.util.ItemAdapter
import com.gumil.giphy.util.itemClick
import com.gumil.giphy.util.showSnackbar
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.android.viewmodel.ext.viewModel

internal class GiphyListFragment : Fragment() {

    private val viewModel: GiphyListViewModel by viewModel()

    private val giphyViewItem = GiphyViewItem()
    private val adapter = ItemAdapter(giphyViewItem).apply {
        footerItem = FooterItem(R.layout.item_progress)
    }

    private lateinit var compositeDisposable: CompositeDisposable

    private var pendingRestore: Parcelable? = null

    private var isLoading = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable = CompositeDisposable()

        recyclerView.layoutManager = StaggeredGridLayoutManager(COLUMNS, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        viewModel.state.observe(this, Observer<ListState> { it?.render() })

        compositeDisposable.add(viewModel.process(actions()))

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
        compositeDisposable.dispose()
        viewModel.state.removeObservers(this)
    }

    private fun actions() = Observable.merge<ListAction>(
        recyclerView.scrollEvents()
            .filter { pendingRestore == null }
            .filter { it.dy > 0 }
            .filter { !isLoading }
            .filter {
                val layoutManager = it.view.layoutManager as StaggeredGridLayoutManager
                val visibleItemCount = recyclerView.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPositions(null).first()

                totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD
            }
            .map { ListAction.LoadMore(adapter.list.size) }
            .doOnNext { isLoading = true },
        swipeRefreshLayout.refreshes().map { ListAction.Refresh() },
        giphyViewItem.itemClick().map { ListAction.OnItemClick(it) }
    )

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