package com.example

import com.example.data.location.NearbyMosque
import com.example.data.location.NearbyMosqueRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NearbyMosqueUnitTest {

    @Test
    fun `bengali numerals formatting converts digits accurately`() {
        assertEquals("১২৩৪৫৬৭৮৯০", NearbyMosque.toBengaliNumerals("1234567890"))
        assertEquals("৩৫০", NearbyMosque.toBengaliNumerals("350"))
        assertEquals("১.৫", NearbyMosque.toBengaliNumerals("1.5"))
    }

    @Test
    fun `distance formatting in bengali produces correct strings`() {
        // Less than 1000m
        val mosqueNear = NearbyMosque(
            id = "test_1",
            name = "Masjid Test",
            nameBengali = "টেস্ট মসজিদ",
            latitude = 23.8103,
            longitude = 90.4125,
            distanceMeters = 350f
        )
        assertEquals("৩৫০ মিটার দূরে", mosqueNear.distanceFormattedBengali)

        // Greater than 1000m
        val mosqueFar = NearbyMosque(
            id = "test_2",
            name = "Masjid Test Far",
            nameBengali = "দূরবর্তী মসজিদ",
            latitude = 23.8300,
            longitude = 90.4200,
            distanceMeters = 2400f
        )
        assertEquals("২.৪ কিমি দূরে", mosqueFar.distanceFormattedBengali)
    }

    @Test
    fun `estimated walking time produces realistic bengali representation`() {
        val mosque500m = NearbyMosque(
            id = "test_walk",
            name = "Masjid Test",
            nameBengali = "টেস্ট মসজিদ",
            latitude = 23.8103,
            longitude = 90.4125,
            distanceMeters = 400f
        )
        // 400m / 80m/min = 5 mins
        assertTrue(mosque500m.estimatedWalkingTimeBengali.contains("মিনিট হাঁটা পথ"))
        assertTrue(mosque500m.estimatedWalkingTimeBengali.contains("৫"))
    }

    @Test
    fun `google maps navigation URIs generated correctly`() {
        val mosque = NearbyMosque(
            id = "test_nav",
            name = "Baitul Mukarram",
            nameBengali = "বায়তুল মোকাররম",
            latitude = 23.7289,
            longitude = 90.4126
        )
        assertEquals("google.navigation:q=23.7289,90.4126&mode=w", mosque.googleMapsNavigationUri)
        assertTrue(mosque.googleMapsWebUrl.contains("destination=23.7289,90.4126"))
        assertTrue(mosque.googleMapsWebUrl.contains("travelmode=walking"))
    }

    @Test
    fun `curated fallback mosques calculates dynamic distances and returns sorted list`() {
        val repo = NearbyMosqueRepository()
        // Coordinates for Dhaka center
        val userLat = 23.8103
        val userLng = 90.4125

        val mosques = repo.getCuratedFallbackMosques(userLat, userLng, radiusMeters = 5000)
        assertTrue("Mosque list should not be empty", mosques.isNotEmpty())

        // Verify sorted by distance
        for (i in 0 until mosques.size - 1) {
            assertTrue(
                "List should be sorted ascending by distance",
                mosques[i].distanceMeters <= mosques[i + 1].distanceMeters
            )
        }

        // Verify all have non-blank Bengali names
        assertTrue(mosques.all { it.nameBengali.isNotBlank() })
    }
}
