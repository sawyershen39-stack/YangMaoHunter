package com.yangmaolie.hunter.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yangmaolie.hunter.core.extensions.formatDate
import com.yangmaolie.hunter.databinding.ItemDealCardBinding
import com.yangmaolie.hunter.domain.model.Deal

class DealsAdapter(
    private val onDealClick: (Deal) -> Unit
) : ListAdapter<Deal, DealsAdapter.DealViewHolder>(DealDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val binding = ItemDealCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DealViewHolder(
        private val binding: ItemDealCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDealClick(getItem(position))
                }
            }
        }

        fun bind(deal: Deal) {
            binding.tvTitle.text = deal.title

            // Category tag
            binding.tvCategory.text = deal.getCategoryDisplayName()

            // Price
            binding.tvDealPrice.text = "¥ %.0f".format(deal.dealPrice)
            if (deal.originalPrice > 0) {
                binding.tvOriginalPrice.text = "¥ %.0f".format(deal.originalPrice)
            } else {
                binding.tvOriginalPrice.text = ""
            }

            // Discount badge
            if (deal.discount > 0) {
                binding.badgeDiscount.visibility = android.view.View.VISIBLE
                binding.tvDiscount.text = "${deal.discount}折"
            } else {
                binding.badgeDiscount.visibility = android.view.View.GONE
            }

            // Upcoming badge
            if (deal.isUpcoming && deal.startTime > System.currentTimeMillis()) {
                binding.badgeUpcoming.visibility = android.view.View.VISIBLE
            } else {
                binding.badgeUpcoming.visibility = android.view.View.GONE
            }

            // Location
            if (!deal.isOnline && !deal.district.isNullOrEmpty()) {
                binding.tvLocation.text = deal.district
                binding.tvLocation.visibility = android.view.View.VISIBLE
            } else {
                binding.tvLocation.text = "线上"
                binding.tvLocation.visibility = android.view.View.VISIBLE
            }

            // Time
            val timeText = when {
                deal.isExpired() -> "已过期"
                deal.isUpcoming -> "${deal.startTime.formatDate()} 开抢"
                else -> "截止 ${deal.endTime.formatDate()}"
            }
            binding.tvTime.text = timeText

            // Cover image
            if (!deal.coverImage.isNullOrEmpty()) {
                Glide.with(binding.ivCover.context)
                    .load(deal.coverImage)
                    .centerCrop()
                    .placeholder(com.yangmaolie.hunter.R.drawable.placeholder)
                    .into(binding.ivCover)
            } else {
                Glide.with(binding.ivCover.context)
                    .load(com.yangmaolie.hunter.R.drawable.placeholder)
                    .into(binding.ivCover)
            }
        }
    }

    class DealDiffCallback : DiffUtil.ItemCallback<Deal>() {
        override fun areItemsTheSame(oldItem: Deal, newItem: Deal): Boolean {
            return oldItem.dealId == newItem.dealId
        }

        override fun areContentsTheSame(oldItem: Deal, newItem: Deal): Boolean {
            return oldItem == newItem
        }
    }
}
