package com.yangmaolie.hunter.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor() {

    private val auth: FirebaseAuth? by lazy {
        try {
            Firebase.auth
        } catch (e: Exception) {
            null
        }
    }

    private var offlineUserId: String? = null

    suspend fun signInAnonymously(): Result<String> {
        return try {
            val auth = auth
            if (auth != null) {
                val result = auth.signInAnonymously().await()
                val userId = result.user?.uid ?: ""
                Result.success(userId)
            } else {
                // Offline mode: generate local user ID
                if (offlineUserId == null) {
                    offlineUserId = "offline_${System.currentTimeMillis()}"
                }
                Result.success(offlineUserId!!)
            }
        } catch (e: Exception) {
            // Fallback to offline mode on any error
            if (offlineUserId == null) {
                offlineUserId = "offline_${System.currentTimeMillis()}"
            }
            Result.success(offlineUserId!!)
        }
    }

    fun getCurrentUserId(): String? {
        return try {
            val firebaseId = auth?.currentUser?.uid
            if (firebaseId != null) {
                firebaseId
            } else {
                offlineUserId
            }
        } catch (e: Exception) {
            offlineUserId
        }
    }

    fun isLoggedIn(): Boolean {
        return try {
            if (auth?.currentUser != null) {
                true
            } else {
                offlineUserId != null
            }
        } catch (e: Exception) {
            offlineUserId != null
        }
    }
}
