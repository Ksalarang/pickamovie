package com.diyartaikenov.pickamovie.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.MovieItemBinding
import com.diyartaikenov.pickamovie.model.Movie
import javax.inject.Inject

class MovieListAdapter @Inject constructor()
    : PagingDataAdapter<Movie, MovieListAdapter.MovieViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return MovieViewHolder(
            MovieItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)

        holder.bind(movie)
    }

    inner class MovieViewHolder(
        private val binding: MovieItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie?) {

            if (movie == null) {
                binding.name.text = itemView.resources.getString(R.string.loading)
            } else {
                binding.movie = movie
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