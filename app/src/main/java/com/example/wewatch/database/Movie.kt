package com.example.wewatch.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: String, // Теперь String (например, imdbID "tt1234567")
    val title: String,
    val year: String,
    val poster: String,
    var isChecked: Boolean = false
)