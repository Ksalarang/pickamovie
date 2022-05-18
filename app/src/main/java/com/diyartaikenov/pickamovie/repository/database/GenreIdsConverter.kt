package com.diyartaikenov.pickamovie.repository.database

import androidx.room.TypeConverter
import java.util.*

class GenreIdsConverter {

    @TypeConverter
    fun listToString(genreIds: List<Int>): String {
        val joiner = StringJoiner(",")
        genreIds.forEach { id -> joiner.add(id.toString()) }

        return joiner.toString()
    }

    // todo: toInt() can throw a NumberFormatException
    @TypeConverter
    fun stringToList(value: String): List<Int> {
        return if (value.isNotEmpty()) {
            value.split(",").map { it.toInt() }
        } else {
            listOf()
        }
    }
}