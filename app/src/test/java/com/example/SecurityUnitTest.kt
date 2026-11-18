package com.example

import com.example.data.security.AuthRateLimiter
import com.example.data.security.InputSanitizer
import org.junit.Assert.*
import org.junit.Test

class SecurityUnitTest {

    @Test
    fun testSearchQuerySanitization() {
        val maliciousQuery = "  <script>alert('xss')</script>বুখারী ১\u0000; DROP TABLE hadith;-- "
        val cleaned = InputSanitizer.sanitizeSearchQuery(maliciousQuery)

        assertFalse(cleaned.contains("<script>"))
        assertFalse(cleaned.contains("alert"))
        assertFalse(cleaned.contains("\u0000"))
        assertTrue(cleaned.contains("বুখারী ১"))
    }

    @Test
    fun testTextInputSanitization() {
        val dangerousInput = "<iframe src='malicious.com' onload='stealCookies()'></iframe>সুবহানাল্লাহ"
        val cleaned = InputSanitizer.sanitizeTextInput(dangerousInput)

        assertFalse(cleaned.contains("<iframe"))
        assertFalse(cleaned.contains("stealCookies"))
        assertTrue(cleaned.contains("সুবহানাল্লাহ"))
    }

    @Test
    fun testAmountSanitization() {
        assertEquals(0.0, InputSanitizer.sanitizeAmount(-500.0), 0.001)
        assertEquals(0.0, InputSanitizer.sanitizeAmount(Double.NaN), 0.001)
        assertEquals(0.0, InputSanitizer.sanitizeAmount(Double.POSITIVE_INFINITY), 0.001)
        assertEquals(5000.0, InputSanitizer.sanitizeAmount(5000.0), 0.001)
        assertEquals(100_000_000.0, InputSanitizer.sanitizeAmount(999_999_999.0), 0.001)
    }

    @Test
    fun testSafeAttachmentUri() {
        assertTrue(InputSanitizer.isSafeAttachmentUri("content://media/external/images/media/123"))
        assertTrue(InputSanitizer.isSafeAttachmentUri("https://example.com/contract.jpg"))
        assertTrue(InputSanitizer.isSafeAttachmentUri(null))
        assertTrue(InputSanitizer.isSafeAttachmentUri(""))

        // Malicious or SSRF paths
        assertFalse(InputSanitizer.isSafeAttachmentUri("file:///data/data/com.example/databases/../../system"))
        assertFalse(InputSanitizer.isSafeAttachmentUri("javascript:alert(1)"))
        assertFalse(InputSanitizer.isSafeAttachmentUri("intent://scan/#Intent;scheme=zxing;end"))
        assertFalse(InputSanitizer.isSafeAttachmentUri("content://media/../secret"))
    }

    @Test
    fun testAuthRateLimiterThrottling() {
        val testKey = "test_rate_limit_user"
        AuthRateLimiter.reset(testKey)

        // 3 attempts should be allowed for a limit of 3
        assertTrue(AuthRateLimiter.isAllowed(testKey, maxAttempts = 3, windowMs = 5000L))
        assertTrue(AuthRateLimiter.isAllowed(testKey, maxAttempts = 3, windowMs = 5000L))
        assertTrue(AuthRateLimiter.isAllowed(testKey, maxAttempts = 3, windowMs = 5000L))

        // 4th attempt must be blocked
        assertFalse(AuthRateLimiter.isAllowed(testKey, maxAttempts = 3, windowMs = 5000L))
        val cooldown = AuthRateLimiter.getCooldownSeconds(testKey, maxAttempts = 3, windowMs = 5000L)
        assertTrue(cooldown > 0)

        // Reset clears history
        AuthRateLimiter.reset(testKey)
        assertTrue(AuthRateLimiter.isAllowed(testKey, maxAttempts = 3, windowMs = 5000L))
    }
}
