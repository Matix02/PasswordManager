package com.example.passwordmanager

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanager.databinding.ItemCacheQueryBinding
import com.example.passwordmanager.extension.inflater
import com.example.passwordmanager.webDetailsList.model.QueryData

class SearchQueryAdapter(
    private val onItemClick: (QueryData) -> Unit,
) : ListAdapter<QueryData, SearchQueryAdapter.CacheQueryItemViewHolder>(QueryItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CacheQueryItemViewHolder {
        return CacheQueryItemViewHolder(ItemCacheQueryBinding.inflate(parent.inflater(), parent, false), onItemClick)
    }

    override fun onBindViewHolder(holder: CacheQueryItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CacheQueryItemViewHolder(
        private val binding: ItemCacheQueryBinding,
        private val onClickListener: (QueryData) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: QueryData) {
            binding.queryTitle.text = model.query
            binding.root.setOnClickListener { onClickListener(model) }
        }
    }

    private object QueryItemDiffCallback : DiffUtil.ItemCallback<QueryData>() {
        override fun areItemsTheSame(oldItem: QueryData, newItem: QueryData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QueryData, newItem: QueryData): Boolean {
            return oldItem == newItem
        }
    }

}