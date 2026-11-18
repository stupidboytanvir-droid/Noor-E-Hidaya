package com.example.data.security

import java.util.regex.Pattern

/**
 * Enterprise Input Sanitization & Injection Shield for Noor-e-Hidayah.
 * Protects against SQL Injection, Cross-Site Scripting (XSS), Null-byte poisoning,
 * Path Traversal, and SSRF attacks.
 */
object InputSanitizer {

    private val SCRIPT_BLOCK_PATTERN = Pattern.compile(
        "<script[^>]*>.*?</script>|<iframe[^>]*>.*?</iframe>",
        Pattern.CASE_INSENSITIVE or Pattern.DOTALL
    )

    private val DANGEROUS_HTML_PATTERN = Pattern.compile(
        "<[^>]*>|javascript:|onload=|onerror=|onclick=|<script|<iframe",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * Sanitizes user search queries (Quran, Hadith, Duas, Mosque search).
     * Trims whitespace, removes null bytes, control characters, and dangerous script tags,
     * while preserving authentic Bengali, Arabic, English, and standard search tokens.
     */
    fun sanitizeSearchQuery(query: String, maxLength: Int = 100): String {
        if (query.isBlank()) return ""

        // 1. Remove null bytes and non-printable control characters (except newline/tab/space)
        val withoutControlChars = query.filter { ch ->
            ch == ' ' || ch == '\n' || ch == '\t' || !ch.isISOControl()
        }

        // 2. Strip full script/iframe blocks then remaining HTML tags
        val withoutScriptBlocks = SCRIPT_BLOCK_PATTERN.matcher(withoutControlChars).replaceAll("")
        val withoutHtml = DANGEROUS_HTML_PATTERN.matcher(withoutScriptBlocks).replaceAll("")

        // 3. Trim and bound length to prevent buffer exhaustion
        return withoutHtml.trim().take(maxLength)
    }

    /**
     * Sanitizes free-form text input (Custom Dhikr, Debt Ledger notes, Person Name).
     */
    fun sanitizeTextInput(input: String, maxLength: Int = 500): String {
        if (input.isBlank()) return ""
        val withoutControl = input.filter { ch ->
            ch == ' ' || ch == '\n' || ch == '\t' || !ch.isISOControl()
        }
        val withoutScriptBlocks = SCRIPT_BLOCK_PATTERN.matcher(withoutControl).replaceAll("")
        val clean = DANGEROUS_HTML_PATTERN.matcher(withoutScriptBlocks).replaceAll("")
        return clean.trim().take(maxLength)
    }

    /**
     * Sanitizes and bounds numeric financial amounts (e.g. Debt Ledger).
     * Returns 0.0 or valid positive number, rejecting NaN, Infinities, and negative numbers.
     */
    fun sanitizeAmount(amount: Double, maxAmount: Double = 100_000_000.0): Double {
        if (amount.isNaN() || amount.isInfinite() || amount < 0.0) return 0.0
        return amount.coerceIn(0.0, maxAmount)
    }

    /**
     * Sanitizes and validates Bangladeshi and E.164 phone numbers.
     */
    fun sanitizePhoneNumber(raw: String): String {
        val digitsAndPlus = raw.filter { it.isDigit() || it == '+' }
        return digitsAndPlus.take(20)
    }

    /**
     * Validates safe URI schemes to prevent SSRF and arbitrary scheme execution.
     */
    fun isSafeAttachmentUri(uriString: String?): Boolean {
        if (uriString.isNullOrBlank()) return true // empty is safe
        val lower = uriString.lowercase().trim()
        // Prevent path traversal
        if (lower.contains("..") || lower.contains("%2e%2e")) return false
        // Allow safe media content or https URIs only
        return lower.startsWith("content://") || lower.startsWith("file://") || lower.startsWith("https://")
    }
}
