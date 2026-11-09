package com.samuel.jarvisguard.security

import android.os.SystemClock
import com.samuel.jarvisguard.data.SecurityPreferences
import com.samuel.jarvisguard.utils.AppConstants
import java.util.concurrent.atomic.AtomicBoolean

class AuthenticationSessionManager(
    private val preferences: SecurityPreferences
) {
    private var sessionGrantedUptime: Long = 0L
    private var sessionDurationMs: Long = AppConstants.MAX_SESSION_MS
    private var whatsappExitUptime: Long = 0L

    val isLockActivityActive = AtomicBoolean(false)
    val isAuthPending = AtomicBoolean(false)

    @Synchronized
    fun grantSession(durationMs: Long = AppConstants.MAX_SESSION_MS) {
        sessionGrantedUptime = SystemClock.elapsedRealtime()
        sessionDurationMs = durationMs
        whatsappExitUptime = 0L
        preferences.sessionExpiry = System.currentTimeMillis() + durationMs
    }

    @Synchronized
    fun isSessionValid(): Boolean {
        if (sessionGrantedUptime == 0L) return false
        val elapsed = SystemClock.elapsedRealtime() - sessionGrantedUptime
        return elapsed < sessionDurationMs
    }

    @Synchronized
    fun invalidateSession() {
        sessionGrantedUptime = 0L
        preferences.sessionExpiry = 0L
    }

    @Synchronized
    fun onWhatsAppExited() {
        whatsappExitUptime = SystemClock.elapsedRealtime()
    }

    @Synchronized
    fun onWhatsAppEntered(): Boolean {
        // Returns true if session is still valid (including grace period)
        if (isSessionValid()) {
            val graceMs = preferences.relockGraceMs
            if (graceMs > 0 && whatsappExitUptime > 0L) {
                val timeAway = SystemClock.elapsedRealtime() - whatsappExitUptime
                if (timeAway > graceMs) {
                    invalidateSession()
                    return false
                }
            } else if (graceMs == 0L && whatsappExitUptime > 0L) {
                // Relock immediately upon leaving WhatsApp
                invalidateSession()
                return false
            }
            return true
        }
        return false
    }

    @Synchronized
    fun onScreenLocked() {
        // Relock immediately when device screen is turned off/locked
        invalidateSession()
    }
}

