package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wewatch.api.MovieItem
import com.example.wewatch.api.RetrofitClient
import com.example.wewatch.database.movieDao
import com.example.wewatch.database.movieDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {
    private val apiService = RetrofitClient.apiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var btnConfirm: Button
    private lateinit var movieDao: movieDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        // Инициализация Room Database
        val db = movieDatabase.getDatabase(this)
        movieDao = db.movieDao()

        recyclerView = findViewById(R.id.recyclerView)
        btnConfirm = findViewById(R.id.btn_confirm)
        adapter = MovieAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val year = intent.getStringExtra("YEAR")
        val query = intent.getStringExtra("QUERY") ?: return

        // Загрузка данных
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = if (year.isNullOrEmpty()) {
                    apiService.searchMovies(query)
                } else if (query.isNullOrEmpty()) {
                    apiService.searchMovies(year)
                } else {
                    apiService.searchMovies("$query, $year")
                }
                withContext(Dispatchers.Main) {
                    if (response.Search != null) {
                        adapter.setData(response.Search)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Обработчик изменения состояния чекбоксов
        adapter.checkListener = { hasCheckedItems ->
            btnConfirm.isEnabled = hasCheckedItems
        }

        btnConfirm.setOnClickListener {
            val selectedMovies = adapter.getCheckedMovies()
            if (selectedMovies.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    movieDao.insertAll(selectedMovies.map { it.toMovie() })

                    withContext(Dispatchers.Main) {
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        }
    }
}