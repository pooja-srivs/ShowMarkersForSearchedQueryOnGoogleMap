package com.example.daytonaassignment.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserPlaces::class, RecentSearch::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun queryDao(): QueryDao

}