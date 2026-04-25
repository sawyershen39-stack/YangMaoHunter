package com.yangmaolie.hunter.data.repository

import com.yangmaolie.hunter.data.remote.firebase.FirestoreService
import com.yangmaolie.hunter.domain.model.ActionType
import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.domain.model.UserBehaviorLog
import com.yangmaolie.hunter.domain.model.UserPreference
import com.yangmaolie.hunter.domain.repository.RecommendRepository
import kotlin.math.max
import kotlin.math.min
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RecommendRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
) : RecommendRepository {

    override suspend fun getUserPreferences(userId: String): UserPreference? {
        return firestoreService.getUserPreferences(userId)
    }

    override suspend fun saveUserPreferences(preferences: UserPreference): Boolean {
        return firestoreService.saveUserPreferences(preferences.userId, preferences)
    }

    override suspend fun calculateUserPreferencesFromLogs(userId: String): UserPreference {
        val logs = firestoreService.getUserBehaviorLogs(userId)
        val existing = firestoreService.getUserPreferences(userId)

        // Start with default or existing weights
        val categoryWeights = existing?.categoryWeights?.toMutableMap()
            ?: Deal.ALL_CATEGORIES.associateWith { 5.0 }.toMutableMap()

        // Add weighted contributions from logs
        for (log in logs) {
            val contribution = log.calculateWeightedScore() / 10.0
            val current = categoryWeights[log.category] ?: 5.0
            categoryWeights[log.category] = max(0.0, min(10.0, current + contribution))
        }

        val newPreferences = UserPreference(
            userId = userId,
            categoryWeights = categoryWeights,
            priceRange = existing?.priceRange ?: listOf("low", "medium"),
            locationLat = existing?.locationLat ?: 22.5431,
            locationLng = existing?.locationLng ?: 114.0579,
            district = existing?.district ?: "",
            preferredBrands = existing?.preferredBrands ?: emptyList(),
            dislikedCategories = existing?.dislikedCategories ?: emptyList(),
            updatedAt = System.currentTimeMillis()
        )

        firestoreService.saveUserPreferences(userId, newPreferences)
        return newPreferences
    }

    override suspend fun logUserBehavior(log: UserBehaviorLog): Boolean {
        return firestoreService.logUserBehavior(log)
    }

    override suspend fun getUserBehaviorLogs(userId: String): List<UserBehaviorLog> {
        return firestoreService.getUserBehaviorLogs(userId)
    }

    override suspend fun getPersonalizedRecommendations(
        userId: String,
        allDeals: List<Deal>,
        limit: Int
    ): List<Deal> {
        val preferences = getUserPreferences(userId)
            ?: UserPreference.createDefault(userId)

        // Filter out disliked categories
        val filteredDeals = allDeals.filter { deal ->
            !preferences.isDisliked(deal.category)
        }

        // Multi-channel recall
        val candidates = mutableListOf<ScoredDeal>()

        // 1. Personal recall (based on user preferences) - 60% of candidates
        val personalizedCount = (limit * 0.6).toInt()
        val personalizedCandidates = getPersonalizedCandidates(filteredDeals, preferences, personalizedCount)
        candidates.addAll(personalizedCandidates)

        // 2. Popular recall - 20%
        val popularCount = (limit * 0.2).toInt()
        val popularCandidates = getPopularCandidates(filteredDeals, candidates, popularCount)
        candidates.addAll(popularCandidates)

        // 3. Fresh recall - 10%
        val freshCount = (limit * 0.1).toInt()
        val freshCandidates = getFreshCandidates(filteredDeals, candidates, freshCount)
        candidates.addAll(freshCandidates)

        // 4. Exploration recall (explore new categories) - 10%
        val exploreCount = limit - candidates.size
        if (exploreCount > 0) {
            val exploreCandidates = getExploreCandidates(filteredDeals, candidates, preferences, exploreCount)
            candidates.addAll(exploreCandidates)
        }

        // Re-rank with MMR for diversity
        return rerankWithMMR(candidates, preferences, limit)
    }

    private fun getPersonalizedCandidates(
        deals: List<Deal>,
        preferences: UserPreference,
        count: Int
    ): List<ScoredDeal> {
        return deals
            .map { deal ->
                val categoryScore = preferences.getWeightForCategory(deal.category) / 10.0
                ScoredDeal(deal, categoryScore, 0.0)
            }
            .sortedByDescending { it.rawScore }
            .take(count)
    }

    private fun getPopularCandidates(
        deals: List<Deal>,
        existing: List<ScoredDeal>,
        count: Int
    ): List<ScoredDeal> {
        val existingIds = existing.map { it.deal.dealId }.toSet()
        return deals
            .filterNot { it.dealId in existingIds }
            .map { deal ->
                val popularityScore = (deal.popularity / 100.0).coerceIn(0.0, 1.0)
                ScoredDeal(deal, popularityScore, 0.0)
            }
            .sortedByDescending { it.rawScore }
            .take(count)
    }

    private fun getFreshCandidates(
        deals: List<Deal>,
        existing: List<ScoredDeal>,
        count: Int
    ): List<ScoredDeal> {
        val existingIds = existing.map { it.deal.dealId }.toSet()
        val now = System.currentTimeMillis()
        return deals
            .filterNot { it.dealId in existingIds }
            .map { deal ->
                val hoursOld = (now - deal.publishTime) / (1000.0 * 3600)
                val freshScore = 1.0 / (1.0 + hoursOld / 24.0)
                ScoredDeal(deal, freshScore, 0.0)
            }
            .sortedByDescending { it.rawScore }
            .take(count)
    }

    private fun getExploreCandidates(
        deals: List<Deal>,
        existing: List<ScoredDeal>,
        preferences: UserPreference,
        count: Int
    ): List<ScoredDeal> {
        val existingIds = existing.map { it.deal.dealId }.toSet()
        // Explore categories with low weight to avoid information cocoon
        return deals
            .filterNot { it.dealId in existingIds }
            .map { deal ->
                val categoryWeight = preferences.getWeightForCategory(deal.category)
                // Lower weight = higher exploration score
                val exploreScore = (10.0 - categoryWeight) / 10.0
                // Add some randomness
                val randomFactor = 0.8 + Random.nextDouble() * 0.2
                ScoredDeal(deal, exploreScore * randomFactor, 0.0)
            }
            .sortedByDescending { it.rawScore }
            .take(count)
    }

    private fun rerankWithMMR(
        candidates: List<ScoredDeal>,
        preferences: UserPreference,
        limit: Int
    ): List<Deal> {
        // MMR (Maximal Marginal Relevance) for diversity
        // lambda = 0.7 balances relevance and diversity
        val lambda = 0.7

        val result = mutableListOf<ScoredDeal>()
        val remaining = candidates.toMutableList()

        // Calculate relevance score first
        for (candidate in remaining) {
            candidate.finalScore = calculateFinalScore(candidate.deal, preferences)
        }

        while (result.size < limit && remaining.isNotEmpty()) {
            var bestMmScore = Double.MIN_VALUE
            var bestCandidate: ScoredDeal? = null

            for (candidate in remaining) {
                val relevance = candidate.finalScore

                // Calculate similarity to already selected items
                var maxSim = 0.0
                for (selected in result) {
                    val sim = calculateSimilarity(candidate.deal, selected.deal)
                    if (sim > maxSim) maxSim = sim
                }

                // MMR formula
                val mmScore = lambda * relevance - (1 - lambda) * maxSim

                if (mmScore > bestMmScore) {
                    bestMmScore = mmScore
                    bestCandidate = candidate
                }
            }

            bestCandidate?.let {
                result.add(it)
                remaining.remove(it)
            }
        }

        return result.sortedByDescending { it.finalScore }.map { it.deal }
    }

    private fun calculateFinalScore(deal: Deal, preferences: UserPreference): Double {
        var score = 0.0

        // Category preference (40%)
        val categoryScore = preferences.getWeightForCategory(deal.category) / 10.0
        score += categoryScore * 0.4

        // Popularity (20%)
        val popularityScore = (deal.popularity / 100.0).coerceIn(0.0, 1.0)
        score += popularityScore * 0.2

        // Timeliness (15%) - prefer deals starting soon or ending soon
        val now = System.currentTimeMillis()
        val timeScore = when {
            deal.startTime > now -> {
                // Upcoming deal starting sooner gets higher score
                val hoursToStart = (deal.startTime - now) / (1000.0 * 3600)
                1.0 / (1.0 + hoursToStart / 24.0)
            }
            deal.endTime > now -> {
                // Ongoing deal ending sooner gets higher score
                val hoursToEnd = (deal.endTime - now) / (1000.0 * 3600)
                0.5 + 0.5 / (1.0 + hoursToEnd / 24.0)
            }
            else -> 0.0
        }
        score += timeScore * 0.15

        // Distance (10%) - for offline deals, closer is better
        if (!deal.isOnline && preferences.locationLat != 0.0) {
            val distance = calculateDistance(
                preferences.locationLat, preferences.locationLng,
                deal.locationLat ?: 22.5431, deal.locationLng ?: 114.0579
            )
            // Distance in km, closer = higher score
            val distanceScore = 1.0 / (1.0 + distance / 5.0)
            score += distanceScore * 0.1
        }

        // Brand preference (15%)
        val brandScore = if (deal.brandName in preferences.preferredBrands) 1.0 else 0.3
        score += brandScore * 0.15

        return score
    }

    private fun calculateSimilarity(deal1: Deal, deal2: Deal): Double {
        // Same category = high similarity
        return if (deal1.category == deal2.category) 0.8 else 0.0
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        // Haversine formula, returns km
        val r = 6371 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    data class ScoredDeal(
        val deal: Deal,
        val rawScore: Double,
        var finalScore: Double
    )
}
