package com.diyartaikenov.pickamovie.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diyartaikenov.pickamovie.databinding.MovieItemBinding
import com.diyartaikenov.pickamovie.model.Movie
import javax.inject.Inject

class MovieListAdapter @Inject constructor()
    : ListAdapter<Movie, MovieListAdapter.MovieViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return MovieViewHolder(
            MovieItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val show = getItem(position)

        holder.bind(show)
    }

    inner class MovieViewHolder(
        private val binding: MovieItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {

            binding.apply {
                this.movie = movie
                executePendingBindings()
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Movie>() {

        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}