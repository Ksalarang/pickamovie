package com.diyartaikenov.pickamovie.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.diyartaikenov.pickamovie.R

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
private const val POSTER_SIZE = "/w342"
private const val BACKDROP_SIZE = "/w300"

@BindingAdapter("posterPath", "backdropPath")
fun loadPoster(imageView: ImageView, posterPath: String?, backdropPath: String?) {
    var url: String? = null

    // load backdrop if there is no poster for the movie
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