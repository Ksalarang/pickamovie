package com.diyartaikenov.pickamovie.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    exportSchema = false,
    entities = [DatabaseMovie::class]
)
abstract class AppDatabase: RoomDatabase() {
    abstract val movieDao: MovieDao
}