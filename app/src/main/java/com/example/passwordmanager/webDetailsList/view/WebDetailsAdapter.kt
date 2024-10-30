package com.example.passwordmanager.webDetailsList.view

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.passwordmanager.databinding.ItemWebDetailsBinding
import com.example.passwordmanager.extension.inflater
import com.example.passwordmanager.webDetailsList.model.WebDetails

class WebDetailsAdapter(
    private val onItemClick: (WebDetails) -> Unit,
    private val onLongItemClick: (WebDetails) -> Unit,
    private val onCredentialClick: (String) -> Unit
) : ListAdapter<WebDetails, WebDetailsAdapter.WebDetailsViewHolder>(WebDetailsComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebDetailsViewHolder {
        return WebDetailsViewHolder(
            ItemWebDetailsBinding.inflate(parent.inflater(), parent, false),
            onItemClick,
            onLongItemClick,
            onCredentialClick
        )
    }

    override fun onBindViewHolder(holder: WebDetailsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WebDetailsViewHolder(
        private val binding: ItemWebDetailsBinding,
        private val onItemClick: (WebDetails) -> Unit,
        private val onLongItemClick: (WebDetails) -> Unit,
        private val onCredentialClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(webDetails: WebDetails) {
            with(binding) {
                if (webDetails.icon.isNotEmpty()) webLogoImageView.load(webDetails.icon)
                webNameTextView.text = webDetails.name
                credentialUsernameText.setText(webDetails.username)
                credentialPasswordText.setText(webDetails.password)
                expandCredentials(webDetails.shouldExpand)
                root.setOnClickListener {
                    onItemClick.invoke(webDetails)
                }
                root.setOnLongClickListener {
                    onLongItemClick.invoke(webDetails)
                    true
                }
                credentialUsernameText.setOnClickListener {
                    onCredentialClick.invoke(webDetails.username)
                }
                credentialPasswordText.setOnClickListener {
                    onCredentialClick.invoke(webDetails.password)
                }
            }
        }

        private fun expandCredentials(shouldExpand: Boolean) {
            binding.loginLabel.isVisible = shouldExpand
            binding.passwordLabel.isVisible = shouldExpand
        }
    }

    private object WebDetailsComparator : DiffUtil.ItemCallback<WebDetails>() {
        override fun areItemsTheSame(oldItem: WebDetails, newItem: WebDetails) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: WebDetails, newItem: WebDetails) = oldItem == newItem
    }
}