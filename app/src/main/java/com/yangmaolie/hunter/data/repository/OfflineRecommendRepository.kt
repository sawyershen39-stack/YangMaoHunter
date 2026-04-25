package com.yangmaolie.hunter.data.repository

import com.yangmaolie.hunter.domain.model.ActionType
import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.domain.model.UserBehaviorLog
import com.yangmaolie.hunter.domain.model.UserPreference
import com.yangmaolie.hunter.domain.repository.RecommendRepository
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRecommendRepository @Inject constructor() : RecommendRepository {

    private var cachedPreferences: UserPreference? = null
    private val logs = mutableListOf<UserBehaviorLog>()

    override suspend fun getUserPreferences(userId: String): UserPreference? {
        return cachedPreferences
    }

    override suspend fun saveUserPreferences(preferences: UserPreference): Boolean {
        cachedPreferences = preferences
        return true
    }

    override suspend fun calculateUserPreferencesFromLogs(userId: String): UserPreference {
        val existing = cachedPreferences ?: UserPreference.createDefault(userId)
        val categoryWeights = existing.categoryWeights.toMutableMap()

        for (log in logs) {
            val contribution = log.calculateWeightedScore() / 10.0
            val current = categoryWeights[log.category] ?: 5.0
            categoryWeights[log.category] = max(0.0, min(10.0, current + contribution))
        }

        val newPreferences = existing.copy(
            categoryWeights = categoryWeights,
            updatedAt = System.currentTimeMillis()
        )
        cachedPreferences = newPreferences
        return newPreferences
    }

    override suspend fun logUserBehavior(log: UserBehaviorLog): Boolean {
        logs.add(log)
        return true
    }

    override suspend fun getUserBehaviorLogs(userId: String): List<UserBehaviorLog> {
        return logs.filter { it.userId == userId }
    }

    override suspend fun getPersonalizedRecommendations(
        userId: String,
        allDeals: List<Deal>,
        limit: Int
    ): List<Deal> {
        val preferences = cachedPreferences ?: UserPreference.createDefault(userId)

        val filteredDeals = allDeals.filter { deal ->
            !preferences.isDisliked(deal.category)
        }.toMutableList()

        // Shuffle for offline demo
        filteredDeals.shuffle(Random(System.currentTimeMillis()))
        return filteredDeals.take(limit)
    }
}
