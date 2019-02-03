package com.gumil.giphy.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gumil.giphy.R
import com.gumil.giphy.util.FooterItem
import com.gumil.giphy.util.ItemAdapter
import com.gumil.giphy.util.showSnackbar
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.android.viewmodel.ext.viewModel

internal class GiphyListFragment : Fragment() {

    private val viewModel: GiphyListViewModel by viewModel()

    private val giphyViewItem = GiphyViewItem()
    private val adapter = ItemAdapter(giphyViewItem).apply {
        footerItem = FooterItem(R.layout.item_progress)
    }

    private val actionSubject = PublishSubject.create<ListAction>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = StaggeredGridLayoutManager(COLUMNS, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        viewModel.state.observe(this, Observer<ListState> { it?.render() })

        viewModel.process(actions())

        savedInstanceState ?: actionSubject.onNext(ListAction.Refresh)
    }

    private fun actions() = actionSubject

    private fun ListState.render(): Unit = when (this) {
        is ListState.Screen -> {
            when(loadingMode) {
                ListState.Mode.REFRESH -> { swipeRefreshLayout.isRefreshing = true }
                ListState.Mode.LOAD_MORE -> adapter.showFooter()
                ListState.Mode.IDLE -> { swipeRefreshLayout.isRefreshing = false }
            }
            adapter.list = giphies
        }
        is ListState.Error -> {
            swipeRefreshLayout.isRefreshing = false
            showSnackbar(errorMessage)
        }
        is ListState.GoToDetail -> TODO()
    }

    companion object {
        private const val COLUMNS = 2
    }
}