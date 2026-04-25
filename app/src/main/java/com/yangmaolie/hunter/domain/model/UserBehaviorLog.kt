package com.yangmaolie.hunter.domain.model

import com.yangmaolie.hunter.data.remote.firebase.FirestoreService

enum class ActionType(
    val weight: Double,
    val halfLifeDays: Int
) {
    VIEW(1.0, 7),
    CLICK(1.0, 7),
    VIEW_LONG(2.0, 14),
    COLLECT(5.0, 30),
    SHARE(8.0, 45),
    DISLIKE(-5.0, 90);

    val decayLambda: Double
        get() = Math.log(2.0) / halfLifeDays
}

data class UserBehaviorLog(
    val id: String,
    val userId: String,
    val dealId: String,
    val category: String,
    val actionType: ActionType,
    val timestamp: Long,
    val durationSeconds: Int
) {
    fun toRemote(): FirestoreService.UserBehaviorLogRemote {
        return FirestoreService.UserBehaviorLogRemote(
            userId = userId,
            dealId = dealId,
            category = category,
            actionType = actionType.name,
            timestamp = timestamp,
            durationSeconds = durationSeconds
        )
    }

    fun calculateWeightedScore(): Double {
        val daysSince = (System.currentTimeMillis() - timestamp) / (1000.0 * 60 * 60 * 24)
        val lambda = actionType.decayLambda
        val decay = Math.exp(-lambda * daysSince)
        return actionType.weight * decay
    }
}
