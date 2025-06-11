package com.example.wewatch.api

import com.example.wewatch.database.Movie
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.Year

interface ApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("s") title: String,
        @Query("apikey") apiKey: String = "44941fd3"
    ): MovieResponse
}

data class MovieResponse(val Search: List<MovieItem>?)

data class MovieItem(
    val ID: Int,
    val Title: String,
    val Year: String,
    val Poster: String,
    var isChecked: Boolean = false
) {
    fun toMovie(): Movie {
        return Movie(
            id = ID, // создаем уникальный ID из названия и года
            title = Title,
            year = Year,
            poster = Poster,
            isChecked = isChecked
        )
    }
}