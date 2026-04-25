package com.yangmaolie.hunter.domain.repository

import com.yangmaolie.hunter.domain.model.Deal

interface DealRepository {
    suspend fun getAllApprovedDeals(): List<Deal>
    suspend fun getDealsByCategory(category: String): List<Deal>
    suspend fun getUpcomingDeals(): List<Deal>
    suspend fun getDealById(dealId: String): Deal?
    suspend fun getDealsByDateRange(start: Long, end: Long): List<Deal>
}
