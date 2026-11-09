package com.samuel.jarvisguard.data

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SecurityEvent(
    val type: Type,
    val timestamp: Long,
    val detail: String = ""
) {
    enum class Type {
        AUTH_SUCCESS,
        AUTH_FAILURE,
        PIN_SUCCESS,
        PIN_FAILURE,
        PROTECTION_ENABLED,
        PROTECTION_DISABLED,
        ACCESSIBILITY_REVOKED,
        SERVICE_RECOVERY,
        SYSTEM
    }

    val displayTitle: String
        get() = when (type) {
            Type.AUTH_SUCCESS -> "Biometric Verification Granted"
            Type.AUTH_FAILURE -> "Biometric Verification Failed"
            Type.PIN_SUCCESS -> "PIN Verification Granted"
            Type.PIN_FAILURE -> "Invalid PIN Attempt"
            Type.PROTECTION_ENABLED -> "WhatsApp Protection Activated"
            Type.PROTECTION_DISABLED -> "WhatsApp Protection Deactivated"
            Type.ACCESSIBILITY_REVOKED -> "Accessibility Permission Revoked"
            Type.SERVICE_RECOVERY -> "System Service Recovered"
            Type.SYSTEM -> "System Event"
        }

    val formattedTime: String
        get() = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

    fun toJson(): JSONObject = JSONObject().apply {
        put("type", type.name)
        put("timestamp", timestamp)
        put("detail", detail)
    }

    companion object {
        fun fromJson(json: JSONObject): SecurityEvent = SecurityEvent(
            type = runCatching { Type.valueOf(json.getString("type")) }
                .getOrDefault(Type.SYSTEM),
            timestamp = json.optLong("timestamp", 0L),
            detail = json.optString("detail", "")
        )
    }
}
