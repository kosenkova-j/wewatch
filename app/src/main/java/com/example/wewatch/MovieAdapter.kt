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
    private val checkedMovies = mutableSetOf<Int>() // Теперь храним ID вместо объектов

    //var clickListener: OnMovieClickListener? = null
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
        return movies.filter { it.ID in checkedMovies }
    }

    fun hasCheckedItems(): Boolean {
        return checkedMovies.isNotEmpty()
    }

    private fun updateCheckedState() {
        val hasChecked = checkedMovies.isNotEmpty()
        listener?.onMovieCheckedChanged(hasChecked)
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

        // Set checkbox state without triggering listener
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = checkedMovies.contains(movie.ID)

        holder.itemView.setOnClickListener {
            listener?.onMovieClick(movie)
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedMovies.add(movie.ID)
            } else {
                checkedMovies.remove(movie.ID)
            }
            updateCheckedState()
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