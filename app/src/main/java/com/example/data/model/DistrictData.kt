package com.example.data.model

data class District(
    val id: String,
    val nameBengali: String,
    val nameEnglish: String,
    val divisionBengali: String,
    val latitude: Double,
    val longitude: Double
)

object DistrictDirectory {
    val DEFAULT_DISTRICT = District(
        id = "dhaka",
        nameBengali = "ঢাকা",
        nameEnglish = "Dhaka",
        divisionBengali = "ঢাকা",
        latitude = 23.8103,
        longitude = 90.4125
    )

    val BANGLADESH_DISTRICTS: List<District> = listOf(
        // Dhaka Division
        District("dhaka", "ঢাকা", "Dhaka", "ঢাকা", 23.8103, 90.4125),
        District("gazipur", "গাজীপুর", "Gazipur", "ঢাকা", 23.9999, 90.4203),
        District("narayanganj", "নারায়ণগঞ্জ", "Narayanganj", "ঢাকা", 23.6238, 90.5000),
        District("tangail", "টাঙ্গাইল", "Tangail", "ঢাকা", 24.2513, 89.9167),
        District("faridpur", "ফরিদপুর", "Faridpur", "ঢাকা", 23.6071, 89.8429),
        District("manikganj", "মানিকগঞ্জ", "Manikganj", "ঢাকা", 23.8617, 90.0003),
        District("munshiganj", "মুন্সীগঞ্জ", "Munshiganj", "ঢাকা", 23.5422, 90.5305),
        District("narsingdi", "নরসিংদী", "Narsingdi", "ঢাকা", 23.9193, 90.7176),
        District("gopalganj", "গোপালগঞ্জ", "Gopalganj", "ঢাকা", 23.0051, 89.8266),
        District("madaripur", "মাদারীপুর", "Madaripur", "ঢাকা", 23.1641, 90.1897),
        District("rajbari", "রাজবাড়ী", "Rajbari", "ঢাকা", 23.7574, 89.6445),
        District("shariatpur", "শরীয়তপুর", "Shariatpur", "ঢাকা", 23.2423, 90.3547),
        District("kishoreganj", "কিশোরগঞ্জ", "Kishoreganj", "ঢাকা", 24.4449, 90.7766),

        // Chattogram Division
        District("chattogram", "চট্টগ্রাম", "Chattogram", "চট্টগ্রাম", 22.3569, 91.7832),
        District("coxsbazar", "কক্সবাজার", "Cox's Bazar", "চট্টগ্রাম", 21.4272, 92.0058),
        District("cumilla", "কুমিল্লা", "Cumilla", "চট্টগ্রাম", 23.4682, 91.1788),
        District("feni", "ফেনী", "Feni", "চট্টগ্রাম", 23.0186, 91.3966),
        District("brahmanbaria", "ব্রাহ্মণবাড়িয়া", "Brahmanbaria", "চট্টগ্রাম", 23.9571, 91.1119),
        District("chandpur", "চাঁদপুর", "Chandpur", "চট্টগ্রাম", 23.2333, 90.6667),
        District("noakhali", "নোয়াখালী", "Noakhali", "চট্টগ্রাম", 22.8696, 91.0998),
        District("lakshmipur", "লক্ষ্মীপুর", "Lakshmipur", "চট্টগ্রাম", 22.9425, 90.8412),
        District("khagrachhari", "খাগড়াছড়ি", "Khagrachhari", "চট্টগ্রাম", 23.1193, 91.9847),
        District("rangamati", "রাঙ্গামাটি", "Rangamati", "চট্টগ্রাম", 22.7324, 92.2985),
        District("bandarban", "বান্দরবান", "Bandarban", "চট্টগ্রাম", 22.1953, 92.2184),

        // Sylhet Division
        District("sylhet", "সিলেট", "Sylhet", "সিলেট", 24.8949, 91.8687),
        District("moulvibazar", "মৌলভীবাজার", "Moulvibazar", "সিলেট", 24.4829, 91.7774),
        District("habiganj", "হবিগঞ্জ", "Habiganj", "সিলেট", 24.3750, 91.4167),
        District("sunamganj", "সুনামগঞ্জ", "Sunamganj", "সিলেট", 25.0658, 91.3950),

        // Rajshahi Division
        District("rajshahi", "রাজশাহী", "Rajshahi", "রাজশাহী", 24.3745, 88.6042),
        District("bogura", "বগুড়া", "Bogura", "রাজশাহী", 24.8465, 89.3770),
        District("pabna", "পাবনা", "Pabna", "রাজশাহী", 24.0064, 89.2372),
        District("sirajganj", "সিরাজগঞ্জ", "Sirajganj", "রাজশাহী", 24.4534, 89.7008),
        District("naogaon", "নওগাঁ", "Naogaon", "রাজশাহী", 24.7936, 88.9318),
        District("natore", "নাটোর", "Natore", "রাজশাহী", 24.4206, 88.9324),
        District("chapainawabganj", "চাঁপাইনবাবগঞ্জ", "Chapainawabganj", "রাজশাহী", 24.5965, 88.2775),
        District("joypurhat", "জয়পুরহাট", "Joypurhat", "রাজশাহী", 25.1015, 89.0270),

        // Khulna Division
        District("khulna", "খুলনা", "Khulna", "খুলনা", 22.8456, 89.5403),
        District("jashore", "যশোর", "Jashore", "খুলনা", 23.1664, 89.2182),
        District("satkhira", "সাতক্ষীরা", "Satkhira", "খুলনা", 22.7185, 89.0705),
        District("kushtia", "কুষ্টিয়া", "Kushtia", "খুলনা", 23.9013, 89.1205),
        District("jhenaidah", "ঝিনাইদহ", "Jhenaidah", "খুলনা", 23.5448, 89.1539),
        District("chuadanga", "চুয়াডাঙ্গা", "Chuadanga", "খুলনা", 23.6402, 88.8418),
        District("meherpur", "মেহেরপুর", "Meherpur", "খুলনা", 23.7622, 88.6318),
        District("magura", "মাগুরা", "Magura", "খুলনা", 23.4873, 89.4198),
        District("narail", "নড়াইল", "Narail", "খুলনা", 23.1725, 89.5127),
        District("bagerhat", "বাগেরহাট", "Bagerhat", "খুলনা", 22.6516, 89.7859),

        // Barishal Division
        District("barishal", "বরিশাল", "Barishal", "বরিশাল", 22.7010, 90.3535),
        District("bhola", "ভোলা", "Bhola", "বরিশাল", 22.6859, 90.6481),
        District("patuakhali", "পটুয়াখালী", "Patuakhali", "বরিশাল", 22.3596, 90.3299),
        District("pirojpur", "পিরোজপুর", "Pirojpur", "বরিশাল", 22.5841, 89.9720),
        District("barguna", "বরগুনা", "Barguna", "বরিশাল", 22.0953, 90.1121),
        District("jhalakathi", "ঝালকাঠি", "Jhalakathi", "বরিশাল", 22.6406, 90.1987),

        // Rangpur Division
        District("rangpur", "রংপুর", "Rangpur", "রংপুর", 25.7439, 89.2752),
        District("dinajpur", "দিনাজপুর", "Dinajpur", "রংপুর", 25.6217, 88.6355),
        District("kurigram", "কুড়িগ্রাম", "Kurigram", "রংপুর", 25.8054, 89.6362),
        District("gaibandha", "গাইবান্ধা", "Gaibandha", "রংপুর", 25.3288, 89.5403),
        District("nilphamari", "নীলফামারী", "Nilphamari", "রংপুর", 25.9318, 88.8560),
        District("panchagarh", "পঞ্চগড়", "Panchagarh", "রংপুর", 26.3411, 88.5542),
        District("thakurgaon", "ঠাকুরগাঁও", "Thakurgaon", "রংপুর", 26.0337, 88.4617),
        District("lalmonirhat", "লালমনিরহাট", "Lalmonirhat", "রংপুর", 25.9923, 89.2847),

        // Mymensingh Division
        District("mymensingh", "ময়মনসিংহ", "Mymensingh", "ময়মনসিংহ", 24.7471, 90.4203),
        District("jamalpur", "জামালপুর", "Jamalpur", "ময়মনসিংহ", 24.9375, 89.9378),
        District("netrokona", "নেত্রকোণা", "Netrokona", "ময়মনসিংহ", 24.8709, 90.7279),
        District("sherpur", "শেরপুর", "Sherpur", "ময়মনসিংহ", 25.0205, 90.0153)
    )

    fun findById(id: String): District {
        return BANGLADESH_DISTRICTS.find { it.id.equals(id, ignoreCase = true) } ?: DEFAULT_DISTRICT
    }

    fun findByName(name: String): District {
        return BANGLADESH_DISTRICTS.find {
            it.nameBengali.equals(name, ignoreCase = true) ||
            it.nameEnglish.equals(name, ignoreCase = true)
        } ?: DEFAULT_DISTRICT
    }

    fun isInsideBangladeshBounds(lat: Double, lng: Double): Boolean {
        return lat in 20.3..26.7 && lng in 87.9..92.8
    }

    /**
     * Finds the closest district in Bangladesh and returns (District, distanceKm).
     */
    fun findNearestDistrict(lat: Double, lng: Double): Pair<District, Double> {
        var closest = DEFAULT_DISTRICT
        var minDistanceKm = Double.MAX_VALUE
        for (district in BANGLADESH_DISTRICTS) {
            val dKm = haversineKm(lat, lng, district.latitude, district.longitude)
            if (dKm < minDistanceKm) {
                minDistanceKm = dKm
                closest = district
            }
        }
        return closest to minDistanceKm
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }
}
