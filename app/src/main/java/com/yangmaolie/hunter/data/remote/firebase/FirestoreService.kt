package com.yangmaolie.hunter.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.yangmaolie.hunter.domain.model.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor() {

    private val db: FirebaseFirestore? by lazy {
        try {
            Firebase.firestore
        } catch (e: Exception) {
            null
        }
    }
    private val storage: FirebaseStorage? by lazy {
        try {
            FirebaseStorage.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        const val COLLECTION_DEALS = "deals"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_USER_PREFERENCES = "user_preferences"
        const val COLLECTION_USER_BEHAVIOR_LOGS = "user_behavior_logs"
        const val COLLECTION_USER_COLLECTIONS = "user_collections"
        const val COLLECTION_USER_REMINDERS = "user_reminders"
    }

    suspend fun getApprovedDeals(): List<Deal> {
        return try {
            db?.let { db ->
                val snapshot = db.collection(COLLECTION_DEALS)
                    .whereEqualTo("status", "approved")
                    .orderBy("publishTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DealRemote::class.java)?.toDomain(doc.id)
                }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDealsByCategory(category: String): List<Deal> {
        return try {
            db?.let { db ->
                val snapshot = db.collection(COLLECTION_DEALS)
                    .whereEqualTo("status", "approved")
                    .whereEqualTo("category", category)
                    .orderBy("publishTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DealRemote::class.java)?.toDomain(doc.id)
                }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUpcomingDeals(): List<Deal> {
        val now = System.currentTimeMillis()
        return try {
            db?.let { db ->
                val snapshot = db.collection(COLLECTION_DEALS)
                    .whereEqualTo("status", "approved")
                    .whereEqualTo("isUpcoming", true)
                    .whereGreaterThan("startTime", now)
                    .orderBy("startTime", com.google.firebase.firestore.Query.Direction.ASCENDING)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DealRemote::class.java)?.toDomain(doc.id)
                }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDealById(dealId: String): Deal? {
        return try {
            db?.let { db ->
                val doc = db.collection(COLLECTION_DEALS).document(dealId).get().await()
                doc.toObject(DealRemote::class.java)?.toDomain(doc.id)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserPreferences(userId: String, preferences: UserPreference): Boolean {
        return try {
            db?.let { db ->
                val data = preferences.toRemote()
                db.collection(COLLECTION_USER_PREFERENCES)
                    .document(userId)
                    .set(data)
                    .await()
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserPreferences(userId: String): UserPreference? {
        return try {
            db?.let { db ->
                val doc = db.collection(COLLECTION_USER_PREFERENCES)
                    .document(userId)
                    .get()
                    .await()
                doc.toObject(UserPreferenceRemote::class.java)?.toDomain(userId)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun logUserBehavior(log: UserBehaviorLog): Boolean {
        return try {
            db?.let { db ->
                db.collection(COLLECTION_USER_BEHAVIOR_LOGS)
                    .add(log.toRemote())
                    .await()
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserBehaviorLogs(userId: String): List<UserBehaviorLog> {
        return try {
            db?.let { db ->
                val snapshot = db.collection(COLLECTION_USER_BEHAVIOR_LOGS)
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(UserBehaviorLogRemote::class.java)?.toDomain(doc.id)
                }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToCollection(userId: String, dealId: String): Boolean {
        return try {
            db?.let { db ->
                val data = mapOf(
                    "userId" to userId,
                    "dealId" to dealId,
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection(COLLECTION_USER_COLLECTIONS)
                    .add(data)
                    .await()
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFromCollection(userId: String, dealId: String): Boolean {
        return try {
            db?.let { db ->
                val snapshot = db.collection(COLLECTION_USER_COLLECTIONS)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("dealId", dealId)
                    .get()
                    .await()

                for (doc in snapshot.documents) {
                    doc.reference.delete().await()
                }
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isCollected(userId: String, dealId: String): Boolean {
        return try {
            db?.let { db ->
                val snapshot = db.collection(COLLECTION_USER_COLLECTIONS)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("dealId", dealId)
                    .get()
                    .await()
                !snapshot.isEmpty
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserCollections(userId: String): List<String> {
        return try {
            db?.let { db ->
                val snapshot = db.collection(COLLECTION_USER_COLLECTIONS)
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                snapshot.documents.mapNotNull { it.getString("dealId") }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateFcmToken(userId: String, token: String): Boolean {
        return try {
            db?.let { db ->
                db.collection(COLLECTION_USERS)
                    .document(userId)
                    .update("fcmToken", token)
                    .await()
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    // Remote data classes
    data class DealRemote(
        val title: String = "",
        val description: String = "",
        val category: String = "",
        val subCategory: String = "",
        val dealType: String = "",
        val originalPrice: Double = 0.0,
        val dealPrice: Double = 0.0,
        val discount: Int = 0,
        val publishTime: Long = 0,
        val startTime: Long = 0,
        val endTime: Long = 0,
        val isUpcoming: Boolean = false,
        val remindBefore: Int = 15,
        val isOnline: Boolean = false,
        val location: GeoPoint? = null,
        val address: String = "",
        val district: String = "",
        val brandName: String = "",
        val usageRules: List<String> = emptyList(),
        val actionUrl: String = "",
        val qrCodeUrl: String = "",
        val images: List<String> = emptyList(),
        val coverImage: String = "",
        val viewCount: Int = 0,
        val clickCount: Int = 0,
        val collectCount: Int = 0,
        val popularity: Double = 0.0,
        val source: String = "",
        val status: String = "pending"
    ) {
        fun toDomain(id: String): Deal {
            return Deal(
                dealId = id,
                title = title,
                description = description,
                category = category,
                subCategory = subCategory,
                dealType = dealType,
                originalPrice = originalPrice,
                dealPrice = dealPrice,
                discount = discount,
                publishTime = publishTime,
                startTime = startTime,
                endTime = endTime,
                isUpcoming = isUpcoming,
                remindBefore = remindBefore,
                isOnline = isOnline,
                locationLat = location?.latitude,
                locationLng = location?.longitude,
                address = address,
                district = district,
                brandName = brandName,
                usageRules = usageRules,
                actionUrl = actionUrl,
                qrCodeUrl = qrCodeUrl,
                images = images,
                coverImage = coverImage,
                viewCount = viewCount,
                clickCount = clickCount,
                collectCount = collectCount,
                popularity = popularity,
                source = source,
                status = status
            )
        }
    }

    data class UserPreferenceRemote(
        val userId: String = "",
        val categoryWeights: Map<String, Double> = emptyMap(),
        val priceRange: List<String> = emptyList(),
        val locationLat: Double = 22.5431,
        val locationLng: Double = 114.0579,
        val district: String = "",
        val preferredBrands: List<String> = emptyList(),
        val dislikedCategories: List<String> = emptyList(),
        val updatedAt: Long = 0
    ) {
        fun toDomain(id: String): UserPreference {
            return UserPreference(
                userId = id,
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
    }

    data class UserBehaviorLogRemote(
        val userId: String = "",
        val dealId: String = "",
        val category: String = "",
        val actionType: String = "",
        val timestamp: Long = 0,
        val durationSeconds: Int = 0
    ) {
        fun toDomain(id: String): UserBehaviorLog {
            return UserBehaviorLog(
                id = id,
                userId = userId,
                dealId = dealId,
                category = category,
                actionType = ActionType.valueOf(actionType),
                timestamp = timestamp,
                durationSeconds = durationSeconds
            )
        }
    }
}
