package com.diyartaikenov.pickamovie.ui.homeviewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.FragmentMovieDetailsBinding
import com.diyartaikenov.pickamovie.model.DetailedMovie
import com.diyartaikenov.pickamovie.ui.MainActivity
import com.diyartaikenov.pickamovie.util.IMAGE_BASE_URL
import com.diyartaikenov.pickamovie.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val BACKDROP_SIZE = "/w780"

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private val moviesViewModel: MoviesViewModel by viewModels()
    private val navArgs: MovieDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moviesViewModel.refreshMovieDetails(navArgs.movieId)

        moviesViewModel.detailedMovie.observe(viewLifecycleOwner) { movie ->
            bindMovie(movie)
        }
    }

    override fun onStart() {
        super.onStart()

        (requireActivity() as MainActivity).apply {
            setBottomNavigationVisibility(View.GONE)
            supportActionBar?.hide()
        }
    }

    override fun onStop() {
        super.onStop()

        (requireActivity() as MainActivity).apply {
            setBottomNavigationVisibility(View.VISIBLE)
            supportActionBar?.show()
        }
    }

    private fun bindMovie(movie: DetailedMovie) {

        binding.apply {
            toolbar.title = movie.title
            overview.text = movie.overview ?: getString(R.string.no_overview)

            Glide
                .with(backdrop.context)
                .load(IMAGE_BASE_URL + BACKDROP_SIZE + movie.backdropPath)
                .into(backdrop)
        }
    }
}