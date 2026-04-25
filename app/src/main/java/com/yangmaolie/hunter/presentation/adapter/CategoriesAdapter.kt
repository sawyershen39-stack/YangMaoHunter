package com.yangmaolie.hunter.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yangmaolie.hunter.databinding.ItemCategoryBinding
import com.yangmaolie.hunter.domain.model.Deal

class CategoriesAdapter(
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private val categories = mutableListOf<Pair<String, String>>()
    private var selectedPosition = 0

    init {
        categories.add("全部" to "all")
        categories.addAll(
            listOf(
                "奶茶" to Deal.CATEGORY_MILK_TEA,
                "餐饮" to Deal.CATEGORY_RESTAURANT,
                "银行" to Deal.CATEGORY_BANK,
                "网购" to Deal.CATEGORY_ONLINE_SHOP,
                "美食" to Deal.CATEGORY_FOOD_DRINK,
                "其他" to Deal.CATEGORY_OTHER
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val (name, code) = categories[position]
        holder.binding.tvCategory.text = name

        if (position == selectedPosition) {
            holder.binding.root.setCardBackgroundColor(
                holder.binding.root.context.getColor(com.yangmaolie.hunter.R.color.category_selected)
            )
            holder.binding.tvCategory.setTextColor(
                holder.binding.root.context.getColor(com.yangmaolie.hunter.R.color.primary)
            )
        } else {
            holder.binding.root.setCardBackgroundColor(
                holder.binding.root.context.getColor(com.yangmaolie.hunter.R.color.white)
            )
            holder.binding.tvCategory.setTextColor(
                holder.binding.root.context.getColor(com.yangmaolie.hunter.R.color.text_secondary)
            )
        }

        holder.binding.root.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onCategoryClick(code)
        }
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)
}
