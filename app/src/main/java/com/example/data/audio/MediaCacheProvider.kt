package com.example.data.audio

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Shared OkHttp client + 250 MB Media3 SimpleCache provider for fast, uninterrupted
 * streaming and automatic on-disk buffering of Quran, Du'a, Ruqyah, and Arabic pronunciation audio.
 */
@OptIn(UnstableApi::class)
object MediaCacheProvider {

    private const val CACHE_DIR_NAME = "media3_audio_cache"
    private const val MAX_CACHE_BYTES = 250L * 1024L * 1024L // 250 MB LRU audio cache

    @Volatile
    private var simpleCache: SimpleCache? = null

    @Volatile
    private var databaseProvider: StandaloneDatabaseProvider? = null

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectionPool(ConnectionPool(8, 5, TimeUnit.MINUTES))
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header(
                        "User-Agent",
                        "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
                    )
                    .build()
                chain.proceed(req)
            }
            .build()
    }

    fun getCacheDir(context: Context): File {
        val dir = File(context.applicationContext.cacheDir, CACHE_DIR_NAME)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    @Synchronized
    fun getCache(context: Context): SimpleCache {
        simpleCache?.let { return it }
        val appContext = context.applicationContext
        val dbProvider = databaseProvider ?: StandaloneDatabaseProvider(appContext).also {
            databaseProvider = it
        }
        val cache = SimpleCache(
            getCacheDir(appContext),
            LeastRecentlyUsedCacheEvictor(MAX_CACHE_BYTES),
            dbProvider
        )
        simpleCache = cache
        return cache
    }

    fun buildHttpDataSourceFactory(): DataSource.Factory =
        OkHttpDataSource.Factory(okHttpClient)

    fun buildUpstreamDataSourceFactory(context: Context): DataSource.Factory =
        DefaultDataSource.Factory(context.applicationContext, buildHttpDataSourceFactory())

    fun buildCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        val appContext = context.applicationContext
        return CacheDataSource.Factory()
            .setCache(getCache(appContext))
            .setUpstreamDataSourceFactory(buildUpstreamDataSourceFactory(appContext))
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun buildMediaSourceFactory(context: Context): MediaSource.Factory {
        return DefaultMediaSourceFactory(buildCacheDataSourceFactory(context))
    }

    /**
     * Pre-warms TLS sockets and reads the initial 64 KB of audio tracks into SimpleCache
     * so playback begins near-instantly when the user taps Listen.
     */
    suspend fun prewarmUrls(context: Context, urls: List<String>) = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val cache = getCache(appContext)
        urls.asSequence()
            .filter { it.startsWith("https://") }
            .distinct()
            .take(5)
            .forEach { url ->
                try {
                    if (cache.isCached(url, 0L, 16 * 1024L)) return@forEach
                    val req = Request.Builder()
                        .url(url)
                        .header("Range", "bytes=0-65535")
                        .build()
                    okHttpClient.newCall(req).execute().use { resp ->
                        if (resp.isSuccessful) {
                            resp.body?.byteStream()?.use { stream ->
                                val buf = ByteArray(8192)
                                var readTotal = 0
                                while (readTotal < 65536) {
                                    val n = stream.read(buf)
                                    if (n <= 0) break
                                    readTotal += n
                                }
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Best-effort pre-warm; ignore network failures
                }
            }
    }

    @Synchronized
    fun releaseAndClearCache(context: Context): Boolean {
        return try {
            simpleCache?.release()
            simpleCache = null
            getCacheDir(context).deleteRecursively()
        } catch (_: Exception) {
            false
        }
    }
}
