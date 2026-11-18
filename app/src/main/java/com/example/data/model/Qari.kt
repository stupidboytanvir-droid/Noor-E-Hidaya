package com.example.data.model

data class Qari(
    val id: String,
    val name: String,
    val arabicName: String,
    val style: String,
    val audioBaseUrl: String
)

object QariDirectory {
    // Strictly 100% Male Reciters Policy enforced across the entire application
    val MALE_RECITERS = listOf(
        Qari(
            id = "alafasy",
            name = "Sheikh Mishary Rashid Al-Afasy",
            arabicName = "مشاري بن راشد العفاسي",
            style = "Murattal (Clear & Melodic)",
            audioBaseUrl = "https://everyayah.com/data/Alafasy_128kbps"
        ),
        Qari(
            id = "abdulbasit",
            name = "Sheikh Abdul Basit Abdul Samad",
            arabicName = "عبد الباسط عبد الصمد",
            style = "Murattal (Classic Egyptian)",
            audioBaseUrl = "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps"
        ),
        Qari(
            id = "husary",
            name = "Sheikh Mahmud Khalil Al-Husary",
            arabicName = "محمود خليل الحصري",
            style = "Tajweed Master (Paced & Exact)",
            audioBaseUrl = "https://everyayah.com/data/Husary_128kbps"
        ),
        Qari(
            id = "sudais",
            name = "Sheikh Abdur-Rahman As-Sudais",
            arabicName = "عبد الرحمن السديস",
            style = "Imam of Masjid Al-Haram",
            audioBaseUrl = "https://everyayah.com/data/Abdurrahmaan_As-Sudais_192kbps"
        ),
        Qari(
            id = "muaiqly",
            name = "Sheikh Maher Al-Muaiqly",
            arabicName = "ماهر المعيقلي",
            style = "Imam of Masjid Al-Haram",
            audioBaseUrl = "https://everyayah.com/data/MaherAlMuaiqly128kbps"
        )
    )

    fun getById(id: String): Qari {
        return MALE_RECITERS.find { it.id == id } ?: MALE_RECITERS[0]
    }
}
