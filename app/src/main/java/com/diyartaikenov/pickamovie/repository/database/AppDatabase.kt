package com.diyartaikenov.pickamovie.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 6,
    exportSchema = false,
    entities = [DbMovie::class, Genre::class, Certification::class]
)
abstract class AppDatabase: RoomDatabase() {
    abstract val movieDao: MovieDao
}