package com.yangmaolie.hunter.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor() {

    private val auth: FirebaseAuth by lazy {
        try {
            Firebase.auth
        } catch (e: Exception) {
            // Firebase not initialized, will return null user
            FakeFirebaseAuth()
        }
    }

    suspend fun signInAnonymously(): Result<String> {
        return try {
            if (auth is FakeFirebaseAuth) {
                Result.failure(Exception("Firebase 初始化失败 - 请检查网络连接"))
            } else {
                val result = auth.signInAnonymously().await()
                val userId = result.user?.uid ?: ""
                Result.success(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? {
        return try {
            auth.currentUser?.uid
        } catch (e: Exception) {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return try {
            auth.currentUser != null
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Fake implementation when Firebase is not available
 */
private class FakeFirebaseAuth : FirebaseAuth() {
    // Everything returns null/offline
}
