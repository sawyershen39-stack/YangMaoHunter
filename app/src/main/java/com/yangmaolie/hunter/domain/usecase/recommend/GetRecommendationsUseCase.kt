package com.yangmaolie.hunter.domain.usecase.recommend

import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.domain.repository.DealRepository
import com.yangmaolie.hunter.domain.repository.RecommendRepository

class GetRecommendationsUseCase(
    private val recommendRepository: RecommendRepository,
    private val dealRepository: DealRepository
) {
    suspend operator fun invoke(
        userId: String,
        categoryFilter: String? = null,
        limit: Int = 50
    ): Result<List<Deal>> {
        return try {
            val allDeals = dealRepository.getAllApprovedDeals()
                .filter { !it.isExpired() }
                .let { deals ->
                    if (!categoryFilter.isNullOrEmpty() && categoryFilter != "all") {
                        deals.filter { it.category == categoryFilter }
                    } else {
                        deals
                    }
                }

            val recommendations = recommendRepository.getPersonalizedRecommendations(
                userId, allDeals, limit
            )
            Result.success(recommendations)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
