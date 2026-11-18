package com.example.data.security

import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory sliding-window rate limiter to protect against brute-force attacks
 * on authentication, SMS OTP requests, and sensitive endpoints.
 */
object AuthRateLimiter {

    private val attemptHistory = ConcurrentHashMap<String, MutableList<Long>>()

    /**
     * Checks if an action is permitted within the rate limit.
     * @param key Unique identifier (e.g., action type or phone number)
     * @param maxAttempts Maximum permitted attempts in the time window
     * @param windowMs Time window in milliseconds (default: 60,000ms = 1 minute)
     */
    @Synchronized
    fun isAllowed(key: String, maxAttempts: Int = 5, windowMs: Long = 60_000L): Boolean {
        val now = System.currentTimeMillis()
        val history = attemptHistory.getOrPut(key) { mutableListOf() }

        // Remove attempts older than the window
        history.removeAll { timestamp -> (now - timestamp) > windowMs }

        if (history.size >= maxAttempts) {
            return false // Rate limit exceeded
        }

        history.add(now)
        return true
    }

    /**
     * Returns remaining cooldown in seconds if rate-limited, or 0 if allowed.
     */
    @Synchronized
    fun getCooldownSeconds(key: String, maxAttempts: Int = 5, windowMs: Long = 60_000L): Int {
        val now = System.currentTimeMillis()
        val history = attemptHistory[key] ?: return 0
        history.removeAll { timestamp -> (now - timestamp) > windowMs }
        if (history.size < maxAttempts) return 0
        val oldest = history.firstOrNull() ?: return 0
        val remainingMs = windowMs - (now - oldest)
        return if (remainingMs > 0) (remainingMs / 1000).toInt() + 1 else 0
    }

    @Synchronized
    fun reset(key: String) {
        attemptHistory.remove(key)
    }
}
