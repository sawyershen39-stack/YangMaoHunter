package com.yangmaolie.hunter.presentation.ui.deal_detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.yangmaolie.hunter.R
import com.yangmaolie.hunter.core.base.BaseActivity
import com.yangmaolie.hunter.core.extensions.formatDefault
import com.yangmaolie.hunter.databinding.ActivityDealDetailBinding
import com.yangmaolie.hunter.domain.model.Deal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DealDetailActivity : BaseActivity<ActivityDealDetailBinding>() {

    override val binding: ActivityDealDetailBinding by lazy {
        ActivityDealDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: DealDetailViewModel by viewModels()

    companion object {
        const val EXTRA_DEAL_ID = "extra_deal_id"
    }

    override fun initViews() {
        val dealId = intent.getStringExtra(EXTRA_DEAL_ID)
        if (dealId.isNullOrEmpty()) {
            finish()
            return
        }
        viewModel.loadDeal(dealId)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnCollect.setOnClickListener {
            viewModel.toggleCollection()
        }

        binding.btnDislike.setOnClickListener {
            viewModel.onDislike()
            Snackbar.make(binding.root, "已记录不喜欢，我们会减少推荐相似内容", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnOpen.setOnClickListener {
            openDealLink()
        }
    }

    override fun observeData() {
        viewModel.deal.observe(this) { deal ->
            deal?.let { bindDeal(it) }
        }

        viewModel.isCollected.observe(this) { isCollected ->
            if (isCollected) {
                binding.btnCollect.text = "❤️ 已收藏"
                binding.btnCollect.backgroundTintList =
                    getColorStateList(R.color.primary)
                binding.btnCollect.setTextColor(getColor(R.color.white))
            } else {
                binding.btnCollect.text = "🤍 收藏"
                binding.btnCollect.backgroundTintList =
                    getColorStateList(R.color.primary_light)
                binding.btnCollect.setTextColor(getColor(R.color.primary))
            }
        }
    }

    private fun bindDeal(deal: Deal) {
        binding.tvTitle.text = deal.title
        binding.tvCategoryBadge.text = deal.getCategoryDisplayName()

        // Price
        binding.tvDealPrice.text = "¥ %.0f".format(deal.dealPrice)
        if (deal.originalPrice > 0) {
            binding.tvOriginalPrice.text = "¥ %.0f".format(deal.originalPrice)
        } else {
            binding.tvOriginalPrice.visibility = View.GONE
        }

        // Location
        if (deal.isOnline) {
            binding.tvLocation.text = "📍 线上"
        } else {
            binding.tvLocation.text = "📍 ${deal.district} ${deal.address}"
        }

        // Time
        val timeText = when {
            deal.isUpcoming -> "开始时间: ${deal.startTime.formatDefault()}"
            else -> "截止时间: ${deal.endTime.formatDefault()}"
        }
        binding.tvTime.text = timeText

        // Description
        binding.tvDescription.text = deal.description

        // Usage rules
        binding.llUsageRules.removeAllViews()
        if (deal.usageRules.isNotEmpty()) {
            deal.usageRules.forEachIndexed { index, rule ->
                val textView = TextView(this)
                textView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    (this as ViewGroup.MarginLayoutParams).topMargin = 4.dpToPx()
                }
                textView.text = "${index + 1}. $rule"
                textView.setTextColor(resources.getColor(R.color.text_secondary))
                textView.setTextSize(14f)
                textView.setLineSpacing(4f, 1f)
                binding.llUsageRules.addView(textView)
            }
        } else {
            val textView = TextView(this)
            textView.text = "无特殊规则"
            textView.setTextColor(resources.getColor(R.color.text_secondary))
            binding.llUsageRules.addView(textView)
        }

        // Cover image
        if (!deal.coverImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(deal.coverImage)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(binding.ivCover)
        } else {
            // No image - show placeholder with brand color
            binding.ivCover.setBackgroundColor(resources.getColor(com.yangmaolie.hunter.R.color.primary_light))
        }

        // Action button text
        if (!deal.actionUrl.isNullOrEmpty()) {
            binding.btnOpen.text = "立即领取"
        } else {
            binding.btnOpen.text = "无链接"
        }
    }

    private fun openDealLink() {
        val deal = viewModel.deal.value ?: return
        val url = deal.actionUrl
        if (url.isNullOrEmpty()) {
            Snackbar.make(binding.root, "暂无领取链接", Snackbar.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            // Add flag to open in external browser/app
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            startActivity(intent)
        } catch (e: Throwable) {
            // If no app can handle, open in browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onPageExit()
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
