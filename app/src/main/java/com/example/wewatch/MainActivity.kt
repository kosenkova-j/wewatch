package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wewatch.database.movieDao
import com.example.wewatch.database.movieDatabase
import com.example.wewatch.api.MovieItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE = 100
        const val MOVIE_ID = "MOVIE_ID"
        const val MOVIE_TITLE = "MOVIE_TITLE"
        const val MOVIE_YEAR = "MOVIE_YEAR"
        const val MOVIE_POSTER = "MOVIE_POSTER"
    }

    private lateinit var movieDao: movieDao
    private lateinit var adapter: MovieAdapter
    private val movieList = mutableListOf<MovieItem>()
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = movieDatabase.getDatabase(this)
        movieDao = db.movieDao()
        adapter = MovieAdapter()
        btnDelete = findViewById(R.id.btn_delete)

        // Инициализация RecyclerView
        findViewById<RecyclerView>(R.id.recycler_view_main).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // Загрузка данных из базы при старте
        loadMoviesFromDatabase()

        // Обработчик кликов
        adapter.listener = object : MovieAdapter.OnMovieClickListener {
            override fun onMovieClick(movie: MovieItem) {
                deleteMovie(movie)
            }

            override fun onMovieCheckedChanged(hasCheckedItems: Boolean) {
                btnDelete.visibility = if (hasCheckedItems) Button.VISIBLE else Button.GONE
            }
        }

        // Кнопка удаления отмеченных фильмов
        btnDelete.setOnClickListener {
            deleteCheckedMovies()
        }

        // Кнопка добавления нового фильма
        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun loadMoviesFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = movieDao.getAllMovies().map {
                MovieItem(it.id, it.title, it.year, it.poster)
            }
            withContext(Dispatchers.Main) {
                movieList.clear()
                movieList.addAll(movies)
                adapter.setData(movieList)
            }
        }
    }

    private fun deleteMovie(movie: MovieItem) {
        CoroutineScope(Dispatchers.IO).launch {
            movieDao.delete(movie.toMovie())
            withContext(Dispatchers.Main) {
                movieList.removeAll { it.ID == movie.ID }
                adapter.setData(movieList)
                Toast.makeText(this@MainActivity, "${movie.Title} удален", Toast.LENGTH_SHORT).show()
                updateDeleteButtonVisibility()
            }
        }
    }

    private fun deleteCheckedMovies() {
        val checkedMovies = adapter.getCheckedMovies()
        if (checkedMovies.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                movieDao.deleteAll(checkedMovies.map { it.toMovie() })
                withContext(Dispatchers.Main) {
                    movieList.removeAll(checkedMovies)
                    adapter.setData(movieList)
                    btnDelete.visibility = Button.GONE
                    Toast.makeText(
                        this@MainActivity,
                        "Удалено ${checkedMovies.size} фильмов",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateDeleteButtonVisibility() {
        btnDelete.visibility = if (adapter.hasCheckedItems()) Button.VISIBLE else Button.GONE
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val id = data.getIntExtra(MOVIE_ID, -1)
            val title = data.getStringExtra(MOVIE_TITLE)
            val year = data.getStringExtra(MOVIE_YEAR)
            val poster = data.getStringExtra(MOVIE_POSTER)

            if (id != -1 && title != null && year != null && poster != null) {
                val movie = MovieItem(id, title, year, poster)
                CoroutineScope(Dispatchers.IO).launch {
                    movieDao.insert(movie.toMovie())
                    withContext(Dispatchers.Main) {
                        movieList.add(movie)
                        adapter.setData(movieList)
                        Toast.makeText(
                            this@MainActivity,
                            "$title добавлен в список",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}