package com.diyartaikenov.pickamovie.ui.homeviewpager.movies

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.setMargins
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.FragmentMovieDetailsBinding
import com.diyartaikenov.pickamovie.model.DetailedMovie
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.model.MovieVideo
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.ui.MainActivity
import com.diyartaikenov.pickamovie.util.IMAGE_BASE_URL
import com.diyartaikenov.pickamovie.util.convertDpToPixels
import com.diyartaikenov.pickamovie.util.setVoteAverageAndColor
import com.diyartaikenov.pickamovie.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

private const val BACKDROP_SIZE = "/w780"
private const val SHORT_OVERVIEW_MAX_LINES = 5

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
        moviesViewModel.addObservers()

        setupShowMoreButton()
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

    private fun MoviesViewModel.addObservers() {
        movie.observe(viewLifecycleOwner) { movie ->
            bindMovie(movie)
        }

        detailedMovie.observe(viewLifecycleOwner) { movie ->
            bindDetailedMovie(movie)
        }

        networkError.observe(viewLifecycleOwner) { isError ->
            if (isError && !isNetworkErrorShown.value!!) {
                Toast.makeText(
                    context,
                    getString(R.string.network_error),
                    Toast.LENGTH_SHORT
                ).show()
                onNetworkErrorShown()
            }
        }
    }

    private fun bindMovie(movie: Movie) {
        binding.apply {
            expandedToolbarBackground.visibility = View.VISIBLE
            toolbar.title = movie.title
            overview.text = if (movie.overview.isNullOrBlank()) {
                getString(R.string.movie_has_no_overview)
            } else { movie.overview }

            setVoteAverageAndColor(voteAverage, movie.voteAverage)
            voteCount.text = String.format("(%d)", movie.voteCount)

            Glide.with(this@MovieDetailsFragment)
                .load(IMAGE_BASE_URL + BACKDROP_SIZE + movie.backdropPath)
                .into(backdrop)
        }
    }

    private fun bindDetailedMovie(movie: DetailedMovie) {
        binding.apply {
            genres.text = movie.genres.asDecoratedString()
            releaseDate.text = movie.releaseDate
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            
            movie.runtime?.let {
                separatorForRuntime.visibility = View.VISIBLE
                runtime.text = getString(R.string.runtime_mins, it)
            }
            status.text = resources.getStringArray(R.array.movie_statuses)[movie.status.ordinal]

            addVideoViews(moviesViewModel.filterVideos(movie.videos))
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

    /**
     * Add after text changed listener to the overview text view
     * to show the "Show more" button which expands the overview text
     * when there are too many lines of it.
     */
    private fun setupShowMoreButton() {
        binding.overview.addTextChangedListener {
            binding.apply {
                // Wait for the TextView to draw the text,
                // then perform the line count related operations.
                overview.post {
                    if (overview.lineCount > SHORT_OVERVIEW_MAX_LINES) {
                        buttonShowMore.visibility = View.VISIBLE

                        buttonShowMore.setOnClickListener {
                            overview.maxLines = Integer.MAX_VALUE
                            buttonShowMore.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    /**
     * Add videos as [ImageView]s to the [binding]'s **videosLinearLayout**.
     * Each view display a video thumbnail. Tapping a view will open the link to the video.
     */
    private fun addVideoViews(videos: List<MovieVideo>) {
        // Layout params for image views for each movie trailer
        val layoutParams = LinearLayout.LayoutParams(
            convertDpToPixels(160, requireContext()),
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(convertDpToPixels(6, requireContext()))

        videos.forEach { video ->
            val imageView = ImageView(context)
            imageView.layoutParams = layoutParams

            imageView.setOnClickListener {
                // Open the video via third-party apps
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=${video.key}")
                )
                try {
                    startActivity(webIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, getString(R.string.no_app_can_open_url), Toast.LENGTH_SHORT).show()
                }
            }

            binding.videosLinearLayout.post {
                binding.videosLinearLayout.addView(imageView)
            }

            // Load a thumbnail for a video
            Glide.with(this)
                .load("https://i.ytimg.com/vi/${video.key}/mqdefault.jpg")
                .into(imageView)
        }
    }
}