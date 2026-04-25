package com.yangmaolie.hunter.presentation.ui.onboard

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.yangmaolie.hunter.core.base.BaseActivity
import com.yangmaolie.hunter.data.remote.firebase.AuthService
import com.yangmaolie.hunter.databinding.ActivityPreferenceOnboardBinding
import com.yangmaolie.hunter.domain.model.UserPreference
import com.yangmaolie.hunter.domain.usecase.recommend.UpdateUserPreferencesUseCase
import com.yangmaolie.hunter.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class PreferenceOnboardActivity : BaseActivity<ActivityPreferenceOnboardBinding>() {

    override val binding: ActivityPreferenceOnboardBinding by lazy {
        ActivityPreferenceOnboardBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var updateUserPreferencesUseCase: UpdateUserPreferencesUseCase

    @Inject
    lateinit var authService: AuthService

    private val questions = OnboardQuestion.getQuestions()
    private lateinit var adapter: OnboardAdapter
    private var currentPage = 0

    override fun initViews() {
        adapter = OnboardAdapter(questions) { _, _, _ ->
            updateNextButton()
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.layoutManager = LinearLayoutManager(this)

        updateProgress()
        updateNextButton()

        binding.btnNext.setOnClickListener {
            if (currentPage < questions.size - 1) {
                currentPage++
                binding.viewPager.scrollToPosition(currentPage)
                updateProgress()
                updateNextButton()
            } else {
                savePreferencesAndFinish()
            }
        }
    }

    private fun updateProgress() {
        val progress = ((currentPage + 1) * 100 / questions.size)
        binding.progressBar.progress = progress
    }

    private fun updateNextButton() {
        val text = if (currentPage == questions.size - 1) "完成" else "下一步"
        binding.btnNext.text = text

        // Check if any selection made
        val selections = adapter.getSelectedOptions()
        val hasSelection = selections[currentPage].isNotEmpty()
        binding.btnNext.isEnabled = hasSelection
        binding.btnNext.alpha = if (hasSelection) 1.0f else 0.5f
    }

    private fun savePreferencesAndFinish() {
        val userId = authService.getCurrentUserId() ?: return
        val selections = adapter.getSelectedOptions()

        // Calculate initial category weights based on selection
        val categoryWeights = mutableMapOf<String, Double>()
        // Default all to 3
        listOf(
            Deal.CATEGORY_MILK_TEA,
            Deal.CATEGORY_RESTAURANT,
            Deal.CATEGORY_BANK,
            Deal.CATEGORY_ONLINE_SHOP,
            Deal.CATEGORY_FOOD_DRINK,
            Deal.CATEGORY_OTHER
        ).forEach { category ->
            categoryWeights[category] = 3.0
        }
        // Increase weight for selected categories
        val categorySelections = selections[0]
        for (selection in categorySelections) {
            categoryWeights[selection.value] = 8.0
        }

        // Price range
        val priceRange = selections[1].map { it.value }

        // District
        val district = selections[2].firstOrNull()?.value ?: ""
        // Default location around Shenzhen city center
        val lat = 22.5431
        val lng = 114.0579

        val preferences = UserPreference(
            userId = userId,
            categoryWeights = categoryWeights,
            priceRange = priceRange,
            locationLat = lat,
            locationLng = lng,
            district = district,
            preferredBrands = emptyList(),
            dislikedCategories = emptyList(),
            updatedAt = System.currentTimeMillis()
        )

        runBlocking {
            updateUserPreferencesUseCase(preferences)
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
