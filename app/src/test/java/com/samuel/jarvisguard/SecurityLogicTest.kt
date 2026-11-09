package com.samuel.jarvisguard

import com.samuel.jarvisguard.data.SecurityEvent
import com.samuel.jarvisguard.utils.AppConstants
import com.samuel.jarvisguard.utils.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class SecurityLogicTest {

    @Test
    fun testSecurityUtilsRandomBytesAndHex() {
        val bytes = SecurityUtils.randomBytes(32)
        assertEquals(32, bytes.size)

        val hex = SecurityUtils.toHex(bytes)
        assertEquals(64, hex.length)

        val recovered = SecurityUtils.fromHex(hex)
        assertTrue(SecurityUtils.constantTimeEquals(bytes, recovered))
    }

    @Test
    fun testConstantTimeEquals() {
        val a = "123456".toByteArray()
        val b = "123456".toByteArray()
        val c = "654321".toByteArray()

        assertTrue(SecurityUtils.constantTimeEquals(a, b))
        assertFalse(SecurityUtils.constantTimeEquals(a, c))
    }

    @Test
    fun testSecurityEventJsonSerialization() {
        val original = SecurityEvent(
            type = SecurityEvent.Type.AUTH_SUCCESS,
            timestamp = 1700000000000L,
            detail = "Fingerprint verified"
        )

        val json = original.toJson()
        val restored = SecurityEvent.fromJson(json)

        assertEquals(original.type, restored.type)
        assertEquals(original.timestamp, restored.timestamp)
        assertEquals(original.detail, restored.detail)
        assertEquals("Biometric Verification Granted", restored.displayTitle)
    }

    @Test
    fun testAllSecurityEventTypesHaveDisplayTitles() {
        for (type in SecurityEvent.Type.values()) {
            val event = SecurityEvent(type, 1000L, "test")
            assertNotNull(event.displayTitle)
            assertTrue(event.displayTitle.isNotEmpty())
        }
    }

    @Test
    fun testPinValidationRules() {
        val shortPin = "12345"
        val validPin = "123456"
        val longPin = "12345678"

        assertTrue(shortPin.length < AppConstants.MIN_PIN_LENGTH)
        assertTrue(validPin.length >= AppConstants.MIN_PIN_LENGTH)
        assertTrue(longPin.length >= AppConstants.MIN_PIN_LENGTH)
    }

    @Test
    fun testSaltedHashVerification() {
        val pin = "849201"
        val salt = SecurityUtils.randomBytes(32)

        fun hash(p: String, s: ByteArray): ByteArray {
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update(s)
            return digest.digest(p.toByteArray(Charsets.UTF_8))
        }

        val expectedHash = hash(pin, salt)
        val verifyHash = hash(pin, salt)
        val wrongHash = hash("000000", salt)

        assertTrue(SecurityUtils.constantTimeEquals(expectedHash, verifyHash))
        assertFalse(SecurityUtils.constantTimeEquals(expectedHash, wrongHash))
    }
}

