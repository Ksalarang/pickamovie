package com.diyartaikenov.pickamovie.model

data class Movie(
    val id: Long,
    val title: String,
    val posterPath: String?,
)

data class Genre(
    val id: Long,
    val name: String,
)