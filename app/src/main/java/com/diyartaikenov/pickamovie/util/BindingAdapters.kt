package com.diyartaikenov.pickamovie.util

import android.util.Log
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

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
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

@BindingAdapter("year")
fun setYear(textView: TextView, date: LocalDate) {
    textView.text = date.year.toString()
}

@BindingAdapter("genres", "scope", "repository")
fun setGenres(
    textView: TextView,
    genres: List<Int>,
    viewModelScope: CoroutineScope,
    repository: MovieRepository
) {
    viewModelScope.launch {
        repository.getGenresById(genres).collectLatest { genres ->
            val joiner = StringJoiner(", ")
            genres.forEach { joiner.add(it.name) }
            textView.text = joiner.toString()
        }
    }
}