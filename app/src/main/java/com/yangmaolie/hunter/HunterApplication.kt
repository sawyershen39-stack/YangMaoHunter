package com.yangmaolie.hunter

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HunterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Catch exception for offline/network-constrained environments
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Firebase initialization failed (likely network issue in mainland China)
            // App will still start but backend won't work
        }
    }
}
