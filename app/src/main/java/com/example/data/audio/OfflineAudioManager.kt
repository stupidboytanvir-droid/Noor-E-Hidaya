package com.example.data.audio

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.Locale

data class SurahDownloadProgress(
    val qariId: String,
    val surahNumber: Int,
    val totalAyahs: Int,
    val downloadedAyahs: Int,
    val isDownloading: Boolean,
    val isComplete: Boolean,
    val error: String? = null
)

object OfflineAudioManager {

    private const val OFFLINE_DIR_NAME = "quran_offline"

    data class AudioStorageInfo(
        val totalAudioCacheBytes: Long,
        val formattedAudioCacheSize: String,
        val offlineDownloadedBytes: Long,
        val formattedOfflineDownloadedSize: String,
        val availableDeviceSpaceBytes: Long,
        val formattedDeviceAvailableSpace: String,
        val quranCacheCount: Int,
        val ruqyahCacheCount: Int,
        val azkarCacheCount: Int,
        val downloadedSurahCount: Int
    )

    private val _surahDownloads = MutableStateFlow<Map<String, SurahDownloadProgress>>(emptyMap())
    val surahDownloads: StateFlow<Map<String, SurahDownloadProgress>> = _surahDownloads.asStateFlow()

    fun keyFor(qariId: String, surahNumber: Int): String = "${qariId}_$surahNumber"

