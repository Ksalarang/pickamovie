package com.diyartaikenov.pickamovie.util

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.repository.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
private const val POSTER_SIZE = "/w342"
private const val BACKDROP_SIZE = "/w300"

@BindingAdapter("posterPath", "backdropPath")
/**
 * Loads a poster for a movie or a backdrop if the poster is absent.
 */
fun loadPoster(imageView: ImageView, posterPath: String?, backdropPath: String?) {
    var url: String? = null

    if (posterPath != null) {
        url = IMAGE_BASE_URL + POSTER_SIZE + posterPath
    } else if (backdropPath != null) {
        url = IMAGE_BASE_URL + BACKDROP_SIZE + backdropPath
    }

    Glide.with(imageView.context)
        .load(url)
        .placeholder(R.drawable.default_poster)
        .into(imageView)
}

@BindingAdapter("voteAverage")
/**
 * Sets vote average and text color based on the movie's vote average.
 */
fun setVoteAverageAndColor(textView: TextView, voteAverage: Double) {
    textView.text = String.format("%.1f", voteAverage)

    if (voteAverage >= 8.5) {
        textView.setTextColor(Color.CYAN)
    } else if (voteAverage >= 7) {
        textView.setTextColor(Color.GREEN)
    } else if (voteAverage >= 5) {
        textView.setTextColor(Color.YELLOW)
    } else {
        textView.setTextColor(Color.RED)
    }
}

@BindingAdapter("genreIds", "scope", "repository")
fun setGenres(
    textView: TextView,
    genreIds: List<Int>,
    viewModelScope: CoroutineScope,
    repository: MovieRepository
) {
    viewModelScope.launch {
        repository.getGenresById(genreIds).collectLatest { genreList ->
            val joiner = StringJoiner(", ")
            genreList.take(3).forEach { joiner.add(it.name) }
            var genres = joiner.toString()
            // Capitalize the first letter
            if (genres.isNotEmpty()) {
                genres = genres.substring(0, 1).uppercase() +
                        genres.substring(1).lowercase()
            }
            textView.text = genres
        }
    }
}