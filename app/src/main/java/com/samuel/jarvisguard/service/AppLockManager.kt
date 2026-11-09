package com.samuel.jarvisguard.service

import android.content.Context
import android.content.Intent
import com.samuel.jarvisguard.LockActivity
import com.samuel.jarvisguard.data.SecurityRepository
import com.samuel.jarvisguard.utils.AppConstants

class AppLockManager(private val context: Context) {
    private val repository = SecurityRepository.getInstance(context)

    fun handleAppSwitch(packageName: String) {
        val prefs = repository.preferences
        val sessionManager = repository.sessionManager

        // Ignore our own application so we never lock JARVIS GUARD
        if (packageName == AppConstants.OWN_PACKAGE) {
            return
        }

        // WhatsApp is entering foreground
        if (packageName == AppConstants.PROTECTED_PACKAGE) {
            if (!prefs.isSetupComplete || !prefs.isProtectionEnabled) {
                return
            }

            // Check if current session is still valid
            val isValid = sessionManager.onWhatsAppEntered()
            if (!isValid) {
                launchLockScreen()
            }
        } else {
            // User switched to another app from WhatsApp
            sessionManager.onWhatsAppExited()
        }
    }

    fun launchLockScreen() {
        val sessionManager = repository.sessionManager

        // Prevent duplicate launches if lock activity is already active
        if (sessionManager.isLockActivityActive.get()) {
            return
        }

        sessionManager.isAuthPending.set(true)
        val intent = Intent(context, LockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
        context.startActivity(intent)
    }
}

