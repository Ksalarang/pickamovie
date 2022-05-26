package com.diyartaikenov.pickamovie.ui.homeviewpager.loadstateadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.diyartaikenov.pickamovie.R
import com.diyartaikenov.pickamovie.databinding.MoviesLoadStateFooterItemBinding

/**
 * ViewHolder is responsible for setting the visibility of each view depending on the [LoadState].
 */
class MoviesLoadStateViewHolder(
    private val binding: MoviesLoadStateFooterItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState, errorMessage: String) {
        if (loadState is LoadState.Error) {
            binding.errorMessage.text = errorMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMessage.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): MoviesLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movies_load_state_footer_item, parent, false)
            val binding = MoviesLoadStateFooterItemBinding.bind(view)

            return MoviesLoadStateViewHolder(binding, retry)
        }
    }
}