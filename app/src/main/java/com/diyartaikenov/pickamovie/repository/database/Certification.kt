package com.diyartaikenov.pickamovie.repository.database

import androidx.room.Entity

@Entity(tableName = "movie_certifications", primaryKeys = ["value", "country"])
data class Certification(
    val value: String,
    val meaning: String,
    val ordinal: Int,
    val country: String,
)