    private fun getOfflineRootDir(context: Context): File {
        val dir = File(context.applicationContext.filesDir, OFFLINE_DIR_NAME)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun getQariFolder(context: Context, qariId: String): File {
        val safeId = qariId.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val dir = File(getOfflineRootDir(context), safeId)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getLocalAyahFile(
        context: Context,
        qariId: String,
        surahNumber: Int,
        ayahNumber: Int
    ): File {
        val fileName = String.format(Locale.US, "%03d%03d.mp3", surahNumber, ayahNumber)
        return File(getQariFolder(context, qariId), fileName)
    }

    /**
     * Returns a local `file://` URI immediately if this Ayah was downloaded for offline use;
     * otherwise returns the HTTPS remote URL for cached streaming via Media3.
     */
    fun getPlayableUrl(
        context: Context,
        qariId: String,
        surahNumber: Int,
        ayahNumber: Int,
        remoteUrl: String
    ): String {
        val local = getLocalAyahFile(context, qariId, surahNumber, ayahNumber)
        return if (local.exists() && local.length() > 1024L) {
            Uri.fromFile(local).toString()
        } else {
            remoteUrl
        }
    }

    fun isSurahDownloaded(
        context: Context,
        qariId: String,
        surahNumber: Int,
        ayahCount: Int
    ): Boolean {
        if (ayahCount <= 0) return false
        val folder = getQariFolder(context, qariId)
        if (!folder.exists()) return false
        val prefix = String.format(Locale.US, "%03d", surahNumber)
        val count = folder.listFiles()?.count {
            it.isFile && it.name.startsWith(prefix) && it.name.endsWith(".mp3") && it.length() > 1024L
        } ?: 0
        return count >= ayahCount
    }

    fun getDownloadedSurahNumbers(context: Context, qariId: String, surahAyahCounts: Map<Int, Int>): Set<Int> {
        val result = mutableSetOf<Int>()
        for ((surahNum, count) in surahAyahCounts) {
            if (isSurahDownloaded(context, qariId, surahNum, count)) {
                result.add(surahNum)
            }
        }
        return result
    }

    /**
     * Downloads all Ayah MP3 tracks for the specified Surah & Qari into internal app storage
     * with live progress reporting so full offline playback works with zero buffering.
     */
    suspend fun downloadSurah(
        context: Context,
        qariId: String,
        qariServerBaseUrl: String,
        surahNumber: Int,
        ayahCount: Int
    ): Boolean = withContext(Dispatchers.IO) {
        val key = keyFor(qariId, surahNumber)
        val base = qariServerBaseUrl.trimEnd('/')
        var completed = 0
        _surahDownloads.value = _surahDownloads.value + (
            key to SurahDownloadProgress(
                qariId = qariId,
                surahNumber = surahNumber,
                totalAyahs = ayahCount,
                downloadedAyahs = 0,
                isDownloading = true,
                isComplete = false
            )
        )

        try {
            val client = MediaCacheProvider.okHttpClient
            for (ayah in 1..ayahCount) {
                val targetFile = getLocalAyahFile(context, qariId, surahNumber, ayah)
                if (targetFile.exists() && targetFile.length() > 1024L) {
                    completed++
                    updateDownloadProgress(key, qariId, surahNumber, ayahCount, completed, isDownloading = true, isComplete = false)
                    continue
                }

                val code = String.format(Locale.US, "%03d%03d", surahNumber, ayah)
                val url = "$base/$code.mp3"
                val request = Request.Builder().url(url).build()
                val tmpFile = File(targetFile.parentFile, "${targetFile.name}.tmp")
                var ok = false
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.byteStream()?.use { input ->
                            FileOutputStream(tmpFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        if (tmpFile.exists() && tmpFile.length() > 1024L) {
                            if (targetFile.exists()) targetFile.delete()
                            tmpFile.renameTo(targetFile)
                            ok = true
                        } else {
                            tmpFile.delete()
                        }
                    }
                }
                if (!ok) {
                    tmpFile.delete()
                    updateDownloadProgress(
                        key, qariId, surahNumber, ayahCount, completed,
                        isDownloading = false, isComplete = false,
                        error = "Download interrupted at Ayah $ayah"
                    )
                    return@withContext false
                }
                completed++
                updateDownloadProgress(key, qariId, surahNumber, ayahCount, completed, isDownloading = true, isComplete = false)
            }
            updateDownloadProgress(key, qariId, surahNumber, ayahCount, completed, isDownloading = false, isComplete = true)
            true
        } catch (e: Exception) {
            updateDownloadProgress(
                key, qariId, surahNumber, ayahCount, completed,
                isDownloading = false, isComplete = false,
                error = e.localizedMessage
            )
            false
        }
    }

    private fun updateDownloadProgress(
        key: String,
        qariId: String,
        surahNumber: Int,
        totalAyahs: Int,
        downloadedAyahs: Int,
        isDownloading: Boolean,
        isComplete: Boolean,
        error: String? = null
    ) {
        _surahDownloads.value = _surahDownloads.value + (
            key to SurahDownloadProgress(
                qariId = qariId,
                surahNumber = surahNumber,
                totalAyahs = totalAyahs,
                downloadedAyahs = downloadedAyahs,
                isDownloading = isDownloading,
                isComplete = isComplete,
                error = error
            )
        )
    }

    fun deleteSurah(context: Context, qariId: String, surahNumber: Int): Boolean {
        val folder = getQariFolder(context, qariId)
        val prefix = String.format(Locale.US, "%03d", surahNumber)
        var deleted = false
        folder.listFiles()?.forEach { file ->
            if (file.isFile && file.name.startsWith(prefix)) {
                if (file.delete()) deleted = true
            }
        }
        val key = keyFor(qariId, surahNumber)
        _surahDownloads.value = _surahDownloads.value - key
        return deleted
    }

    fun getStorageInfo(context: Context): AudioStorageInfo {
        var cacheBytes = 0L
        var offlineBytes = 0L
        var quranCount = 0
        var ruqyahCount = 0
        var azkarCount = 0
        val downloadedSurahPrefixes = mutableSetOf<String>()

        val mediaCacheDir = MediaCacheProvider.getCacheDir(context)
        if (mediaCacheDir.exists()) {
            mediaCacheDir.walkTopDown().forEach { file ->
                if (file.isFile && file.name.endsWith(".exo")) {
                    cacheBytes += file.length()
                    quranCount++
                }
            }
        }

        val offlineRoot = getOfflineRootDir(context)
        if (offlineRoot.exists()) {
            offlineRoot.walkTopDown().forEach { file ->
                if (file.isFile && file.name.endsWith(".mp3")) {
                    offlineBytes += file.length()
                    quranCount++
                    val surahPrefix = file.parentFile?.name + "_" + file.name.take(3)
                    downloadedSurahPrefixes.add(surahPrefix)
                }
            }
        }

        val stat = StatFs(Environment.getDataDirectory().path)
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        val totalCombined = cacheBytes + offlineBytes

        return AudioStorageInfo(
            totalAudioCacheBytes = totalCombined,
            formattedAudioCacheSize = formatBytes(totalCombined),
            offlineDownloadedBytes = offlineBytes,
            formattedOfflineDownloadedSize = formatBytes(offlineBytes),
            availableDeviceSpaceBytes = availableBytes,
            formattedDeviceAvailableSpace = formatBytes(availableBytes),
            quranCacheCount = quranCount,
            ruqyahCacheCount = ruqyahCount,
            azkarCacheCount = azkarCount,
            downloadedSurahCount = downloadedSurahPrefixes.size
        )
    }

    fun clearAllAudioCache(context: Context, includeOfflineDownloads: Boolean = false): Boolean {
        val cacheCleared = MediaCacheProvider.releaseAndClearCache(context)
        var offlineCleared = true
        if (includeOfflineDownloads) {
            offlineCleared = try {
                getOfflineRootDir(context).deleteRecursively()
            } catch (_: Exception) {
                false
            }
            _surahDownloads.value = emptyMap()
        }
        return cacheCleared && offlineCleared
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0L) return "0 MB"
        val df = DecimalFormat("#.##")
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1.0 -> "${df.format(gb)} GB"
            mb >= 1.0 -> "${df.format(mb)} MB"
            kb >= 1.0 -> "${df.format(kb)} KB"
            else -> "$bytes B"
        }
    }
}
