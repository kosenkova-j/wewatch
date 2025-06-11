package com.example.wewatch.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey val id: Int, // или другой подходящий идентификатор
    val title: String,
    val year: String,
    val poster: String,
    var isChecked: Boolean = false
)