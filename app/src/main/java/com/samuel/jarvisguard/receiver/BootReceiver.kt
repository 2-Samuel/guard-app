package com.samuel.jarvisguard.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.samuel.jarvisguard.data.SecurityEvent
import com.samuel.jarvisguard.data.SecurityRepository

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository = SecurityRepository.getInstance(context)
            repository.sessionManager.invalidateSession()
            repository.logEvent(SecurityEvent.Type.SERVICE_RECOVERY, "System boot completed. Security gate active.")
        }
    }
}

