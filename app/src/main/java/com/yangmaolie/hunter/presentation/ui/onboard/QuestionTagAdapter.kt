package com.yangmaolie.hunter.presentation.ui.onboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.yangmaolie.hunter.databinding.ItemTagChoiceBinding

class QuestionTagAdapter(
    private val options: List<OnboardQuestion.Option>,
    private val questionType: OnboardQuestion.QuestionType,
    private val onSelectionChanged: (OnboardQuestion.Option, Boolean) -> Unit
) : RecyclerView.Adapter<QuestionTagAdapter.TagViewHolder>() {

    private val selected = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemTagChoiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int = options.size

    inner class TagViewHolder(
        private val binding: ItemTagChoiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(option: OnboardQuestion.Option) {
            binding.tvLabel.text = option.label

            val isSelected = selected.contains(option.value)
            updateSelection(binding.root, isSelected)

            binding.root.setOnClickListener {
                if (questionType == OnboardQuestion.QuestionType.SINGLE_CHOICE) {
                    // Clear other selections for single choice
                    selected.clear()
                    selected.add(option.value)
                    notifyDataSetChanged()
                    onSelectionChanged(option, true)
                } else {
                    // Toggle for multi choice
                    if (isSelected) {
                        selected.remove(option.value)
                        onSelectionChanged(option, false)
                    } else {
                        selected.add(option.value)
                        onSelectionChanged(option, true)
                    }
                    updateSelection(binding.root, !isSelected)
                }
            }
        }

        private fun updateSelection(card: MaterialCardView, isSelected: Boolean) {
            if (isSelected) {
                card.setCardBackgroundColor(
                    card.context.getColor(com.yangmaolie.hunter.R.color.primary)
                )
                binding.tvLabel.setTextColor(
                    card.context.getColor(com.yangmaolie.hunter.R.color.white)
                )
            } else {
                card.setCardBackgroundColor(
                    card.context.getColor(com.yangmaolie.hunter.R.color.white)
                )
                binding.tvLabel.setTextColor(
                    card.context.getColor(com.yangmaolie.hunter.R.color.text_primary)
                )
            }
        }
    }
}
