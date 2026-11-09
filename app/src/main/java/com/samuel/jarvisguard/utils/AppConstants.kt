package com.samuel.jarvisguard.utils

object AppConstants {
    const val PROTECTED_PACKAGE = "com.whatsapp"
    const val OWN_PACKAGE = "com.samuel.jarvisguard"

    const val MAX_SESSION_MS = 10 * 60 * 1000L
    const val DEFAULT_GRACE_PERIOD_MS = 0L
    const val MIN_PIN_LENGTH = 6
    const val MAX_EVENTS = 100

    const val PREFS_SECURITY = "jarvis_guard_security"
    const val PREFS_STATE = "jarvis_guard_state"

    const val KEY_PIN_HASH = "pin_hash"
    const val KEY_PIN_SALT = "pin_salt"
    const val KEY_PIN_FAILURES = "pin_failures"
    const val KEY_PIN_LOCK_UNTIL = "pin_lock_until"

    const val KEY_SETUP_COMPLETE = "setup_complete"
    const val KEY_PROTECTION_ENABLED = "protection_enabled"
    const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    const val KEY_RELOCK_GRACE = "relock_grace_ms"
    const val KEY_SESSION_EXPIRY = "session_expiry"

    const val KEY_EVENTS = "events"

    const val NOTIFICATION_CHANNEL_ID = "jarvis_guard_lock"
    const val NOTIFICATION_ID = 1001
}
