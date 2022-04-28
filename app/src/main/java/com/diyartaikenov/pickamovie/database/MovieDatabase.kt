package com.diyartaikenov.pickamovie.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DatabaseMovie::class], version = 1, exportSchema = true)
abstract class MovieDatabase: RoomDatabase() {

}