package com.diyartaikenov.pickamovie.ui.homeviewpager.movies

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.diyartaikenov.pickamovie.ui.MainActivity
import com.diyartaikenov.pickamovie.util.IMAGE_BASE_URL
import com.diyartaikenov.pickamovie.util.convertDpToPixels
import com.diyartaikenov.pickamovie.util.join
import com.diyartaikenov.pickamovie.util.setVoteAverageAndColor
import com.diyartaikenov.pickamovie.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private const val BACKDROP_SIZE = "/w780"
private const val SHORT_OVERVIEW_MAX_LINES = 5
private const val BLANK_SIGN = "-"

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
            toolbar.title = movie.title.ifEmpty { BLANK_SIGN }
            expandedToolbarBackground.setOnClickListener {
                // Search in google "watch <movie's title>"
                val query = "${getString(R.string.watch)} ${movie.title}"
                viewUri("https://www.google.com/search?q=$query")
            }
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
            if (movie.title != movie.originalTitle) {
                originalTitle.visibility = View.VISIBLE
                originalTitle.text = movie.originalTitle.ifEmpty { BLANK_SIGN }
            }
            genres.text = movie.genres.map { it.name }
                .join(4, true, BLANK_SIGN)
            releaseDate.text = movie.releaseDate
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                .ifEmpty { BLANK_SIGN }
            
            movie.runtime?.let {
                separatorForRuntime.visibility = View.VISIBLE
                runtime.text = getString(R.string.runtime_mins, it)
            }
            status.text = resources.getStringArray(R.array.movie_statuses)[movie.status.ordinal]

            countries.text = movie.productionCountries.map { it.name }
                .join(n = 3, defaultReturnValue = BLANK_SIGN)

            addVideoViews(moviesViewModel.filterVideos(movie.videos))
        }
    }

    /**
     * Add after text changed listener to the overview text view
     * to show the "Show more" button. The button expands the overview text
     * when there are too many lines of it.
     */
    private fun setupShowMoreButton() {
        binding.overview.addTextChangedListener {
            binding.apply {
                // Wait for the TextView to draw the text,
                // then perform the line count related operations.
                overview.post {
                    // Display 'Show more' button if there are too many lines of text
                    if (overview.lineCount > SHORT_OVERVIEW_MAX_LINES) {
                        buttonShowMore.visibility = View.VISIBLE

                        buttonShowMore.setOnClickListener {
                            // Expand or shrink text
                            if (buttonShowMore.text == getString(R.string.show_more)) {
                                overview.maxLines = Integer.MAX_VALUE
                                buttonShowMore.text = getString(R.string.show_less)
                            } else {
                                overview.maxLines = SHORT_OVERVIEW_MAX_LINES
                                buttonShowMore.text = getString(R.string.show_more)
                            }
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

        if (videos.isNotEmpty()) {
            binding.trailersLabel.visibility = View.VISIBLE
            binding.videosScrollview.visibility = View.VISIBLE
        }

        videos.forEach { video ->
            val imageView = ImageView(context)
            imageView.layoutParams = layoutParams

            imageView.setOnClickListener {
                // Open the video via third-party apps
                viewUri("https://www.youtube.com/watch?v=${video.key}")
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

    /**
     * Start activity with implicit intent constructed with [Intent.ACTION_VIEW] and a [uri] string.
     */
    private fun viewUri(uri: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(uri)
        )
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, getString(R.string.no_app_can_open_uri), Toast.LENGTH_SHORT).show()
        }
    }
}