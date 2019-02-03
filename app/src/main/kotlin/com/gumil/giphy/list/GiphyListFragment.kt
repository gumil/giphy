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
import com.gumil.giphy.util.prefetch
import com.gumil.giphy.util.showSnackbar
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
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

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = StaggeredGridLayoutManager(COLUMNS, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        viewModel.state.observe(this, Observer<ListState> { it?.render() })

        compositeDisposable.add(viewModel.process(actions()))

        savedInstanceState ?: actionSubject.onNext(ListAction.Refresh)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    private fun actions() = Observable.merge<ListAction>(
        actionSubject,
        adapter.prefetch()
            .map { adapter.list.size }
            .map { ListAction.LoadMore(it) },
        swipeRefreshLayout.refreshes().map { ListAction.Refresh }
    )

    private fun ListState.render(): Unit = when (this) {
        is ListState.Screen -> {
            when(loadingMode) {
                ListState.Mode.REFRESH -> { swipeRefreshLayout.isRefreshing = true }
                ListState.Mode.LOAD_MORE -> adapter.showFooter()
                ListState.Mode.IDLE_LOAD_MORE -> { adapter.addItems(giphies) }
                ListState.Mode.IDLE_REFRESH -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.list = giphies
                }
            }
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