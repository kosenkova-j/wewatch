package com.example.wewatch

import android.content.Context
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

        // Загрузка данных из базы при старте
        loadMoviesFromDatabase()

        // Инициализация RecyclerView
        findViewById<RecyclerView>(R.id.recycler_view_main).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

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

    override fun onResume() {
        super.onResume()
        loadMoviesFromDatabase()
    }

    private fun loadMoviesFromDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = movieDao.getAllMovies().map {
                MovieItem(
                    imdbID = it.id, // Теперь id — строка (imdbID)
                    Title = it.title,
                    Year = it.year,
                    Poster = it.poster
                )
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
                movieList.removeAll { it.imdbID == movie.imdbID }
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
            val imdbID = data.getStringExtra(MOVIE_ID)
            val title = data.getStringExtra(MOVIE_TITLE)
            val year = data.getStringExtra(MOVIE_YEAR)
            val poster = data.getStringExtra(MOVIE_POSTER)

            if (imdbID != null && title != null && year != null && poster != null) {
                val movie = MovieItem(imdbID, title, year, poster)
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