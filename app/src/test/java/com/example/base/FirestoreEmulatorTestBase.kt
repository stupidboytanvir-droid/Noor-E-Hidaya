package com.example.base

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.R
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
abstract class FirestoreEmulatorTestBase {

    protected lateinit var app: Application
    protected lateinit var auth: FirebaseAuth
    protected lateinit var db: FirebaseFirestore
    protected lateinit var databaseId: String

    @Before
    fun setupEmulator() {
        ShadowLog.stream = System.out
        app = ApplicationProvider.getApplicationContext()
        databaseId = runCatching { app.getString(R.string.firestore_database_id) }
            .getOrDefault("ai-studio-android-noorehid-1ea1b8f1-a66c-485c-ac4f-9fa502fddd8f")

        val existingTestApp = FirebaseApp.getApps(app).firstOrNull { it.name == TEST_APP_NAME }
        val firebaseApp = existingTestApp ?: FirebaseApp.initializeApp(
            app,
            FirebaseOptions.Builder()
                .setApplicationId("com.aistudio.noorehidayah.mvyqwa")
                .setProjectId(PROJECT_ID)
                .setApiKey("fake-api-key-for-emulator")
                .build(),
            TEST_APP_NAME
        ).also { created ->
            val createdDb = FirebaseFirestore.getInstance(created, databaseId)
            createdDb.useEmulator(EMULATOR_HOST, FIRESTORE_PORT)
            createdDb.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .build()

            val createdAuth = FirebaseAuth.getInstance(created)
            createdAuth.useEmulator(EMULATOR_HOST, AUTH_PORT)
        }

        auth = FirebaseAuth.getInstance(firebaseApp)
        db = FirebaseFirestore.getInstance(firebaseApp, databaseId)
        auth.signOut()
    }

    @After
    fun tearDownEmulator() {
        if (::auth.isInitialized) {
            auth.signOut()
        }
    }

    protected fun isPermissionDenied(t: Throwable): Boolean {
        var cur: Throwable? = t
        while (cur != null) {
            if (cur is FirebaseFirestoreException &&
                cur.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
            ) {
                return true
            }
            cur = cur.cause
        }
        return false
    }

    protected suspend fun signInTestUser(email: String, password: String): String =
        withContext(Dispatchers.IO) {
            withTimeout(AUTH_TIMEOUT_MS) {
                val res = try {
                    auth.signInWithEmailAndPassword(email, password).await()
                } catch (_: Exception) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                }
                checkNotNull(res.user?.uid) { "Test user sign-in failed" }
            }
        }

    companion object {
        private const val TEST_APP_NAME = "rules-emulator-test-app"
        private const val EMULATOR_HOST = "127.0.0.1"
        private const val FIRESTORE_PORT = 8085
        private const val AUTH_PORT = 9099
        private const val PROJECT_ID = "gen-lang-client-0043146128"
        private const val AUTH_TIMEOUT_MS = 8000L
    }
}
