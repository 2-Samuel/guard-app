package com.samuel.jarvisguard.data

import android.content.Context
import com.samuel.jarvisguard.security.AuthenticationSessionManager
import com.samuel.jarvisguard.security.BiometricAuthManager
import com.samuel.jarvisguard.security.PinSecurityManager
import com.samuel.jarvisguard.security.SecureStorageManager
import com.samuel.jarvisguard.utils.AppConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray

class SecurityRepository(context: Context) {
    val preferences = SecurityPreferences(context)
    val secureStorage = SecureStorageManager()
    val pinSecurityManager = PinSecurityManager(preferences, secureStorage)
    val biometricAuthManager = BiometricAuthManager(context)
    val sessionManager = AuthenticationSessionManager(preferences)

    private val _eventsFlow = MutableStateFlow<List<SecurityEvent>>(emptyList())
    val eventsFlow: StateFlow<List<SecurityEvent>> = _eventsFlow.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        val rawJson = preferences.eventsJson ?: "[]"
        val list = mutableListOf<SecurityEvent>()
        runCatching {
            val array = JSONArray(rawJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(SecurityEvent.fromJson(obj))
            }
        }
        _eventsFlow.value = list.sortedByDescending { it.timestamp }
    }

    @Synchronized
    fun logEvent(type: SecurityEvent.Type, detail: String = "") {
        val event = SecurityEvent(type, System.currentTimeMillis(), detail)
        val currentList = _eventsFlow.value.toMutableList()
        currentList.add(0, event)
        if (currentList.size > AppConstants.MAX_EVENTS) {
            currentList.removeAt(currentList.lastIndex)
        }
        _eventsFlow.value = currentList

        // Persist
        runCatching {
            val array = JSONArray()
            currentList.forEach { array.put(it.toJson()) }
            preferences.eventsJson = array.toString()
        }
    }

    fun clearEvents() {
        preferences.eventsJson = "[]"
        _eventsFlow.value = emptyList()
    }

    companion object {
        @Volatile
        private var instance: SecurityRepository? = null

        fun getInstance(context: Context): SecurityRepository {
            return instance ?: synchronized(this) {
                instance ?: SecurityRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}

