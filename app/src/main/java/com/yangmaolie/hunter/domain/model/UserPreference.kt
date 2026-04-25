package com.yangmaolie.hunter.domain.model

import com.yangmaolie.hunter.data.remote.firebase.FirestoreService

data class UserPreference(
    val userId: String,
    val categoryWeights: Map<String, Double>,
    val priceRange: List<String>,
    val locationLat: Double,
    val locationLng: Double,
    val district: String,
    val preferredBrands: List<String>,
    val dislikedCategories: List<String>,
    val updatedAt: Long
) {
    fun toRemote(): FirestoreService.UserPreferenceRemote {
        return FirestoreService.UserPreferenceRemote(
            userId = userId,
            categoryWeights = categoryWeights,
            priceRange = priceRange,
            locationLat = locationLat,
            locationLng = locationLng,
            district = district,
            preferredBrands = preferredBrands,
            dislikedCategories = dislikedCategories,
            updatedAt = updatedAt
        )
    }

    fun getWeightForCategory(category: String): Double {
        return categoryWeights[category] ?: 5.0
    }

    fun isDisliked(category: String): Boolean {
        return category in dislikedCategories
    }

    companion object {
        fun createDefault(userId: String): UserPreference {
            val defaultWeights = mapOf(
                Deal.CATEGORY_MILK_TEA to 5.0,
                Deal.CATEGORY_RESTAURANT to 5.0,
                Deal.CATEGORY_BANK to 5.0,
                Deal.CATEGORY_ONLINE_SHOP to 5.0,
                Deal.CATEGORY_FOOD_DRINK to 5.0,
                Deal.CATEGORY_OTHER to 5.0
            )
            return UserPreference(
                userId = userId,
                categoryWeights = defaultWeights,
                priceRange = listOf("low", "medium"),
                locationLat = 22.5431,
                locationLng = 114.0579,
                district = "",
                preferredBrands = emptyList(),
                dislikedCategories = emptyList(),
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
