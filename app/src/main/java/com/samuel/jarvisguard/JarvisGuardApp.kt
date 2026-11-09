package com.samuel.jarvisguard

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.samuel.jarvisguard.data.SecurityRepository
import com.samuel.jarvisguard.utils.AppConstants

class JarvisGuardApp : Application() {
    lateinit var repository: SecurityRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = SecurityRepository.getInstance(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConstants.NOTIFICATION_CHANNEL_ID,
                "JARVIS GUARD Security",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "JARVIS GUARD Security and Lock Notifications"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}

