package com.diyartaikenov.pickamovie.ui.homeviewpager.movies

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.FragmentMoviesBinding
import com.diyartaikenov.pickamovie.repository.network.QueryParams
import com.diyartaikenov.pickamovie.repository.network.SortBy
import com.diyartaikenov.pickamovie.ui.MainActivity
import com.diyartaikenov.pickamovie.ui.adapter.MovieListAdapter
import com.diyartaikenov.pickamovie.ui.homeviewpager.HomeViewPagerFragmentDirections
import com.diyartaikenov.pickamovie.ui.homeviewpager.loadstateadapter.MoviesLoadStateAdapter
import com.diyartaikenov.pickamovie.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    private val moviesViewModel: MoviesViewModel by viewModels()
    private var adapter: MovieListAdapter? = null
    private lateinit var movieFiltersDialogFragment: MovieFiltersDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (adapter == null) {
            adapter = MovieListAdapter(moviesViewModel) { movie ->
                findNavController().navigate(
                    HomeViewPagerFragmentDirections.actionNavHomeToNavMovieDetails(movie.id)
                )
            }
        }

        binding.apply {
            val errorMessage = getString(R.string.no_internet)
            recyclerView.adapter = adapter!!.withLoadStateHeaderAndFooter(
                header = MoviesLoadStateAdapter(errorMessage) { adapter!!.retry() },
                footer = MoviesLoadStateAdapter(errorMessage) { adapter!!.retry() }
            )
            retryButton.setOnClickListener { adapter!!.retry() }
        }

        moviesViewModel.movies.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter!!.submitData(it)
            }
        }

        // Show a toast when a network error occurs.
        moviesViewModel.networkError.observe(viewLifecycleOwner) { isError ->
            if (isError && !moviesViewModel.isNetworkErrorShown.value!!) {
                Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT)
                    .show()
                moviesViewModel.onNetworkErrorShown()
            }
        }

        // Collect load states and show views corresponding to the states
        lifecycleScope.launch {
            adapter!!.loadStateFlow.collect { loadStates ->
                val isListEmpty = loadStates.refresh is LoadState.NotLoading
                        && adapter!!.itemCount == 0

                binding.apply {
                    tvNoResults.isVisible = isListEmpty
                    recyclerView.isVisible = !isListEmpty
                    progressBar.isVisible = loadStates.source.refresh is LoadState.Loading
                    retryButton.isVisible = loadStates.refresh is LoadState.Error
                    tvNoInternet.isVisible = loadStates.refresh is LoadState.Error
                }
            }
        }

        movieFiltersDialogFragment = MovieFiltersDialogFragment(moviesViewModel)
    }

    //region Options menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)

        val itemId = when (moviesViewModel.queryParams.sortBy) {
            SortBy.POPULARITY_DESC -> R.id.sort_by_popularity_desc
            SortBy.VOTE_AVERAGE_DESC -> R.id.sort_by_rating_desc
            SortBy.RELEASE_DATE_DESC -> R.id.sort_by_date_desc
            SortBy.RELEASE_DATE_ASC -> R.id.sort_by_date_asc
            SortBy.POPULAR -> R.id.option_show_popular
            SortBy.TOP_RATED -> R.id.option_show_top_rated
        }
        menu.findItem(itemId).isChecked = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_popularity_desc -> {
                moviesViewModel.getMoviesWithQuery(QueryParams(SortBy.POPULARITY_DESC))
                item.isChecked = true
                return true
            }
            R.id.sort_by_rating_desc -> {
                moviesViewModel.getMoviesWithQuery(QueryParams(SortBy.VOTE_AVERAGE_DESC))
                item.isChecked = true
                return true
            }
            R.id.sort_by_date_desc -> {
                moviesViewModel.getMoviesWithQuery(QueryParams(SortBy.RELEASE_DATE_DESC))
                item.isChecked = true
                return true
            }
            R.id.sort_by_date_asc -> {
                moviesViewModel.getMoviesWithQuery(QueryParams(SortBy.RELEASE_DATE_ASC))
                item.isChecked = true
                return true
            }

            R.id.option_filter_by -> {
                movieFiltersDialogFragment.show(
                    requireActivity().supportFragmentManager,
                    "movieFiltersDialogFragment"
                )
                return true
            }

            R.id.option_show_popular -> {
                moviesViewModel.getMoviesWithQuery(QueryParams(SortBy.POPULAR))
                (requireActivity() as MainActivity)
                    .supportActionBar?.title = getString(R.string.option_show_popular)

                return true
            }
            R.id.option_show_top_rated -> {
                moviesViewModel.getMoviesWithQuery(QueryParams(SortBy.TOP_RATED))
                (requireActivity() as MainActivity)
                    .supportActionBar?.title = getString(R.string.option_show_top_rated)

                return true
            }
        }

        return false
    }
    //endregion
}