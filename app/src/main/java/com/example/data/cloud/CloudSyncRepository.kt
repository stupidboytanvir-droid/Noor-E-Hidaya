package com.example.data.cloud

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.auth.UserSession
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.HadithBookmarkEntity
import com.example.data.local.DebtLedgerEntity
import com.example.data.preference.AppPreferences
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Success(
        val lastSyncedAt: Long,
        val restoredBookmarks: Int = 0,
        val restoredDuas: Int = 0,
        val restoredHadiths: Int = 0,
        val restoredDebts: Int = 0,
        val restoredCustomZikrs: Int = 0
    ) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}

class CloudSyncRepository(
    private val context: Context,
    private val appPreferences: AppPreferences,
    private val database: AppDatabase
) {
    companion object {
        private const val TAG = "CloudSyncRepository"
        private val accountSwitchMutex = Mutex()
    }

    private val db: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            val databaseId = context.getString(R.string.firestore_database_id)
            FirebaseFirestore.getInstance(FirebaseApp.getInstance(), databaseId)
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseFirestore initialization failed: ${e.message}")
            null
        }
    }

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    fun triggerUnauthenticatedStatus(message: String) {
        _syncStatus.value = SyncStatus.Error(message)
    }

    private val bgScope = CoroutineScope(Dispatchers.IO)

    /**
     * Resolves currently active user ID strictly from FirebaseAuth to adhere to security rules.
     * Returns null if unauthenticated so Firestore is never queried without valid credentials.
     */
    suspend fun getActiveUserId(): String? {
        val fbUser = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        } catch (e: Exception) {
            null
        }
        if (fbUser != null && fbUser.uid.isNotEmpty()) {
            return fbUser.uid
        }
        return null
    }

    /**
     * Zero-click real-time background sync for Quran bookmarks
     */
    fun autoSyncBookmark(bookmark: BookmarkEntity, isAdded: Boolean) {
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            if (isAdded) {
                syncBookmarkToCloud(uid, bookmark)
            } else {
                removeBookmarkFromCloud(uid, bookmark.key)
            }
        }
    }

    /**
     * Zero-click real-time background sync for Hadith bookmarks
     */
    fun autoSyncHadithBookmark(bookmark: HadithBookmarkEntity, isAdded: Boolean) {
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            if (isAdded) {
                syncHadithBookmarkToCloud(uid, bookmark)
            } else {
                removeHadithBookmarkFromCloud(uid, bookmark.hadithId)
            }
        }
    }

    /**
     * Zero-click real-time background sync for Favorite Duas
     */
    fun autoSyncFavoriteDua(duaId: String, isAdded: Boolean) {
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            if (isAdded) {
                syncFavoriteDuaToCloud(uid, duaId)
            } else {
                removeFavoriteDuaFromCloud(uid, duaId)
            }
        }
    }

    /**
     * Zero-click real-time background sync for Debt Ledger entries
     */
    fun autoSyncDebtEntry(entry: DebtLedgerEntity) {
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            syncDebtEntryToCloud(uid, entry)
        }
    }

    /**
     * Zero-click real-time background removal for Debt Ledger entry
     */
    fun autoRemoveDebtEntry(debtId: String) {
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            removeDebtEntryFromCloud(uid, debtId)
        }
    }

    /**
     * Zero-click real-time background sync for today's 14 Amals
     */
    fun autoSyncTodayAmal(dateString: String, amals: Set<String>) {
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            syncAmalRecordToCloud(uid, dateString, amals)
        }
    }

    /**
     * Zero-click real-time background sync for spiritual stats and preferences
     */
    fun autoSyncSpiritualStats() {
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            val user = UserSession(
                uid = uid,
                displayName = appPreferences.userName.first().ifEmpty { "সম্মানিত দ্বীনি ভাই/বোন" },
                email = appPreferences.userEmail.first(),
                phoneNumber = appPreferences.userPhone.first(),
                photoUrl = appPreferences.userPhotoUrl.first(),
                isLoggedIn = true
            )
            syncProfileAndStats(user)
        }
    }

    /**
     * Zero-click real-time background sync for monthly worship analytics
     */
    fun autoSyncMonthlyWorship(entity: com.example.data.local.MonthlyWorshipEntity) {
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            syncMonthlyWorshipToCloud(uid, entity)
        }
    }

    /**
     * Zero-click real-time background sync for Daily 5-Prayer Amal tracker
     */
    fun autoSyncDailyPrayerAmal(amal: com.example.data.local.AmalEntity) {
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            syncDailyPrayerAmalToCloud(uid, amal)
        }
    }

    suspend fun syncDailyPrayerAmalToCloud(
        userId: String,
        amal: com.example.data.local.AmalEntity
    ) {
        val firestore = db ?: return
        if (userId.isEmpty() || amal.dateString.isBlank()) return

        try {
            val data = hashMapOf(
                "date" to amal.dateString,
                "fajr" to amal.fajr,
                "dhuhr" to amal.dhuhr,
                "asr" to amal.asr,
                "maghrib" to amal.maghrib,
                "isha" to amal.isha,
                "tahajjud" to amal.tahajjud,
                "surahKahf" to amal.surahKahf,
                "tasbeehDone" to amal.tasbeehDone,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(userId)
                .collection("amalRecords")
                .document(amal.dateString)
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync daily prayer amal: ${e.message}", e)
        }
    }

    /**
     * Zero-click real-time background sync for ALL Tasbeeh / Dhikr counters (built-in presets & custom)
     */
    fun autoSyncCustomZikr(entity: com.example.data.local.TasbihEntity) {
        autoSyncZikr(entity)
    }

    fun autoSyncZikr(entity: com.example.data.local.TasbihEntity) {
        if (entity.id.isBlank()) return
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            syncCustomZikrToCloud(uid, entity)
        }
    }

    fun autoRemoveCustomZikr(zikrId: String) {
        if (zikrId.isBlank()) return
        bgScope.launch {
            val uid = getActiveUserId() ?: return@launch
            removeCustomZikrFromCloud(uid, zikrId)
        }
    }

    suspend fun syncCustomZikrToCloud(
        userId: String,
        entity: com.example.data.local.TasbihEntity
    ) {
        val firestore = db ?: return
        if (userId.isEmpty() || entity.id.isBlank()) return

        try {
            val safeArabic = entity.arabicPhrase.ifBlank { entity.transliteration }.ifBlank { "سُبْحَانَ اللَّهِ" }.take(500)
            val data = hashMapOf(
                "id" to entity.id.take(128),
                "arabicPhrase" to safeArabic,
                "transliteration" to entity.transliteration.take(500),
                "bengaliMeaning" to entity.bengaliMeaning.take(600),
                "targetCount" to entity.targetCount.coerceIn(1, 100_000),
                "currentCount" to entity.currentCount.coerceAtLeast(0),
                "completedRounds" to entity.completedRounds.coerceAtLeast(0),
                "totalHistoricalCount" to entity.totalHistoricalCount.coerceAtLeast(0L),
                "updatedAt" to entity.updatedAt.coerceAtLeast(0L)
            )
            firestore.collection("users")
                .document(userId)
                .collection("customZikrs")
                .document(entity.id)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Synced Zikr counter: ${entity.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync Zikr counter: ${e.message}", e)
        }
    }

    suspend fun removeCustomZikrFromCloud(userId: String, zikrId: String) {
        val firestore = db ?: return
        if (userId.isEmpty() || zikrId.isEmpty()) return

        try {
            firestore.collection("users")
                .document(userId)
                .collection("customZikrs")
                .document(zikrId)
                .delete()
                .await()
            Log.d(TAG, "Deleted cloud custom Zikr: $zikrId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete custom Zikr: ${e.message}", e)
        }
    }

    /**
     * Synchronize a single month's worship totals to Cloud Firestore
     */
    suspend fun syncMonthlyWorshipToCloud(
        userId: String,
        entity: com.example.data.local.MonthlyWorshipEntity
    ) {
        val firestore = db ?: return
        if (userId.isEmpty() || entity.yearMonth.isEmpty()) return

        try {
            val data = hashMapOf(
                "yearMonth" to entity.yearMonth,
                "quranMinutes" to entity.quranMinutes,
                "duaCount" to entity.duaCount,
                "tasbihCount" to entity.tasbihCount,
                "duroodCount" to entity.duroodCount,
                "hadithMinutes" to entity.hadithMinutes,
                "hadithReadCount" to entity.hadithReadCount,
                "updatedAt" to entity.updatedAt
            )

            firestore.collection("users")
                .document(userId)
                .collection("monthlyHistory")
                .document(entity.yearMonth)
                .set(data, SetOptions.merge())
                .await()

            Log.d(TAG, "Synced monthly worship for ${entity.yearMonth}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync monthly worship: ${e.message}", e)
        }
    }

    /**
     * Synchronize user profile & latest statistics to Firestore
     */
    suspend fun syncProfileAndStats(userSession: UserSession) {
        val firestore = db ?: return
        if (userSession.uid.isEmpty()) return

        try {
            _syncStatus.value = SyncStatus.Syncing

            val quranMins = appPreferences.quranReadMinutesToday.first()
            val duasCount = appPreferences.duasReadCount.first()
            val tasbihCount = appPreferences.totalTasbihCount.first()
            val salawatCount = appPreferences.salawatJumatCount.first()
            val hadithMins = appPreferences.hadithReadMinutes.first()
            val hadithCount = appPreferences.hadithReadCount.first()
            val prayerStreak = appPreferences.prayerStreak.first()
            val savedPhoto = appPreferences.userPhotoUrl.first()?.takeIf { it.isNotBlank() }
            val effectivePhoto = when {
                savedPhoto != null && savedPhoto.startsWith("data:image") -> savedPhoto
                !userSession.photoUrl.isNullOrBlank() -> userSession.photoUrl
                savedPhoto != null -> savedPhoto
                else -> ""
            }
            val settingsMap = hashMapOf<String, Any>(
                "uiLanguage" to appPreferences.uiLanguage.first(),
                "contentLanguage" to appPreferences.contentLanguage.first(),
                "selectedQariId" to appPreferences.selectedQariId.first(),
                "calculationMethod" to appPreferences.calculationMethod.first(),
                "juristicMethod" to appPreferences.juristicMethod.first(),
                "themeMode" to appPreferences.themeMode.first()
            )

            val profileData = hashMapOf(
                "uid" to userSession.uid,
                "displayName" to userSession.displayName.take(120),
                "email" to (userSession.email ?: "").take(200),
                "phoneNumber" to (userSession.phoneNumber ?: "").take(40),
                "photoUrl" to effectivePhoto.take(180000),
                "provider" to userSession.provider.name.take(32),
                "quranReadMinutes" to quranMins.coerceAtLeast(0),
                "duasReadCount" to duasCount.coerceAtLeast(0),
                "totalTasbihCount" to tasbihCount.coerceAtLeast(0),
                "salawatJumatCount" to salawatCount.coerceAtLeast(0),
                "hadithReadMinutes" to hadithMins.coerceAtLeast(0),
                "hadithReadCount" to hadithCount.coerceAtLeast(0),
                "prayerStreak" to prayerStreak.coerceAtLeast(0),
                "settings" to settingsMap,
                "lastSyncedAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userSession.uid)
                .set(profileData, SetOptions.merge())
                .await()

            Log.d(TAG, "Successfully synced profile & stats for user: ${userSession.uid}")
            _syncStatus.value = SyncStatus.Success(System.currentTimeMillis(), 0, 0)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync profile: ${e.message}", e)
            _syncStatus.value = SyncStatus.Error("ক্লাউড সিঙ্ক ব্যর্থ হয়েছে: ${e.localizedMessage}")
        }
    }

    /**
     * Backup a single bookmark to Cloud Firestore
     */
    suspend fun syncBookmarkToCloud(userId: String, bookmark: BookmarkEntity) {
        val firestore = db ?: return
        if (userId.isEmpty()) return

        try {
            val bookmarkData = hashMapOf(
                "bookmarkId" to bookmark.key,
                "surahNumber" to bookmark.surahNumber,
                "ayahNumber" to bookmark.ayahNumber,
                "surahNameArabic" to bookmark.surahNameArabic,
                "surahNameEnglish" to bookmark.surahNameEnglish,
                "ayahTextArabic" to bookmark.ayahTextArabic,
                "timestamp" to bookmark.createdAt
            )

            firestore.collection("users")
                .document(userId)
                .collection("bookmarks")
                .document(bookmark.key)
                .set(bookmarkData, SetOptions.merge())
                .await()

            Log.d(TAG, "Backed up bookmark: ${bookmark.key}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to backup bookmark: ${e.message}", e)
        }
    }

    /**
     * Remove bookmark from Cloud Firestore
     */
    suspend fun removeBookmarkFromCloud(userId: String, bookmarkKey: String) {
        val firestore = db ?: return
        if (userId.isEmpty()) return

        try {
            firestore.collection("users")
                .document(userId)
                .collection("bookmarks")
                .document(bookmarkKey)
                .delete()
                .await()
            Log.d(TAG, "Deleted cloud bookmark: $bookmarkKey")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove bookmark from cloud: ${e.message}", e)
        }
    }

    /**
     * Backup favorite Dua ID to Cloud Firestore
     */
    suspend fun syncFavoriteDuaToCloud(userId: String, duaId: String) {
        val firestore = db ?: return
        if (userId.isEmpty()) return

        try {
            val data = hashMapOf(
                "duaId" to duaId,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(userId)
                .collection("favoriteDuas")
                .document(duaId)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Backed up favorite Dua: $duaId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to backup favorite Dua: ${e.message}", e)
        }
    }

    /**
     * Remove favorite Dua from Cloud Firestore
     */
    suspend fun removeFavoriteDuaFromCloud(userId: String, duaId: String) {
        val firestore = db ?: return
        if (userId.isEmpty()) return

        try {
            firestore.collection("users")
                .document(userId)
                .collection("favoriteDuas")
                .document(duaId)
                .delete()
                .await()
            Log.d(TAG, "Deleted favorite Dua from cloud: $duaId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove favorite Dua from cloud: ${e.message}", e)
        }
    }

    /**
     * Full restore: restores bookmarks, favorite Duas, and spiritual stats upon login or reinstall
     */
    /**
     * Backup a single Hadith bookmark to Cloud Firestore
     */
    suspend fun syncHadithBookmarkToCloud(userId: String, bookmark: HadithBookmarkEntity) {
        val firestore = db ?: return
        if (userId.isEmpty()) return

        try {
            val data = hashMapOf(
                "hadithId" to bookmark.hadithId,
                "bookId" to bookmark.bookId,
                "bookName" to bookmark.bookName,
                "chapterName" to bookmark.chapterName,
                "hadithNumber" to bookmark.hadithNumber,
                "narrator" to bookmark.narrator,
                "arabicText" to bookmark.arabicText,
                "banglaText" to bookmark.banglaText,
                "explanation" to bookmark.explanation,
                "grade" to bookmark.grade,
                "timestamp" to bookmark.createdAt
            )

            firestore.collection("users")
                .document(userId)
                .collection("hadithBookmarks")
                .document(bookmark.hadithId)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Backed up Hadith bookmark: ${bookmark.hadithId}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to backup Hadith bookmark: ${e.message}", e)
        }
    }

    /**
     * Remove Hadith bookmark from Cloud Firestore
     */
    suspend fun removeHadithBookmarkFromCloud(userId: String, hadithId: String) {
        val firestore = db ?: return
        if (userId.isEmpty()) return

        try {
            firestore.collection("users")
                .document(userId)
                .collection("hadithBookmarks")
                .document(hadithId)
                .delete()
                .await()
            Log.d(TAG, "Deleted cloud Hadith bookmark: $hadithId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove Hadith bookmark: ${e.message}", e)
        }
    }

    /**
     * Backup a single Debt/Loan ledger entry to Cloud Firestore
     */
    suspend fun syncDebtEntryToCloud(userId: String, entry: DebtLedgerEntity) {
        val firestore = db ?: return
        if (userId.isEmpty()) return

        try {
            val data = hashMapOf(
                "id" to entry.id,
                "userId" to userId,
                "type" to entry.type,
                "personName" to entry.personName,
                "phoneNumber" to entry.phoneNumber,
                "amount" to entry.amount,
                "transactionDate" to entry.transactionDate,
                "dueDate" to (entry.dueDate ?: 0L),
                "notes" to entry.notes,
                "proofAttachmentUri" to (entry.proofAttachmentUri ?: ""),
                "proofAttachmentType" to (entry.proofAttachmentType ?: ""),
                "proofAttachmentNote" to (entry.proofAttachmentNote ?: ""),
                "isSettled" to entry.isSettled,
                "settledDate" to (entry.settledDate ?: 0L),
                "createdAt" to entry.createdAt,
                "updatedAt" to entry.updatedAt
            )

            firestore.collection("users")
                .document(userId)
                .collection("debtLedger")
                .document(entry.id)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Backed up Debt Ledger entry: ${entry.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to backup Debt Ledger entry: ${e.message}", e)
        }
    }

    /**
     * Remove Debt/Loan ledger entry from Cloud Firestore
     */
    suspend fun removeDebtEntryFromCloud(userId: String, debtId: String) {
        val firestore = db ?: return
        if (userId.isEmpty()) return

        try {
            firestore.collection("users")
                .document(userId)
                .collection("debtLedger")
                .document(debtId)
                .delete()
                .await()
            Log.d(TAG, "Deleted cloud Debt Ledger entry: $debtId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove Debt Ledger entry: ${e.message}", e)
        }
    }

    /**
     * Backup daily Amal records to Cloud Firestore
     */
    suspend fun syncAmalRecordToCloud(userId: String, dateString: String, checkedAmals: Set<String>) {
        val firestore = db ?: return
        if (userId.isEmpty()) return

        try {
            val data = hashMapOf(
                "date" to dateString,
                "completedAmals" to checkedAmals.toList(),
                "completedCount" to checkedAmals.size,
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .collection("amalRecords")
                .document(dateString)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Backed up Amal record for date: $dateString")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to backup Amal record: ${e.message}", e)
        }
    }

    /**
     * Clears ALL local user-owned Room tables & DataStore worship stats so one account's data
     * (or Guest mode data) NEVER leaks into a brand-new or different Gmail/Phone account.
     * Note: Bundled read-only tables (e.g. hadiths) and device preferences are untouched.
     */
    suspend fun clearLocalUserData() {
        appPreferences.clearAllUserWorshipData()
        database.monthlyWorshipDao().clearAll()
        database.tasbihDao().clearAll()
        database.amalDao().clearAll()
        database.bookmarkDao().clearAll()
        database.hadithBookmarkDao().clearAll()
        database.debtLedgerDao().clearAll()
    }

    /**
     * Ensures local storage belongs strictly to [targetUid].
     * Returns true if the active owner changed (and local user tables were cleanly wiped to 0/empty).
     */
    suspend fun prepareLocalStorageForAccount(targetUid: String): Boolean {
        val normalizedTarget = targetUid.ifBlank { AppPreferences.GUEST_DATA_OWNER }
        val previousOwner = appPreferences.activeDataOwnerUid.first()
        if (previousOwner != normalizedTarget) {
            Log.d(TAG, "Account owner switched ($previousOwner -> $normalizedTarget): wiping local user worship data.")
            clearLocalUserData()
            appPreferences.setActiveDataOwnerUid(normalizedTarget)
            return true
        }
        return false
    }

    /**
     * Atomically handles sign-in / account switching for Google (Gmail) and Phone accounts:
     * 1. If switching from Guest or another account, wipes ALL local user data to strict 0/empty first.
     * 2. Restores ONLY this account's cloud data from Firestore (exact values, zero cross-account merge).
     * 3. For brand-new accounts (no Firestore doc yet), keeps all local counters strictly at 0 and
     *    initializes a clean zero-valued profile document in Firestore.
     */
    suspend fun switchAccountAndSync(userSession: UserSession) = accountSwitchMutex.withLock {
        if (userSession.uid.isBlank()) return@withLock
        val switched = prepareLocalStorageForAccount(userSession.uid)
        val restoreResult = restoreUserDataFromCloud(userSession.uid, replaceLocal = switched)
        if (restoreResult.isSuccess) {
            syncProfileAndStats(userSession)
        }
    }

    /**
     * Resets all local user-owned data cleanly to 0/empty upon sign-out so the next user
     * or Guest session starts completely fresh.
     */
    suspend fun resetLocalDataOnSignOut() = accountSwitchMutex.withLock {
        clearLocalUserData()
        appPreferences.setActiveDataOwnerUid(AppPreferences.GUEST_DATA_OWNER)
        _syncStatus.value = SyncStatus.Idle
    }

    /**
     * Full restore: restores bookmarks, favorite Duas, Hadiths, Debt Ledger, Amals, Tasbeeh, and spiritual stats.
     * When [replaceLocal] is true (account switch), cloud values strictly replace local values without merging old leftovers.
     */
    suspend fun restoreUserDataFromCloud(userId: String, replaceLocal: Boolean = false): Result<Boolean> {
        val firestore = db ?: return Result.failure(Exception("Firestore not available"))
        val currentAuthUid = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        } catch (_: Exception) { null }

        if (currentAuthUid == null || currentAuthUid != userId || userId.isEmpty()) {
            Log.d(TAG, "Skipping cloud restore: user is not authenticated with Firebase Auth ($currentAuthUid vs $userId)")
            _syncStatus.value = SyncStatus.Idle
            return Result.success(false)
        }

        _syncStatus.value = SyncStatus.Syncing

        return try {
            // Ensure local storage is strictly scoped to this UID even on direct launch/manual restore
            val ownerSwitched = prepareLocalStorageForAccount(userId)
            val strictReplace = replaceLocal || ownerSwitched

            // 1. Restore Profile & Stats
            val userDoc = firestore.collection("users").document(userId).get().await()
            if (userDoc.exists()) {
                val quranMins = userDoc.getLong("quranReadMinutes")?.toInt() ?: 0
                val duasCount = userDoc.getLong("duasReadCount")?.toInt() ?: 0
                val tasbihCount = userDoc.getLong("totalTasbihCount")?.toInt() ?: 0
                val salawatCount = userDoc.getLong("salawatJumatCount")?.toInt() ?: 0
                val hadithMins = userDoc.getLong("hadithReadMinutes")?.toInt() ?: 0
                val hadithCount = userDoc.getLong("hadithReadCount")?.toInt() ?: 0
                val streak = userDoc.getLong("prayerStreak")?.toInt()

                appPreferences.restoreSpiritualStats(
                    quranMins = quranMins,
                    duasCount = duasCount,
                    tasbihCount = tasbihCount,
                    salawatCount = salawatCount,
                    hadithMins = hadithMins,
                    hadithCount = hadithCount,
                    prayerStreak = streak
                )

                val remotePhotoUrl = userDoc.getString("photoUrl")?.takeIf { it.isNotBlank() }
                val localPhotoUrl = appPreferences.userPhotoUrl.first()?.takeIf { it.isNotBlank() }
                if (remotePhotoUrl != null && (remotePhotoUrl.startsWith("data:image") || localPhotoUrl == null)) {
                    appPreferences.setUserPhotoUrl(remotePhotoUrl)
                }

                @Suppress("UNCHECKED_CAST")
                val remoteSettings = userDoc.get("settings") as? Map<String, Any?>
                if (remoteSettings != null) {
                    (remoteSettings["uiLanguage"] as? String)?.takeIf { it.isNotBlank() }?.let { lang ->
                        appPreferences.setUiLanguage(lang)
                    }
                    (remoteSettings["contentLanguage"] as? String)?.takeIf { it.isNotBlank() }?.let { lang ->
                        appPreferences.setContentLanguage(lang)
                    }
                    (remoteSettings["selectedQariId"] as? String)?.takeIf { it.isNotBlank() }?.let { qari ->
                        appPreferences.setSelectedQariId(qari)
                    }
                    (remoteSettings["calculationMethod"] as? String)?.takeIf { it.isNotBlank() }?.let { calc ->
                        appPreferences.setCalculationMethod(calc)
                    }
                    (remoteSettings["juristicMethod"] as? String)?.takeIf { it.isNotBlank() }?.let { juris ->
                        appPreferences.setJuristicMethod(juris)
                    }
                    (remoteSettings["themeMode"] as? String)?.takeIf { it.isNotBlank() }?.let { mode ->
                        appPreferences.setThemeMode(mode)
                    }
                }
            } else {
                // Brand-new account with no Firestore record yet: enforce strict zero counters locally!
                appPreferences.restoreSpiritualStats(
                    quranMins = 0,
                    duasCount = 0,
                    tasbihCount = 0,
                    salawatCount = 0,
                    hadithMins = 0,
                    hadithCount = 0,
                    prayerStreak = 0
                )
            }

            // 2. Restore Quran Bookmarks
            val bookmarksSnapshot = firestore.collection("users")
                .document(userId)
                .collection("bookmarks")
                .get()
                .await()

            var restoredBookmarksCount = 0
            for (doc in bookmarksSnapshot.documents) {
                val key = doc.getString("bookmarkId") ?: doc.id
                val surahNumber = doc.getLong("surahNumber")?.toInt() ?: 1
                val ayahNumber = doc.getLong("ayahNumber")?.toInt() ?: 1
                val surahNameArabic = doc.getString("surahNameArabic") ?: "الفاتحة"
                val surahNameEnglish = doc.getString("surahNameEnglish") ?: "Al-Fatihah"
                val ayahTextArabic = doc.getString("ayahTextArabic") ?: ""
                val createdAt = doc.getLong("timestamp") ?: System.currentTimeMillis()

                val entity = BookmarkEntity(
                    key = key,
                    surahNumber = surahNumber,
                    surahNameArabic = surahNameArabic,
                    surahNameEnglish = surahNameEnglish,
                    ayahNumber = ayahNumber,
                    ayahTextArabic = ayahTextArabic,
                    createdAt = createdAt
                )
                database.bookmarkDao().addBookmark(entity)
                restoredBookmarksCount++
            }

            // 3. Restore Favorite Duas
            val duasSnapshot = firestore.collection("users")
                .document(userId)
                .collection("favoriteDuas")
                .get()
                .await()

            val favoriteIds = duasSnapshot.documents.map { it.id }.toSet()
            if (favoriteIds.isNotEmpty()) {
                appPreferences.setFavoriteDuas(favoriteIds)
            }

            // 4. Restore Hadith Bookmarks
            val hadithSnapshot = firestore.collection("users")
                .document(userId)
                .collection("hadithBookmarks")
                .get()
                .await()

            var restoredHadithsCount = 0
            for (doc in hadithSnapshot.documents) {
                val hadithId = doc.getString("hadithId") ?: doc.id
                val bookId = doc.getString("bookId") ?: "bukhari"
                val bookName = doc.getString("bookName") ?: "সহীহ বুখারী"
                val chapterName = doc.getString("chapterName") ?: "ঈমান"
                val hadithNumber = doc.getString("hadithNumber") ?: "১"
                val narrator = doc.getString("narrator") ?: ""
                val arabicText = doc.getString("arabicText") ?: ""
                val banglaText = doc.getString("banglaText") ?: ""
                val explanation = doc.getString("explanation") ?: ""
                val grade = doc.getString("grade") ?: "সহীহ"
                val createdAt = doc.getLong("timestamp") ?: System.currentTimeMillis()

                val entity = HadithBookmarkEntity(
                    hadithId = hadithId,
                    bookId = bookId,
                    bookName = bookName,
                    chapterName = chapterName,
                    hadithNumber = hadithNumber,
                    narrator = narrator,
                    arabicText = arabicText,
                    banglaText = banglaText,
                    explanation = explanation,
                    grade = grade,
                    createdAt = createdAt
                )
                database.hadithBookmarkDao().addBookmark(entity)
                restoredHadithsCount++
            }

            // 5. Restore Debt Ledger entries
            val debtsSnapshot = firestore.collection("users")
                .document(userId)
                .collection("debtLedger")
                .get()
                .await()

            var restoredDebtsCount = 0
            for (doc in debtsSnapshot.documents) {
                val id = doc.getString("id") ?: doc.id
                val type = doc.getString("type") ?: "GIVE"
                val personName = doc.getString("personName") ?: ""
                val phoneNumber = doc.getString("phoneNumber") ?: ""
                val amount = doc.getDouble("amount") ?: 0.0
                val transactionDate = doc.getLong("transactionDate") ?: System.currentTimeMillis()
                val dueDate = doc.getLong("dueDate")?.takeIf { it > 0 }
                val notes = doc.getString("notes") ?: ""
                val proofUri = doc.getString("proofAttachmentUri")?.takeIf { it.isNotEmpty() }
                val proofType = doc.getString("proofAttachmentType")?.takeIf { it.isNotEmpty() }
                val proofNote = doc.getString("proofAttachmentNote")?.takeIf { it.isNotEmpty() }
                val isSettled = doc.getBoolean("isSettled") ?: false
                val settledDate = doc.getLong("settledDate")?.takeIf { it > 0 }
                val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()

                val entry = DebtLedgerEntity(
                    id = id,
                    userId = userId,
                    type = type,
                    personName = personName,
                    phoneNumber = phoneNumber,
                    amount = amount,
                    transactionDate = transactionDate,
                    dueDate = dueDate,
                    notes = notes,
                    proofAttachmentUri = proofUri,
                    proofAttachmentType = proofType,
                    proofAttachmentNote = proofNote,
                    isSettled = isSettled,
                    settledDate = settledDate,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
                database.debtLedgerDao().insertOrUpdate(entry)
                restoredDebtsCount++
            }

            // 6. Restore Daily Amal & 5-Prayer Tracker Records (last 14 days)
            val todayString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val amalSnapshot = firestore.collection("users")
                .document(userId)
                .collection("amalRecords")
                .get()
                .await()

            for (amalDoc in amalSnapshot.documents) {
                val dateKey = amalDoc.getString("date") ?: amalDoc.id
                if (dateKey == todayString) {
                    @Suppress("UNCHECKED_CAST")
                    val amalsList = amalDoc.get("completedAmals") as? List<String>
                    if (amalsList != null) {
                        appPreferences.setTodayAmals(todayString, amalsList.toSet())
                    }
                }
                val hasPrayerFields = amalDoc.contains("fajr") || amalDoc.contains("dhuhr") ||
                    amalDoc.contains("asr") || amalDoc.contains("maghrib") ||
                    amalDoc.contains("isha") || amalDoc.contains("tahajjud") || amalDoc.contains("surahKahf")
                if (hasPrayerFields && dateKey.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
                    database.amalDao().saveAmal(
                        com.example.data.local.AmalEntity(
                            dateString = dateKey,
                            fajr = amalDoc.getBoolean("fajr") ?: false,
                            dhuhr = amalDoc.getBoolean("dhuhr") ?: false,
                            asr = amalDoc.getBoolean("asr") ?: false,
                            maghrib = amalDoc.getBoolean("maghrib") ?: false,
                            isha = amalDoc.getBoolean("isha") ?: false,
                            tahajjud = amalDoc.getBoolean("tahajjud") ?: false,
                            surahKahf = amalDoc.getBoolean("surahKahf") ?: false,
                            tasbeehDone = amalDoc.getBoolean("tasbeehDone") ?: false
                        )
                    )
                }
            }

            // 7. Restore Monthly Worship History (all 12+ months)
            val monthlySnapshot = firestore.collection("users")
                .document(userId)
                .collection("monthlyHistory")
                .get()
                .await()

            for (doc in monthlySnapshot.documents) {
                val ym = doc.getString("yearMonth") ?: doc.id
                if (ym.matches(Regex("^\\d{4}-\\d{2}$"))) {
                    val existing = if (strictReplace) null else database.monthlyWorshipDao().getMonth(ym)
                    val remoteQuran = doc.getLong("quranMinutes")?.toInt() ?: 0
                    val remoteDua = doc.getLong("duaCount")?.toInt() ?: 0
                    val remoteTasbih = doc.getLong("tasbihCount")?.toInt() ?: 0
                    val remoteDurood = doc.getLong("duroodCount")?.toInt() ?: 0
                    val remoteHadithMins = doc.getLong("hadithMinutes")?.toInt() ?: 0
                    val remoteHadithRead = doc.getLong("hadithReadCount")?.toInt() ?: 0
                    val remoteUpdatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                    val merged = com.example.data.local.MonthlyWorshipEntity(
                        yearMonth = ym,
                        quranMinutes = if (strictReplace) remoteQuran else maxOf(existing?.quranMinutes ?: 0, remoteQuran),
                        duaCount = if (strictReplace) remoteDua else maxOf(existing?.duaCount ?: 0, remoteDua),
                        tasbihCount = if (strictReplace) remoteTasbih else maxOf(existing?.tasbihCount ?: 0, remoteTasbih),
                        duroodCount = if (strictReplace) remoteDurood else maxOf(existing?.duroodCount ?: 0, remoteDurood),
                        hadithMinutes = if (strictReplace) remoteHadithMins else maxOf(existing?.hadithMinutes ?: 0, remoteHadithMins),
                        hadithReadCount = if (strictReplace) remoteHadithRead else maxOf(existing?.hadithReadCount ?: 0, remoteHadithRead),
                        updatedAt = if (strictReplace) remoteUpdatedAt else maxOf(existing?.updatedAt ?: 0L, remoteUpdatedAt)
                    )
                    database.monthlyWorshipDao().upsert(merged)
                }
            }

            // 8. Restore ALL Tasbeeh / Dhikr Counters (built-in presets & custom Zikrs)
            val zikrsSnapshot = firestore.collection("users")
                .document(userId)
                .collection("customZikrs")
                .get()
                .await()

            var restoredCustomZikrsCount = 0
            for (doc in zikrsSnapshot.documents) {
                val id = (doc.getString("id") ?: doc.id).takeIf { it.isNotBlank() } ?: continue
                val arabicPhrase = doc.getString("arabicPhrase")?.takeIf { it.isNotBlank() } ?: continue
                val transliteration = doc.getString("transliteration") ?: ""
                val bengaliMeaning = doc.getString("bengaliMeaning") ?: ""
                val targetCount = (doc.getLong("targetCount")?.toInt() ?: 33).coerceIn(1, 100_000)
                val currentCount = (doc.getLong("currentCount")?.toInt() ?: 0).coerceAtLeast(0)
                val completedRounds = (doc.getLong("completedRounds")?.toInt() ?: 0).coerceAtLeast(0)
                val totalHistoricalCount = (doc.getLong("totalHistoricalCount") ?: 0L).coerceAtLeast(0L)
                val updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()

                database.tasbihDao().insertOrUpdate(
                    com.example.data.local.TasbihEntity(
                        id = id,
                        arabicPhrase = arabicPhrase,
                        transliteration = transliteration,
                        bengaliMeaning = bengaliMeaning,
                        targetCount = targetCount,
                        currentCount = currentCount,
                        completedRounds = completedRounds,
                        totalHistoricalCount = totalHistoricalCount,
                        updatedAt = updatedAt
                    )
                )
                restoredCustomZikrsCount++
            }

            _syncStatus.value = SyncStatus.Success(
                lastSyncedAt = System.currentTimeMillis(),
                restoredBookmarks = restoredBookmarksCount,
                restoredDuas = favoriteIds.size,
                restoredHadiths = restoredHadithsCount,
                restoredDebts = restoredDebtsCount,
                restoredCustomZikrs = restoredCustomZikrsCount
            )
            Log.d(TAG, "Restore successful: $restoredBookmarksCount bookmarks, ${favoriteIds.size} duas, $restoredHadithsCount hadiths, $restoredDebtsCount debts, $restoredCustomZikrsCount custom zikrs")
            Result.success(true)
        } catch (e: Exception) {
            if (e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
                Log.w(TAG, "Cloud restore permission denied (authentication required): ${e.message}")
                _syncStatus.value = SyncStatus.Idle
            } else {
                Log.e(TAG, "Restore from cloud failed: ${e.message}", e)
                _syncStatus.value = SyncStatus.Error("রিস্টোর ব্যর্থ হয়েছে: ${e.localizedMessage}")
            }
            Result.failure(e)
        }
    }

    /**
     * Trigger a complete sync: pushes local bookmarks, favorites, hadiths, debts, and amals to cloud
     */
    suspend fun backupAllLocalData(userId: String) {
        val firestore = db ?: return
        val currentAuthUid = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        } catch (_: Exception) { null }

        if (currentAuthUid == null || currentAuthUid != userId || userId.isEmpty()) {
            Log.d(TAG, "Skipping cloud backup: user is not authenticated with Firebase Auth ($currentAuthUid vs $userId)")
            _syncStatus.value = SyncStatus.Idle
            return
        }

        try {
            _syncStatus.value = SyncStatus.Syncing

            // Push all local bookmarks
            val localBookmarks = database.bookmarkDao().getAllBookmarks().first()
            for (bm in localBookmarks) {
                syncBookmarkToCloud(userId, bm)
            }

            // Push all local favorite Duas
            val localDuas = appPreferences.favoriteDuaIds.first()
            for (duaId in localDuas) {
                syncFavoriteDuaToCloud(userId, duaId)
            }

            // Push all local Hadith bookmarks
            val localHadiths = database.hadithBookmarkDao().getAllBookmarks().first()
            for (hb in localHadiths) {
                syncHadithBookmarkToCloud(userId, hb)
            }

            // Push all local Debt ledger entries
            val localDebts = database.debtLedgerDao().getAllEntries().first()
            for (debt in localDebts) {
                syncDebtEntryToCloud(userId, debt)
            }

            // Push today's Amal record & recent 5-prayer tracker records
            val todayString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val todayAmals = appPreferences.getTodayAmals(todayString).first()
            if (todayAmals.isNotEmpty()) {
                syncAmalRecordToCloud(userId, todayString, todayAmals)
            }
            val recentPrayerAmals = database.amalDao().getAllRecentAmals()
            for (prayerAmal in recentPrayerAmals) {
                syncDailyPrayerAmalToCloud(userId, prayerAmal)
            }

            // Push all monthly worship archive records
            val monthlyRecords = database.monthlyWorshipDao().getAllMonths()
            for (record in monthlyRecords) {
                syncMonthlyWorshipToCloud(userId, record)
            }

            // Push all Tasbeeh / Dhikr counters (built-in presets & custom)
            val allZikrs = database.tasbihDao().getAllCounters().first()
            for (zikr in allZikrs) {
                syncCustomZikrToCloud(userId, zikr)
            }

            _syncStatus.value = SyncStatus.Success(
                lastSyncedAt = System.currentTimeMillis(),
                restoredBookmarks = localBookmarks.size,
                restoredDuas = localDuas.size,
                restoredHadiths = localHadiths.size,
                restoredDebts = localDebts.size,
                restoredCustomZikrs = allZikrs.size
            )
        } catch (e: Exception) {
            Log.e(TAG, "Backup all failed: ${e.message}", e)
            _syncStatus.value = SyncStatus.Error("ক্লাউড ব্যাকআপ ব্যর্থ: ${e.localizedMessage}")
        }
    }
}
