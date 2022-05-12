package com.diyartaikenov.pickamovie.repository.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "genres")
@JsonClass(generateAdapter = true)
data class Genre(
    @PrimaryKey
    val id: Int,
    val name: String,
)