package com.yangmaolie.hunter.domain.repository

import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.domain.model.UserBehaviorLog
import com.yangmaolie.hunter.domain.model.UserPreference

interface RecommendRepository {
    suspend fun getUserPreferences(userId: String): UserPreference?
    suspend fun saveUserPreferences(preferences: UserPreference): Boolean
    suspend fun calculateUserPreferencesFromLogs(userId: String): UserPreference
    suspend fun logUserBehavior(log: UserBehaviorLog): Boolean
    suspend fun getUserBehaviorLogs(userId: String): List<UserBehaviorLog>
    suspend fun getPersonalizedRecommendations(
        userId: String,
        allDeals: List<Deal>,
        limit: Int
    ): List<Deal>
}
