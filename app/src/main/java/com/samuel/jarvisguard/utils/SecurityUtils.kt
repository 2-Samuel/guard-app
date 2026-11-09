package com.samuel.jarvisguard.utils

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtils {
    private val random = SecureRandom()
    private val HEX_CHARS = "0123456789abcdef".toCharArray()

    fun randomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        random.nextBytes(bytes)
        return bytes
    }

    fun toHex(bytes: ByteArray): String {
        val builder = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            val value = b.toInt() and 0xFF
            builder.append(HEX_CHARS[value ushr 4])
            builder.append(HEX_CHARS[value and 0x0F])
        }
        return builder.toString()
    }

    fun fromHex(hex: String): ByteArray {
        require(hex.length % 2 == 0) { "Invalid hex length" }
        val result = ByteArray(hex.length / 2)
        var index = 0
        while (index < hex.length) {
            val high = Character.digit(hex[index], 16)
            val low = Character.digit(hex[index + 1], 16)
            require(high >= 0 && low >= 0) { "Invalid hex character" }
            result[index / 2] = ((high shl 4) or low).toByte()
            index += 2
        }
        return result
    }

    fun constantTimeEquals(first: ByteArray, second: ByteArray): Boolean =
        MessageDigest.isEqual(first, second)
}
