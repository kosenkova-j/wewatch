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
    val imdbID: String, // Например, "tt1234567"
    val Title: String,
    val Year: String,
    val Poster: String
) {
    fun toMovie(): Movie {
        return Movie(
            id = this.imdbID, // Используем imdbID как уникальный ключ
            title = this.Title,
            year = this.Year,
            poster = this.Poster
        )
    }
}