package com.diyartaikenov.pickamovie.ui.homeviewpager.loadstateadapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

/**
 * Adapter for displaying RecyclerView items based on [LoadState] such as a header and a footer
 * which contain a progress bar or an error message with a retry button.
 */
class MoviesLoadStateAdapter(
    private val errorMessage: String,
    private val retry: () -> Unit,
) : LoadStateAdapter<MoviesLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: MoviesLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState, errorMessage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): MoviesLoadStateViewHolder {
        return MoviesLoadStateViewHolder.create(parent, retry)
    }
}