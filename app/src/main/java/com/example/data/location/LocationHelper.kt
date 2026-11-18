package com.example.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.data.model.DistrictDirectory
import com.example.data.util.SupportedLanguage
import com.example.ui.theme.LocalizationRegistry
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

data class UserLocation(
    val latitude: Double = 23.8103, // Default: Dhaka, Bangladesh
    val longitude: Double = 90.4125,
    val accuracyMeters: Float = 10f,
    val isLiveLocation: Boolean = false,
    val cityName: String = "Dhaka, Bangladesh"
)

data class DetectedLocationResult(
    val districtId: String,
    val districtName: String,
    val displayLocationName: String,
    val latitude: Double,
    val longitude: Double,
    val isGpsDetected: Boolean
)

/**
 * High-precision GPS location helper using Google Play Services FusedLocationProviderClient,
 * native LocationManager fallback, and Geocoder city resolution localized to the active app language.
 */
class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow(UserLocation())
    val currentLocation: StateFlow<UserLocation> = _currentLocation.asStateFlow()

    private var locationCallback: LocationCallback? = null

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    /**
     * Automatically detects the device's current coordinates (validating cache freshness,
     * requesting HIGH_ACCURACY live fix, falling back to BALANCED / LocationManager) and resolves
     * a human-readable city/district name in [langCode].
     */
    @SuppressLint("MissingPermission")
    suspend fun detectCurrentLocation(langCode: String = "bn"): DetectedLocationResult? {
        if (!hasLocationPermission()) return null

        val location = withTimeoutOrNull(12_000L) {
            fetchBestLocationSuspend()
        } ?: getAndroidLocationManagerFallback()

        if (location == null) return null

        val resolved = resolveLocationDetails(location.latitude, location.longitude, langCode)
        _currentLocation.value = UserLocation(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracyMeters = location.accuracy,
            isLiveLocation = true,
            cityName = resolved.displayLocationName
        )

        return resolved
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchBestLocationSuspend(): Location? {
        // 1. Check cached lastLocation only if fresh (< 15 min) and reasonably accurate (<= 500m)
        val lastKnown = suspendCancellableCoroutine<Location?> { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { loc ->
                    if (cont.isActive) cont.resume(loc)
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(null)
                }
        }
        val now = System.currentTimeMillis()
        if (lastKnown != null && (now - lastKnown.time) <= 15 * 60_000L && lastKnown.accuracy <= 500f) {
            return lastKnown
        }

        // 2. Request a fresh high-accuracy current location fix
        val highAccuracyLoc = requestSingleFix(Priority.PRIORITY_HIGH_ACCURACY)
        if (highAccuracyLoc != null) return highAccuracyLoc

        // 3. Fallback to balanced power accuracy fix or stale lastKnown
        return requestSingleFix(Priority.PRIORITY_BALANCED_POWER_ACCURACY) ?: lastKnown
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestSingleFix(priority: Int): Location? {
        val cts = CancellationTokenSource()
        return withTimeoutOrNull(7_000L) {
            suspendCancellableCoroutine { cont ->
                cont.invokeOnCancellation { cts.cancel() }
                fusedLocationClient.getCurrentLocation(priority, cts.token)
                    .addOnSuccessListener { loc ->
                        if (cont.isActive) cont.resume(loc)
                    }
                    .addOnFailureListener {
                        if (cont.isActive) cont.resume(null)
                    }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getAndroidLocationManagerFallback(): Location? {
        if (!hasLocationPermission()) return null
        return try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
            val providers = lm.getProviders(true)
            var best: Location? = null
            for (provider in providers) {
                val loc = try {
                    lm.getLastKnownLocation(provider)
                } catch (_: Exception) {
                    null
                }
                if (loc != null && (best == null || loc.accuracy < best.accuracy)) {
                    best = loc
                }
            }
            best
        } catch (_: Exception) {
            null
        }
    }

    suspend fun resolveLocationDetails(
        lat: Double,
        lng: Double,
        langCode: String = "bn"
    ): DetectedLocationResult =
        withContext(Dispatchers.IO) {
            val normLang = SupportedLanguage.normalizeCode(langCode)
            val isBn = normLang == "bn"
            val (nearestDistrict, distanceKm) = DistrictDirectory.findNearestDistrict(lat, lng)
            val insideBd = DistrictDirectory.isInsideBangladeshBounds(lat, lng) || distanceKm < 80.0

            val geocodedCity = geocodeCityName(lat, lng, normLang)

            if (insideBd) {
                val cityLabel = if (isBn) nearestDistrict.nameBengali else nearestDistrict.nameEnglish
                val countryLabel = LocalizationRegistry.translate(normLang, "বাংলাদেশ", "Bangladesh")
                DetectedLocationResult(
                    districtId = nearestDistrict.id,
                    districtName = cityLabel,
                    displayLocationName = "$cityLabel, $countryLabel",
                    latitude = lat,
                    longitude = lng,
                    isGpsDetected = true
                )
            } else {
                val fallbackName = geocodedCity ?: getCityDescription(lat, lng, normLang)
                DetectedLocationResult(
                    districtId = "gps_custom",
                    districtName = fallbackName,
                    displayLocationName = fallbackName,
                    latitude = lat,
                    longitude = lng,
                    isGpsDetected = true
                )
            }
        }

    private suspend fun geocodeCityName(lat: Double, lng: Double, langCode: String): String? {
        if (!Geocoder.isPresent()) return null
        return try {
            val targetLocale = SupportedLanguage.fromCode(langCode).locale
            val geocoder = Geocoder(context, targetLocale)
            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                withTimeoutOrNull(3500L) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(lat, lng, 1) { list ->
                            if (cont.isActive) cont.resume(list)
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(lat, lng, 1)
            }

            val addr = addresses?.firstOrNull() ?: return null
            val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea
            val country = addr.countryName
            when {
                !city.isNullOrBlank() && !country.isNullOrBlank() -> "$city, $country"
                !city.isNullOrBlank() -> city
                !country.isNullOrBlank() -> country
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLastKnownLocation(langCode: String = "bn", onSuccess: ((Location) -> Unit)? = null) {
        if (!hasLocationPermission()) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val resolvedLoc = location ?: getAndroidLocationManagerFallback()
                if (resolvedLoc != null) {
                    _currentLocation.value = UserLocation(
                        latitude = resolvedLoc.latitude,
                        longitude = resolvedLoc.longitude,
                        accuracyMeters = resolvedLoc.accuracy,
                        isLiveLocation = true,
                        cityName = getCityDescription(resolvedLoc.latitude, resolvedLoc.longitude, langCode)
                    )
                    onSuccess?.invoke(resolvedLoc)
                }
            }
            .addOnFailureListener {
                val fallback = getAndroidLocationManagerFallback()
                if (fallback != null) {
                    _currentLocation.value = UserLocation(
                        latitude = fallback.latitude,
                        longitude = fallback.longitude,
                        accuracyMeters = fallback.accuracy,
                        isLiveLocation = true,
                        cityName = getCityDescription(fallback.latitude, fallback.longitude, langCode)
                    )
                    onSuccess?.invoke(fallback)
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun startRealtimeLocationUpdates(langCode: String = "bn", onUpdate: ((Location) -> Unit)? = null) {
        if (!hasLocationPermission()) return

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L
        ).apply {
            setMinUpdateIntervalMillis(5000L)
            setMinUpdateDistanceMeters(10f)
        }.build()

        stopLocationUpdates()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                _currentLocation.value = UserLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracyMeters = location.accuracy,
                    isLiveLocation = true,
                    cityName = getCityDescription(location.latitude, location.longitude, langCode)
                )
                onUpdate?.invoke(location)
            }
        }

        locationCallback?.let {
            fusedLocationClient.requestLocationUpdates(locationRequest, it, context.mainLooper)
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    fun getCityDescription(lat: Double, lng: Double, langCode: String = "bn"): String {
        val normLang = SupportedLanguage.normalizeCode(langCode)
        val isBn = normLang == "bn"
        val (nearest, distKm) = DistrictDirectory.findNearestDistrict(lat, lng)
        if (DistrictDirectory.isInsideBangladeshBounds(lat, lng) || distKm < 80.0) {
            val cityLabel = if (isBn) nearest.nameBengali else nearest.nameEnglish
            val countryLabel = LocalizationRegistry.translate(normLang, "বাংলাদেশ", "Bangladesh")
            return "$cityLabel, $countryLabel"
        }
        return when {
            lat in 21.3..21.6 && lng in 39.7..40.0 ->
                LocalizationRegistry.translate(normLang, "মক্কা মুকাররমা", "Makkah")
            lat in 24.4..24.6 && lng in 39.5..39.7 ->
                LocalizationRegistry.translate(normLang, "মদিনা মুনাওয়ারা", "Madinah")
            else ->
                LocalizationRegistry.translate(normLang, "বর্তমান জিপিএস অবস্থান", "Current GPS Location")
        }
    }
}
