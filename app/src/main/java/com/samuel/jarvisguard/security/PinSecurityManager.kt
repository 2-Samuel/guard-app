package com.samuel.jarvisguard.security

import com.samuel.jarvisguard.data.SecurityPreferences
import com.samuel.jarvisguard.utils.AppConstants
import com.samuel.jarvisguard.utils.SecurityUtils
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PinSecurityManager(
    private val preferences: SecurityPreferences,
    private val secureStorageManager: SecureStorageManager
) {
    sealed class VerificationResult {
        object Success : VerificationResult()
        data class Failure(val remainingAttemptsBeforeLockout: Int, val isLockedOut: Boolean, val lockTimeRemainingSeconds: Int) : VerificationResult()
        data class LockedOut(val lockTimeRemainingSeconds: Int) : VerificationResult()
    }

    fun isConfigured(): Boolean = preferences.hasPin()

    fun isLockedOut(): Boolean {
        val lockUntil = preferences.pinLockUntil
        return System.currentTimeMillis() < lockUntil
    }

    fun getRemainingLockoutSeconds(): Int {
        val lockUntil = preferences.pinLockUntil
        val diff = lockUntil - System.currentTimeMillis()
        return if (diff > 0) (diff / 1000).toInt() + 1 else 0
    }

    fun savePin(pin: String): Boolean {
        if (pin.length < AppConstants.MIN_PIN_LENGTH) return false
        val saltBytes = SecurityUtils.randomBytes(32)
        val hashBytes = hashPin(pin, saltBytes)
        val saltHex = SecurityUtils.toHex(saltBytes)
        val hashHex = SecurityUtils.toHex(hashBytes)

        val encryptedHash = secureStorageManager.encrypt(hashHex)
        preferences.pinSalt = saltHex
        preferences.pinHash = encryptedHash
        preferences.resetPinFailures()
        return true
    }

    fun verifyPin(pin: String): VerificationResult {
        if (isLockedOut()) {
            return VerificationResult.LockedOut(getRemainingLockoutSeconds())
        }

        val storedSaltHex = preferences.pinSalt ?: return VerificationResult.Failure(0, false, 0)
        val encryptedHash = preferences.pinHash ?: return VerificationResult.Failure(0, false, 0)
        val storedHashHex = secureStorageManager.decrypt(encryptedHash)
            ?: return VerificationResult.Failure(0, false, 0)

        val saltBytes = SecurityUtils.fromHex(storedSaltHex)
        val computedHash = hashPin(pin, saltBytes)
        val storedHashBytes = SecurityUtils.fromHex(storedHashHex)

        val isValid = SecurityUtils.constantTimeEquals(computedHash, storedHashBytes)
        return if (isValid) {
            preferences.resetPinFailures()
            VerificationResult.Success
        } else {
            val currentFailures = preferences.pinFailures + 1
            preferences.pinFailures = currentFailures

            var lockDurationMs = 0L
            if (currentFailures >= 7) {
                lockDurationMs = 60_000L // 60 seconds
            } else if (currentFailures >= 5) {
                lockDurationMs = 30_000L // 30 seconds
            } else if (currentFailures >= 3) {
                lockDurationMs = 15_000L // 15 seconds
            }

            if (lockDurationMs > 0) {
                preferences.pinLockUntil = System.currentTimeMillis() + lockDurationMs
                VerificationResult.LockedOut((lockDurationMs / 1000).toInt())
            } else {
                val remaining = 3 - currentFailures
                VerificationResult.Failure(remaining.coerceAtLeast(0), false, 0)
            }
        }
    }

    private fun hashPin(pin: String, salt: ByteArray): ByteArray {
        return try {
            val spec = PBEKeySpec(pin.toCharArray(), salt, 10_000, 256)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            factory.generateSecret(spec).encoded
        } catch (e: Exception) {
            // Fallback to SHA-256 with salt if PBKDF2 is unavailable
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(salt)
            digest.digest(pin.toByteArray(Charsets.UTF_8))
        }
    }
}

