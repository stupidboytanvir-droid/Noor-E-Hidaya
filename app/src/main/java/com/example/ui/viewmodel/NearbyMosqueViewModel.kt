package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.location.LocationHelper
import com.example.data.location.NearbyMosque
import com.example.data.location.NearbyMosqueRepository
import com.example.data.location.UserLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NearbyMosqueUiState(
    val userLocation: UserLocation = UserLocation(),
    val allMosques: List<NearbyMosque> = emptyList(),
    val filteredMosques: List<NearbyMosque> = emptyList(),
    val selectedMosque: NearbyMosque? = null,
    val selectedRadiusMeters: Int = 3000,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val mapTileStyle: String = "dark", // "dark" (Obsidian CartoDB) or "satellite" (Esri)
    val errorMessage: String? = null
)

class NearbyMosqueViewModel(application: Application) : AndroidViewModel(application) {

    private val locationHelper = LocationHelper(application)
    private val repository = NearbyMosqueRepository()

    private val _uiState = MutableStateFlow(NearbyMosqueUiState())
    val uiState: StateFlow<NearbyMosqueUiState> = _uiState.asStateFlow()

    init {
        checkPermissionAndInitialize()
    }

    fun checkPermissionAndInitialize() {
        val hasPermission = locationHelper.hasLocationPermission()
        _uiState.value = _uiState.value.copy(hasPermission = hasPermission)

        if (hasPermission) {
            locationHelper.fetchLastKnownLocation { location ->
                _uiState.value = _uiState.value.copy(
                    userLocation = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracyMeters = location.accuracy,
                        isLiveLocation = true
                    )
                )
                refreshMosques()
            }
            locationHelper.startRealtimeLocationUpdates { location ->
                val prev = _uiState.value.userLocation
                val distMeters = NearbyMosqueRepository.calculateDistanceMeters(
                    prev.latitude, prev.longitude,
                    location.latitude, location.longitude
                )
                // If moved more than 50 meters, update and refresh list
                if (distMeters > 50f) {
                    _uiState.value = _uiState.value.copy(
                        userLocation = UserLocation(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracyMeters = location.accuracy,
                            isLiveLocation = true
                        )
                    )
                    refreshMosques()
                }
            }
        } else {
            // Load initial mosques using default Dhaka coordinates
            refreshMosques()
        }
    }

    fun onPermissionGranted() {
        _uiState.value = _uiState.value.copy(hasPermission = true)
        checkPermissionAndInitialize()
    }

    fun setRadius(radiusMeters: Int) {
        _uiState.value = _uiState.value.copy(selectedRadiusMeters = radiusMeters)
        refreshMosques()
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun selectMosque(mosque: NearbyMosque?) {
        _uiState.value = _uiState.value.copy(selectedMosque = mosque)
    }

    fun toggleMapTileStyle() {
        val next = when (_uiState.value.mapTileStyle) {
            "dark" -> "satellite"
            "satellite" -> "osm"
            else -> "dark"
        }
        _uiState.value = _uiState.value.copy(mapTileStyle = next)
    }

    fun refreshMosques() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val loc = _uiState.value.userLocation
            val radius = _uiState.value.selectedRadiusMeters

            val mosques = repository.findNearbyMosques(
                userLat = loc.latitude,
                userLng = loc.longitude,
                radiusMeters = radius
            )

            _uiState.value = _uiState.value.copy(
                allMosques = mosques,
                isLoading = false,
                selectedMosque = _uiState.value.selectedMosque?.let { prev ->
                    mosques.find { it.id == prev.id }
                } ?: mosques.firstOrNull()
            )
            applyFilters()
        }
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val all = _uiState.value.allMosques
        val filtered = if (query.isEmpty()) {
            all
        } else {
            all.filter {
                it.nameBengali.lowercase().contains(query) ||
                it.name.lowercase().contains(query) ||
                it.address.lowercase().contains(query)
            }
        }
        _uiState.value = _uiState.value.copy(filteredMosques = filtered)
    }

    /**
     * Launch Google Maps Turn-by-Turn GPS Navigation.
     */
    fun openGoogleMapsNavigation(context: Context, mosque: NearbyMosque, uiLang: String = "bn") {
        try {
            // Intent for Google Maps Turn-by-Turn Navigation (Walking mode)
            val gmmIntentUri = Uri.parse(mosque.googleMapsNavigationUri)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                // Fallback to web browser Google Maps directions
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mosque.googleMapsWebUrl))
                context.startActivity(webIntent)
            }
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mosque.googleMapsWebUrl))
            try {
                context.startActivity(webIntent)
            } catch (err: Exception) {
                Toast.makeText(
                    context,
                    com.example.ui.theme.localizedText(
                        uiLang,
                        "ম্যাপস অ্যাপ্লিকেশন খোলা যায়নি",
                        "Unable to open Maps application"
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Share Mosque Location with others.
     */
    fun shareMosqueLocation(context: Context, mosque: NearbyMosque, uiLang: String = "bn") {
        try {
            val isBn = com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn"
            val shareText = if (!isBn) {
                """
                🕌 ${mosque.localizedName(uiLang)}
                📍 Address: ${mosque.localizedAddress(uiLang)}
                📏 Distance: ${mosque.localizedDistance(uiLang)}
                🧭 Google Maps: ${mosque.googleMapsWebUrl}
                
                (Shared from Noor-e-Hidayah Islamic App)
                """.trimIndent()
            } else {
                """
                🕌 ${mosque.nameBengali} (${mosque.name})
                📍 ঠিকানা: ${mosque.address}
                📏 দূরত্ব: ${mosque.distanceFormattedBengali}
                🧭 গুগল ম্যাপস লোকেশন: ${mosque.googleMapsWebUrl}
                
                (নূর-এ-হেদায়াত ইসলামিক অ্যাপ থেকে শেয়ার করা হয়েছে)
                """.trimIndent()
            }

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            context.startActivity(
                Intent.createChooser(
                    sendIntent,
                    com.example.ui.theme.localizedText(
                        uiLang,
                        "মসজিদের লোকেশন শেয়ার করুন",
                        "Share Mosque Location"
                    )
                )
            )
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        locationHelper.stopLocationUpdates()
    }
}
