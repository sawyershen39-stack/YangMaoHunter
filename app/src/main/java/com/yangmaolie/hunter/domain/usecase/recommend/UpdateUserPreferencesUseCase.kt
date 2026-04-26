package com.yangmaolie.hunter.domain.usecase.recommend

import com.yangmaolie.hunter.domain.model.UserPreference
import com.yangmaolie.hunter.domain.repository.RecommendRepository

class UpdateUserPreferencesUseCase(
    private val recommendRepository: RecommendRepository
) {
    suspend operator fun invoke(preferences: UserPreference): Result<Boolean> {
        return try {
            val result = recommendRepository.saveUserPreferences(preferences)
            Result.success(result)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
