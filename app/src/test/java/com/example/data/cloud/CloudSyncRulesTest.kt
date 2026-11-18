package com.example.data.cloud

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudSyncRulesTest : FirestoreEmulatorTestBase() {

    @Test
    fun `unauthenticated reads and writes are denied by security rules`() = runBlocking {
        var readDenied = false
        try {
            withTimeout(OP_TIMEOUT_MS) {
                db.collection("users").document("user_1").get().await()
            }
        } catch (t: Throwable) {
            readDenied = isPermissionDenied(t)
        }
        assertTrue("Unauthenticated read must be denied", readDenied)

        var writeDenied = false
        try {
            withTimeout(OP_TIMEOUT_MS) {
                db.collection("users").document("user_1")
                    .collection("monthlyHistory").document("2026-11")
                    .set(mapOf("yearMonth" to "2026-11", "quranMinutes" to 5))
                    .await()
            }
        } catch (t: Throwable) {
            writeDenied = isPermissionDenied(t)
        }
        assertTrue("Unauthenticated write must be denied", writeDenied)
    }

    @Test
    fun `owner can read and write monthlyHistory while cross-user access is denied`() = runBlocking {
        val ownerUid = signInTestUser("owner_noor@example.com", "TestPass123!")
        val validMonthly = hashMapOf(
            "yearMonth" to "2026-11",
            "quranMinutes" to 15,
            "duaCount" to 10,
            "tasbihCount" to 100,
            "duroodCount" to 33,
            "hadithMinutes" to 12,
            "hadithReadCount" to 4,
            "updatedAt" to 1700000000000L
        )
        withTimeout(OP_TIMEOUT_MS) {
            db.collection("users").document(ownerUid)
                .collection("monthlyHistory").document("2026-11")
                .set(validMonthly)
                .await()
        }

        val snap = withTimeout(OP_TIMEOUT_MS) {
            db.collection("users").document(ownerUid)
                .collection("monthlyHistory").document("2026-11")
                .get()
                .await()
        }
        assertTrue(snap.exists())
        assertEquals(15L, snap.getLong("quranMinutes"))

        // Reject negative counters
        var invalidDenied = false
        try {
            withTimeout(OP_TIMEOUT_MS) {
                db.collection("users").document(ownerUid)
                    .collection("monthlyHistory").document("2026-12")
                    .set(validMonthly + mapOf("yearMonth" to "2026-12", "quranMinutes" to -5))
                    .await()
            }
        } catch (t: Throwable) {
            invalidDenied = isPermissionDenied(t)
        }
        assertTrue("Negative counter must be rejected by rules", invalidDenied)

        // Cross-user read/write denied
        signInTestUser("other_noor@example.com", "TestPass123!")
        var crossReadDenied = false
        try {
            withTimeout(OP_TIMEOUT_MS) {
                db.collection("users").document(ownerUid)
                    .collection("monthlyHistory").document("2026-11")
                    .get()
                    .await()
            }
        } catch (t: Throwable) {
            crossReadDenied = isPermissionDenied(t)
        }
        assertTrue("Cross-user read must be denied", crossReadDenied)
    }

    @Test
    fun `owner can sync customZikrs and profile settings while invalid schema and cross-user access fail`() = runBlocking {
        val ownerUid = signInTestUser("zikr_owner@example.com", "TestPass123!")

        withTimeout(OP_TIMEOUT_MS) {
            db.collection("users").document(ownerUid)
                .set(
                    mapOf(
                        "uid" to ownerUid,
                        "displayName" to "Believer",
                        "photoUrl" to "data:image/jpeg;base64,/9j/4AAQSkZJRg==",
                        "settings" to mapOf("uiLanguage" to "en", "contentLanguage" to "en"),
                        "lastSyncedAt" to 1700000000000L
                    )
                )
                .await()
        }

        val validZikr = mapOf(
            "id" to "custom_1001",
            "arabicPhrase" to "سُبْحَانَ اللَّهِ",
            "transliteration" to "SubhanAllah",
            "bengaliMeaning" to "Glory be to Allah",
            "targetCount" to 33,
            "currentCount" to 3,
            "completedRounds" to 1,
            "totalHistoricalCount" to 36L,
            "updatedAt" to 1700000000000L
        )
        withTimeout(OP_TIMEOUT_MS) {
            db.collection("users").document(ownerUid)
                .collection("customZikrs").document("custom_1001")
                .set(validZikr)
                .await()
        }

        val snap = withTimeout(OP_TIMEOUT_MS) {
            db.collection("users").document(ownerUid)
                .collection("customZikrs").document("custom_1001")
                .get()
                .await()
        }
        assertTrue(snap.exists())
        assertEquals(33L, snap.getLong("targetCount"))

        // Reject invalid targetCount == 0
        var invalidZikrDenied = false
        try {
            withTimeout(OP_TIMEOUT_MS) {
                db.collection("users").document(ownerUid)
                    .collection("customZikrs").document("custom_1002")
                    .set(validZikr + mapOf("id" to "custom_1002", "targetCount" to 0))
                    .await()
            }
        } catch (t: Throwable) {
            invalidZikrDenied = isPermissionDenied(t)
        }
        assertTrue("Invalid targetCount must be denied", invalidZikrDenied)

        // Cross-user customZikr read denied
        signInTestUser("zikr_intruder@example.com", "TestPass123!")
        var crossZikrDenied = false
        try {
            withTimeout(OP_TIMEOUT_MS) {
                db.collection("users").document(ownerUid)
                    .collection("customZikrs").document("custom_1001")
                    .get()
                    .await()
            }
        } catch (t: Throwable) {
            crossZikrDenied = isPermissionDenied(t)
        }
        assertTrue("Cross-user customZikrs read must be denied", crossZikrDenied)
    }

    private companion object {
        const val OP_TIMEOUT_MS = 8000L
    }
}
