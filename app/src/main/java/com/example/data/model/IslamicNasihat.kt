package com.example.data.model

/**
 * Categories representing authentic themes for AI-generated daily Believer's Nasihah.
 * Static pre-written advice lists have been retired in favor of daily Gemini AI generation.
 */
enum class NasihatCategory(
    val displayNameBn: String,
    val displayNameEn: String,
    val badgeColorHex: String
) {
    AKHLAQ("উত্তম চরিত্র ও আদব", "Good Character & Manners", "#E5C158"),
    SUNNAH_ADAB("দৈনন্দিন সুন্নাত", "Daily Sunnah", "#00E676"),
    SABR_SHUKR("সবর ও কৃতজ্ঞতা", "Patience & Gratitude", "#4FC3F7"),
    AKHIRAH("আখেরাত ও কবর", "Hereafter & Grave", "#FF8A65"),
    TAQWA_IKHLAS("তাকওয়া ও ইখলাস", "Taqwa & Sincerity", "#BA68C8"),
    TONGUE_SPEECH("জিহ্বা ও চোখের হেফাজত", "Guarding Tongue & Gaze", "#FFD54F"),
    FAMILY_PARENT("পিতা-মাতা ও পরিবার", "Parents & Family", "#81C784"),
    HALAL_EARNING("হালাল রিযিক ও সততা", "Halal Earning & Honesty", "#4DB6AC"),
    TAWAKKUL("তাওয়াক্কুল ও দুশ্চিন্তা", "Tawakkul & Peace", "#AED581"),
    TAWBAH_ISTIGHFAR("তওবা ও ইস্তিগফার", "Tawbah & Istighfar", "#4DD0E1"),
    PRAYER_KHUSHU("নামায ও অন্তরের প্রশান্তি", "Salah & Khushu'", "#7986CB"),
    CHARITY_BROTHERHOOD("সদকাহ ও মুসলিম ভ্রাতৃত্ব", "Charity & Brotherhood", "#F06292");

    fun localizedName(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, displayNameBn, displayNameEn)
}
