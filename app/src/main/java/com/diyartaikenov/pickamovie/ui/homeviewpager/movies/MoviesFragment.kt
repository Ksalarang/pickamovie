package com.diyartaikenov.pickamovie.ui.homeviewpager.movies

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.FragmentMoviesBinding
import com.diyartaikenov.pickamovie.repository.network.MovieList
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

    private val moviesViewModel: MoviesViewModel by activityViewModels()
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
        // Scroll to top of list on inserting items
        adapter!!.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        })
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
        movieFiltersDialogFragment = MovieFiltersDialogFragment()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    //region Options menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_filter_by -> {
                movieFiltersDialogFragment.show(
                    requireActivity().supportFragmentManager,
                    "movieFiltersDialogFragment"
                )
                return true
            }

            R.id.option_show_popular -> {
                moviesViewModel.getMoviesWithQueryParams(
                    movieList = MovieList.POPULAR,
                    withGenres = listOf(),
                    withoutGenres = listOf(),
                )
                (requireActivity() as MainActivity)
                    .supportActionBar?.title = getString(R.string.option_popular)
                return true
            }
            R.id.option_show_top_rated -> {
                moviesViewModel.getMoviesWithQueryParams(
                    movieList = MovieList.TOP_RATED,
                    withGenres = listOf(),
                    withoutGenres = listOf(),
                )
                (requireActivity() as MainActivity)
                    .supportActionBar?.title = getString(R.string.option_top_rated)
                return true
            }
        }
        return false
    }
    //endregion
}