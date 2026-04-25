package com.yangmaolie.hunter.presentation.ui.onboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.yangmaolie.hunter.databinding.ItemOnboardQuestionBinding

class OnboardAdapter(
    private val questions: List<OnboardQuestion>,
    private val onOptionSelected: (Int, OnboardQuestion.Option, Boolean) -> Unit
) : RecyclerView.Adapter<OnboardAdapter.QuestionViewHolder>() {

    private val selectedOptions = questions.map { mutableListOf<OnboardQuestion.Option>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemOnboardQuestionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }

    override fun getItemCount(): Int = questions.size

    fun getSelectedOptions(): List<List<OnboardQuestion.Option>> {
        return selectedOptions
    }

    inner class QuestionViewHolder(
        private val binding: ItemOnboardQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: OnboardQuestion, position: Int) {
            binding.tvQuestion.text = question.question
            binding.tvHint.text = question.hint

            val tagAdapter = QuestionTagAdapter(question.options, question.type) { option, isSelected ->
                if (isSelected) {
                    selectedOptions[position].add(option)
                } else {
                    selectedOptions[position].remove(option)
                }
                onOptionSelected(position, option, isSelected)
            }

            binding.flexboxTags.layoutManager = FlexboxLayoutManager(binding.root.context)
            binding.flexboxTags.adapter = tagAdapter
        }
    }
}
