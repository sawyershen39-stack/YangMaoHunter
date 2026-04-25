package com.yangmaolie.hunter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HunterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Do NOT initialize Firebase at all - we use fully offline mode
        // This prevents any crash during startup regardless of network configuration
    }
}
