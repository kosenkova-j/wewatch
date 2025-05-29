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

    private var movies: List<MovieItem> = listOf()

    var listener: OnMovieClickListener? = null

    interface OnMovieClickListener {
        fun onMovieClick(movie: MovieItem)
    }

    fun setData(movieList: List<MovieItem>) {
        this.movies = movieList
        notifyDataSetChanged()
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

        holder.itemView.setOnClickListener {
            listener?.onMovieClick(movie) // Передаем выбранный фильм через интерфейс
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listener?.onMovieClick(movie) // Передаем фильм на удаление
            }
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