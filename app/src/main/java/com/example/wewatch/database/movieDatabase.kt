package com.example.wewatch.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Movie::class], version = 1)
abstract class movieDatabase : RoomDatabase() {
    abstract fun movieDao(): movieDao

    companion object {
        @Volatile
        private var INSTANCE: movieDatabase? = null

        fun getDatabase(context: Context): movieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, movieDatabase::class.java, "movie_database").build()
                INSTANCE = instance
                instance
            }
        }
    }
}