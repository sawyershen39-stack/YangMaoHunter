package com.yangmaolie.hunter.domain.usecase.recommend

import com.yangmaolie.hunter.domain.model.ActionType
import com.yangmaolie.hunter.domain.model.UserBehaviorLog
import com.yangmaolie.hunter.domain.repository.RecommendRepository
import java.util.UUID

class LogUserBehaviorUseCase(
    private val recommendRepository: RecommendRepository
) {
    suspend operator fun invoke(
        userId: String,
        dealId: String,
        category: String,
        actionType: ActionType,
        durationSeconds: Int = 0
    ): Result<Boolean> {
        return try {
            val log = UserBehaviorLog(
                id = UUID.randomUUID().toString(),
                userId = userId,
                dealId = dealId,
                category = category,
                actionType = actionType,
                timestamp = System.currentTimeMillis(),
                durationSeconds = durationSeconds
            )
            val result = recommendRepository.logUserBehavior(log)
            // 重新计算偏好
            recommendRepository.calculateUserPreferencesFromLogs(userId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
