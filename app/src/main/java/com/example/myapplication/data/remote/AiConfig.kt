package com.example.myapplication.data.remote

import android.os.Build

/**
 * Central configuration for the Python Flask + Ollama AI Ally backend.
 *
 * Supports Android Emulator (http://10.0.2.2:5001/), physical devices on local LAN,
 * and custom runtime overrides without modifying the main PHP backend configuration.
 */
object AiConfig {

    const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2:5001/"
    const val DEFAULT_DEVICE_LAN_BASE_URL = "http://172.23.176.205:5001/"

    // Extended timeouts for CPU-based Ollama execution of Qwen 3 (4B)
    // 660s provides a safe 60s buffer over the server-side 600s generation timeout
    const val CONNECT_TIMEOUT_SECONDS = 60L
    const val READ_TIMEOUT_SECONDS = 660L
    const val WRITE_TIMEOUT_SECONDS = 60L
    const val CALL_TIMEOUT_SECONDS = 660L

    /**
     * Optional custom base URL override (e.g., for physical device testing or deployment).
     * Set this value programmatically to point to a specific host/port.
     */
    var customBaseUrl: String? = null

    /**
     * Detects if the current environment is an Android Emulator.
     * Uses null-safe properties so JVM unit tests do not throw NullPointerException.
     */
    fun isEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT ?: ""
        val model = Build.MODEL ?: ""
        val manufacturer = Build.MANUFACTURER ?: ""
        val hardware = Build.HARDWARE ?: ""
        val product = Build.PRODUCT ?: ""

        return (
            fingerprint.startsWith("generic") ||
                fingerprint.startsWith("unknown") ||
                model.contains("google_sdk") ||
                model.contains("Emulator") ||
                model.contains("Android SDK built for x86") ||
                manufacturer.contains("Genymotion") ||
                hardware.contains("goldfish") ||
                hardware.contains("ranchu") ||
                product.contains("sdk_gphone") ||
                product.contains("sdk")
        )
    }

    /**
     * Resolves the active base URL for connecting to the Python Flask backend.
     */
    fun getResolvedBaseUrl(): String {
        val override = customBaseUrl?.trim()
        if (!override.isNullOrBlank()) {
            return if (override.endsWith("/")) override else "$override/"
        }

        return if (isEmulator()) {
            DEFAULT_EMULATOR_BASE_URL
        } else {
            DEFAULT_DEVICE_LAN_BASE_URL
        }
    }
}
