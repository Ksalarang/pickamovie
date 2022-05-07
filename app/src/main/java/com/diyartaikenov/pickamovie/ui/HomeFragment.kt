package com.diyartaikenov.pickamovie.ui

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.FragmentHomeBinding
import com.diyartaikenov.pickamovie.network.QueryParams
import com.diyartaikenov.pickamovie.network.SortBy
import com.diyartaikenov.pickamovie.ui.adapter.MovieListAdapter
import com.diyartaikenov.pickamovie.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var adapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        })

        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
            retryButton.setOnClickListener { adapter.retry() }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadStates ->
                val isListEmpty = loadStates.refresh is LoadState.NotLoading
                        && adapter.itemCount == 0

                binding.apply {
                    tvNoResults.isVisible = isListEmpty
                    recyclerView.isVisible = !isListEmpty
                    progressBar.isVisible = loadStates.source.refresh is LoadState.Loading
                    retryButton.isVisible = loadStates.refresh is LoadState.Error
                    tvNoInternet.isVisible = loadStates.refresh is LoadState.Error
                }
            }
        }

        viewModel.movies.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)

        val itemId = when (viewModel.queryParams.sortBy) {
            SortBy.POPULARITY_DESC -> R.id.sort_by_popularity_desc
            SortBy.VOTE_AVERAGE_DESC -> R.id.sort_by_rating_desc
            SortBy.RELEASE_DATE_DESC -> R.id.sort_by_date_desc
            SortBy.RELEASE_DATE_ASC -> R.id.sort_by_date_asc
        }
        menu.findItem(itemId).isChecked = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_popularity_desc -> {
                viewModel.getMoviesWithQuery(
                    QueryParams(
                        SortBy.POPULARITY_DESC
                    )
                )
                item.isChecked = true
            }
            R.id.sort_by_rating_desc -> {
                viewModel.getMoviesWithQuery(
                    QueryParams(
                        SortBy.VOTE_AVERAGE_DESC,
                    )
                )
                item.isChecked = true
            }
            R.id.sort_by_date_desc -> {
                viewModel.getMoviesWithQuery(
                    QueryParams(
                        SortBy.RELEASE_DATE_DESC,
                    )
                )
                item.isChecked = true
            }
            R.id.sort_by_date_asc -> {
                viewModel.getMoviesWithQuery(
                    QueryParams(
                        SortBy.RELEASE_DATE_ASC
                    )
                )
                item.isChecked = true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}