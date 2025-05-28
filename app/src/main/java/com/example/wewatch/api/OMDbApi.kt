package com.example.wewatch.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("s") title: String,
        @Query("apikey") apiKey: String = "44941fd3"
    ): MovieResponse
}

data class MovieResponse(val Search: List<MovieItem>?)

data class MovieItem(val Title: String, val Year: String, val Poster: String)