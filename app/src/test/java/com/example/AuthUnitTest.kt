package com.example

import com.example.data.auth.AuthProviderType
import com.example.data.auth.UserSession
import org.junit.Assert.*
import org.junit.Test

class AuthUnitTest {

    private fun formatBdPhoneNumber(rawNumber: String): String {
        val cleaned = rawNumber.replace("[^0-9+]".toRegex(), "")
        return when {
            cleaned.startsWith("+880") -> cleaned
            cleaned.startsWith("880") -> "+$cleaned"
            cleaned.startsWith("01") -> "+880${cleaned.substring(1)}"
            cleaned.startsWith("1") && cleaned.length == 10 -> "+880$cleaned"
            else -> "+880$cleaned"
        }
    }

    private fun isValidBdNumber(rawNumber: String): Boolean {
        val formatted = formatBdPhoneNumber(rawNumber)
        return formatted.matches(Regex("^\\+8801[3-9][0-9]{8}$"))
    }

    @Test
    fun testBdPhoneNumberFormatting() {
        assertEquals("+8801712345678", formatBdPhoneNumber("01712345678"))
        assertEquals("+8801712345678", formatBdPhoneNumber("1712345678"))
        assertEquals("+8801712345678", formatBdPhoneNumber("+8801712345678"))
        assertEquals("+8801812345678", formatBdPhoneNumber("8801812345678"))
    }

    @Test
    fun testBdPhoneNumberValidation() {
        assertTrue(isValidBdNumber("01712345678"))
        assertTrue(isValidBdNumber("01312345678"))
        assertTrue(isValidBdNumber("01912345678"))
        assertTrue(isValidBdNumber("+8801812345678"))

        // Invalid numbers
        assertFalse(isValidBdNumber("01212345678")) // operator 2 doesn't exist in BD
        assertFalse(isValidBdNumber("0171234567"))  // too short
        assertFalse(isValidBdNumber("017123456789")) // too long
        assertFalse(isValidBdNumber("abcdefghijk"))
    }

    @Test
    fun testUserSessionCreation() {
        val session = UserSession(
            uid = "test_user_01",
            displayName = "তানভীর আহমেদ (Tanvir Ahmed)",
            email = "stupidboytanvir@gmail.com",
            phoneNumber = "+8801700000000",
            photoUrl = "https://lh3.googleusercontent.com/a/default-user=s256-c",
            provider = AuthProviderType.GOOGLE,
            isLoggedIn = true
        )

        assertEquals("test_user_01", session.uid)
        assertEquals("stupidboytanvir@gmail.com", session.email)
        assertEquals("https://lh3.googleusercontent.com/a/default-user=s256-c", session.photoUrl)
        assertEquals(AuthProviderType.GOOGLE, session.provider)
        assertTrue(session.isLoggedIn)
    }
}
