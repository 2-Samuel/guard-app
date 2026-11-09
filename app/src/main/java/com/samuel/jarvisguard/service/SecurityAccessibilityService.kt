package com.samuel.jarvisguard.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.ContextCompat
import com.samuel.jarvisguard.data.SecurityEvent
import com.samuel.jarvisguard.data.SecurityRepository

class SecurityAccessibilityService : AccessibilityService() {

    private lateinit var appLockManager: AppLockManager
    private lateinit var repository: SecurityRepository

    private val screenLockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                repository.sessionManager.onScreenLocked()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        repository = SecurityRepository.getInstance(this)
        appLockManager = AppLockManager(this)

        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        ContextCompat.registerReceiver(
            this,
            screenLockReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        repository.logEvent(
            SecurityEvent.Type.SERVICE_RECOVERY,
            "Accessibility security monitor connected."
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            appLockManager.handleAppSwitch(packageName)
        }
    }

    override fun onInterrupt() {
        // Accessibility service interrupted by system
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching {
            unregisterReceiver(screenLockReceiver)
        }
        repository.logEvent(
            SecurityEvent.Type.ACCESSIBILITY_REVOKED,
            "Accessibility security service was stopped."
        )
    }
}

