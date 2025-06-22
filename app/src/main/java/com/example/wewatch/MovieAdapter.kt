package com.example.wewatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wewatch.api.MovieItem
import com.bumptech.glide.Glide

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    private var movies: List<MovieItem> = emptyList()
    private val checkedMovies = mutableSetOf<String>() // Храним только выбранные ID

    var checkListener: ((Boolean) -> Unit)? = null
    var listener: OnMovieClickListener? = null

    interface OnMovieClickListener {
        fun onMovieClick(movie: MovieItem)
        fun onMovieCheckedChanged(hasCheckedItems: Boolean)
    }

    fun setData(movieList: List<MovieItem>) {
        this.movies = movieList
        notifyDataSetChanged()
        updateCheckedState()
    }

    fun getCheckedMovies(): List<MovieItem> {
        return movies.filter { it.imdbID in checkedMovies }
    }

    fun hasCheckedItems(): Boolean {
        return checkedMovies.isNotEmpty()
    }

    private fun updateCheckedState() {
        checkListener?.invoke(checkedMovies.isNotEmpty())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.title.text = movie.Title
        holder.year.text = movie.Year
        Glide.with(holder.itemView.context).load(movie.Poster).into(holder.poster)

        // Устанавливаем состояние чекбокса без вызова слушателя
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = checkedMovies.contains(movie.imdbID)

        // Обработка клика на чекбокс
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedMovies.add(movie.imdbID)
            } else {
                checkedMovies.remove(movie.imdbID)
            }
            updateCheckedState()
        }

        // Обработка клика на весь элемент (опционально)
        holder.itemView.setOnClickListener {
            listener?.onMovieClick(movie)
        }
    }

    override fun getItemCount(): Int = movies.size

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.movie_title)
        val year: TextView = itemView.findViewById(R.id.movie_year)
        val poster: ImageView = itemView.findViewById(R.id.movie_poster)
        val checkbox: CheckBox = itemView.findViewById(R.id.movie_checkbox)
    }
}