package com.example.data.location

import java.util.Locale

/**
 * Data model for a nearby Mosque (মসজিদ) discovered via real-time Radar/Places/Overpass API.
 */
data class NearbyMosque(
    val id: String,
    val name: String,
    val nameBengali: String,
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Float = 0f,
    val address: String = "কাছাকাছি এলাকা",
    val isOpenNow: Boolean = true,
    val openingHoursText: String = "সার্বক্ষণিক খোলা / ওয়াক্ত অনুযায়ী জামা'আত",
    val hasWuduFacility: Boolean = true,
    val hasWomenPrayerArea: Boolean = false,
    val hasAirConditioning: Boolean = true,
    val contactNumber: String? = null,
    val rating: Float = 4.8f,
    val isJummahAvailable: Boolean = true
) {
    /**
     * Formats distance into localized Bengali representation.
     * e.g., 350m -> "৩৫০ মিটার দূরে", 1450m -> "১.৫ কিমি দূরে"
     */
    val distanceFormattedBengali: String
        get() {
            return if (distanceMeters < 1000f) {
                val meters = distanceMeters.toInt()
                "${toBengaliNumerals(meters.toString())} মিটার দূরে"
            } else {
                val km = String.format(Locale.US, "%.1f", distanceMeters / 1000f)
                "${toBengaliNumerals(km)} কিমি দূরে"
            }
        }

    val distanceFormattedEnglish: String
        get() {
            return if (distanceMeters < 1000f) {
                "${distanceMeters.toInt()} m away"
            } else {
                val km = String.format(Locale.US, "%.1f", distanceMeters / 1000f)
                "$km km away"
            }
        }

    fun localizedDistance(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") distanceFormattedBengali else distanceFormattedEnglish

    /**
     * Estimated walking time assuming normal 4.8 km/h pace.
     */
    val estimatedWalkingTimeBengali: String
        get() {
            val minutes = (distanceMeters / (80.0f)).toInt().coerceAtLeast(1)
            return if (minutes < 60) {
                "~ ${toBengaliNumerals(minutes.toString())} মিনিট হাঁটা পথ"
            } else {
                val hours = minutes / 60
                val remainingMins = minutes % 60
                "~ ${toBengaliNumerals(hours.toString())} ঘণ্টা ${toBengaliNumerals(remainingMins.toString())} মিনিট"
            }
        }

    val estimatedWalkingTimeEnglish: String
        get() {
            val minutes = (distanceMeters / (80.0f)).toInt().coerceAtLeast(1)
            return if (minutes < 60) {
                "~ $minutes min walk"
            } else {
                val hours = minutes / 60
                val remainingMins = minutes % 60
                "~ ${hours}h ${remainingMins}m walk"
            }
        }

    fun localizedWalkingTime(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") estimatedWalkingTimeBengali else estimatedWalkingTimeEnglish

    fun localizedName(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) == "bn") nameBengali.ifBlank { name } else name.ifBlank { nameBengali }

    fun localizedAddress(lang: String): String =
        if (com.example.data.util.SupportedLanguage.normalizeCode(lang) != "bn" && address == "কাছাকাছি এলাকা") {
            com.example.ui.theme.localizedText(lang, "কাছাকাছি এলাকা", "Nearby Area")
        } else address

    /**
     * Turn-by-turn navigation Google Maps intent URI.
     */
    val googleMapsNavigationUri: String
        get() = "google.navigation:q=$latitude,$longitude&mode=w"

    /**
     * Web fallback Google Maps direction URL.
     */
    val googleMapsWebUrl: String
        get() = "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude&travelmode=walking"

    companion object {
        fun toBengaliNumerals(input: String): String {
            val bengaliDigits = mapOf(
                '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
                '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯',
                '.' to '.'
            )
            return input.map { bengaliDigits[it] ?: it }.joinToString("")
        }
    }
}
