package com.yangmaolie.hunter.data.repository

import com.yangmaolie.hunter.data.remote.firebase.FirestoreService
import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.domain.repository.DealRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DealRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
) : DealRepository {

    override suspend fun getAllApprovedDeals(): List<Deal> {
        return firestoreService.getApprovedDeals()
    }

    override suspend fun getDealsByCategory(category: String): List<Deal> {
        return firestoreService.getDealsByCategory(category)
    }

    override suspend fun getUpcomingDeals(): List<Deal> {
        return firestoreService.getUpcomingDeals()
    }

    override suspend fun getDealById(dealId: String): Deal? {
        return firestoreService.getDealById(dealId)
    }

    override suspend fun getDealsByDateRange(start: Long, end: Long): List<Deal> {
        // Filter on client side for simplicity
        return firestoreService.getUpcomingDeals()
            .filter { it.startTime in start..end }
    }
}
