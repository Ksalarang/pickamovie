package com.diyartaikenov.pickamovie.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diyartaikenov.pickamovie.databinding.ShowItemBinding
import com.diyartaikenov.pickamovie.model.Show

class ShowListAdapter(): ListAdapter<Show, ShowListAdapter.ShowViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return ShowViewHolder(
            ShowItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        val show = getItem(position)

        holder.bind(show)
    }

    inner class ShowViewHolder(
        private val binding: ShowItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(show: Show) {

            binding.apply {
                // todo: bind item
                executePendingBindings()
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Show>() {

        override fun areItemsTheSame(oldItem: Show, newItem: Show): Boolean {
//            return oldItem.id == newItem.id
            // fixme
            return true
        }

        override fun areContentsTheSame(oldItem: Show, newItem: Show): Boolean {
            return oldItem == newItem
        }
    }
}