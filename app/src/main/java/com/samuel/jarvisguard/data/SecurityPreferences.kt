package com.samuel.jarvisguard.data

import android.content.Context
import android.content.SharedPreferences
import com.samuel.jarvisguard.utils.AppConstants

class SecurityPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(AppConstants.PREFS_STATE, Context.MODE_PRIVATE)

    var isSetupComplete: Boolean
        get() = prefs.getBoolean(AppConstants.KEY_SETUP_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(AppConstants.KEY_SETUP_COMPLETE, value).apply()

    var isProtectionEnabled: Boolean
        get() = prefs.getBoolean(AppConstants.KEY_PROTECTION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(AppConstants.KEY_PROTECTION_ENABLED, value).apply()

    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(AppConstants.KEY_BIOMETRIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(AppConstants.KEY_BIOMETRIC_ENABLED, value).apply()

    var relockGraceMs: Long
        get() = prefs.getLong(AppConstants.KEY_RELOCK_GRACE, AppConstants.DEFAULT_GRACE_PERIOD_MS)
        set(value) = prefs.edit().putLong(AppConstants.KEY_RELOCK_GRACE, value).apply()

    var pinHash: String?
        get() = prefs.getString(AppConstants.KEY_PIN_HASH, null)
        set(value) = prefs.edit().putString(AppConstants.KEY_PIN_HASH, value).apply()

    var pinSalt: String?
        get() = prefs.getString(AppConstants.KEY_PIN_SALT, null)
        set(value) = prefs.edit().putString(AppConstants.KEY_PIN_SALT, value).apply()

    var pinFailures: Int
        get() = prefs.getInt(AppConstants.KEY_PIN_FAILURES, 0)
        set(value) = prefs.edit().putInt(AppConstants.KEY_PIN_FAILURES, value).apply()

    var pinLockUntil: Long
        get() = prefs.getLong(AppConstants.KEY_PIN_LOCK_UNTIL, 0L)
        set(value) = prefs.edit().putLong(AppConstants.KEY_PIN_LOCK_UNTIL, value).apply()

    var eventsJson: String?
        get() = prefs.getString(AppConstants.KEY_EVENTS, null)
        set(value) = prefs.edit().putString(AppConstants.KEY_EVENTS, value).apply()

    var sessionExpiry: Long
        get() = prefs.getLong(AppConstants.KEY_SESSION_EXPIRY, 0L)
        set(value) = prefs.edit().putLong(AppConstants.KEY_SESSION_EXPIRY, value).apply()

    fun hasPin(): Boolean = !pinHash.isNullOrEmpty() && !pinSalt.isNullOrEmpty()

    fun resetPinFailures() {
        prefs.edit()
            .putInt(AppConstants.KEY_PIN_FAILURES, 0)
            .putLong(AppConstants.KEY_PIN_LOCK_UNTIL, 0L)
            .apply()
    }
}
