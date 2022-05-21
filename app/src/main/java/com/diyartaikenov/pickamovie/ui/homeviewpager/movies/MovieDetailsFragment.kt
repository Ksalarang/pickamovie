package com.diyartaikenov.pickamovie.ui.homeviewpager.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.FragmentMovieDetailsBinding
import com.diyartaikenov.pickamovie.model.DetailedMovie
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.ui.MainActivity
import com.diyartaikenov.pickamovie.util.IMAGE_BASE_URL
import com.diyartaikenov.pickamovie.util.setVoteAverageAndColor
import com.diyartaikenov.pickamovie.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

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

        moviesViewModel.movie.observe(viewLifecycleOwner) { movie ->
            bindMovie(movie)
        }

        moviesViewModel.detailedMovie.observe(viewLifecycleOwner) { movie ->
            bindDetailedMovie(movie)
        }

        moviesViewModel.networkError.observe(viewLifecycleOwner) { isError ->
            if (isError && !moviesViewModel.isNetworkErrorShown.value!!) {
                Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                moviesViewModel.onNetworkErrorShown()
            }
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

    private fun bindMovie(movie: Movie) {
        binding.apply {
            expandedToolbarBackground.visibility = View.VISIBLE
            toolbar.title = movie.title
            overview.text = if (movie.overview.isNullOrBlank()) {
                getString(R.string.movie_has_no_overview)
            } else { movie.overview }

            releaseDate.text = movie.releaseDate
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))

            setVoteAverageAndColor(voteAverage, movie.voteAverage)
            voteCount.text = String.format("(%d)", movie.voteCount)

            Glide
                .with(backdrop.context)
                .load(IMAGE_BASE_URL + BACKDROP_SIZE + movie.backdropPath)
                .into(backdrop)
        }
    }

    private fun bindDetailedMovie(movie: DetailedMovie) {
        binding.apply {
            genres.text = movie.genres.asDecoratedString()
            movie.runtime?.let {
                separator1.visibility = View.VISIBLE
                runtime.text = getString(R.string.runtime_mins, it)
            }
            status.text = resources.getStringArray(R.array.movie_statuses)[movie.status.ordinal]

            Glide
                .with(backdrop.context)
                .load(IMAGE_BASE_URL + BACKDROP_SIZE + movie.backdropPath)
                .into(backdrop)
        }
    }

    /**
     * Take first 4 genres and separate them with a comma.
     * Capitalize the first letter of the resulting string.
     */
    private fun List<Genre>.asDecoratedString(): String {
        val joiner = StringJoiner(", ")
        this.take(4).forEach { joiner.add(it.name) }

        var genres = joiner.toString()
        if (genres.isNotEmpty()) {
            genres = genres.substring(0, 1).uppercase() +
                    genres.substring(1).lowercase()
        }
        return genres
    }
}