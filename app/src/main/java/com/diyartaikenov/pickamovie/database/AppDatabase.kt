package com.diyartaikenov.pickamovie.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 2,
    exportSchema = false,
    entities = [DatabaseMovie::class, Genre::class]
)
abstract class AppDatabase: RoomDatabase() {
    abstract val movieDao: MovieDao
}