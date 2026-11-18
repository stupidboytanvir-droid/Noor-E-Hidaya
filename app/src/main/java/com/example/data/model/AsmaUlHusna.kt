package com.example.data.model

data class AllahName(
    val number: Int,
    val arabic: String,
    val transliteration: String,
    val meaningBengali: String,
    val meaningEnglish: String
)

object AsmaUlHusnaRepository {
    val NAMES: List<AllahName> = listOf(
        AllahName(1, "الرَّحْمَٰنُ", "Ar-Rahman", "পরম দয়াময়", "The Entirely Merciful"),
        AllahName(2, "الرَّحِيمُ", "Ar-Raheem", "অতি দয়ালু", "The Especially Merciful"),
        AllahName(3, "الْمَلِكُ", "Al-Malik", "সার্বভৌম ক্ষমতার অধিকারী", "The Sovereign Lord"),
        AllahName(4, "الْقُدُّوسُ", "Al-Quddus", "নিখুঁত ও পরম পবিত্র", "The Pure / The Holy"),
        AllahName(5, "السَّلَامُ", "As-Salam", "শান্তিদাতা", "The Source of Peace"),
        AllahName(6, "الْمُؤْمِنُ", "Al-Mu'min", "নিরাপত্তা ও ঈমানদানকারী", "The Granter of Security"),
        AllahName(7, "الْمُهَيْمِنُ", "Al-Muhaymin", "রক্ষক ও পর্যবেক্ষক", "The Preserver of Safety"),
        AllahName(8, "الْعَزِيزُ", "Al-Aziz", "পরাক্রমশালী", "The Almighty"),
        AllahName(9, "الْجَبَّارُ", "Al-Jabbar", "অপ্রতিহত প্রভাবের অধিকারী", "The Compeller"),
        AllahName(10, "الْمُتَكَبِّرُ", "Al-Mutakabbir", "সর্বশ্রেষ্ঠ মর্যাদাবান", "The Supreme"),
        AllahName(11, "الْخَالِقُ", "Al-Khaliq", "সৃষ্টিকর্তা", "The Creator"),
        AllahName(12, "الْبَارِئُ", "Al-Bari'", "উদ্ভাবক", "The Evolver"),
        AllahName(13, "الْمُصَوِّرُ", "Al-Musawwir", "রূপদানকারী", "The Fashioner"),
        AllahName(14, "الْغَفَّارُ", "Al-Ghaffar", "অসীম ক্ষমাশীল", "The All-Forgiving"),
        AllahName(15, "الْقَهَّارُ", "Al-Qahhar", "কঠোর নিয়ন্ত্রক", "The Subduer"),
        AllahName(16, "الْوَهَّابُ", "Al-Wahhab", "সবকিছু দানকারী", "The Bestower"),
        AllahName(17, "الرَّزَّاقُ", "Ar-Razzaq", "রিজিকদাতা", "The Provider"),
        AllahName(18, "الْفَتَّاحُ", "Al-Fattah", "বিজয় ও পথ উন্মোচনকারী", "The Opener"),
        AllahName(19, "الْعَلِيمُ", "Al-Aleem", "সর্বজ্ঞাত", "The All-Knowing"),
        AllahName(20, "الْقَابِضُ", "Al-Qabid", "সংকোচনকারী", "The Withholder"),
        AllahName(21, "الْبَاسِطُ", "Al-Basit", "সম্প্রসারণকারী", "The Extender"),
        AllahName(22, "الْخَافِضُ", "Al-Khafid", "অবনমিতকারী", "The Abaser"),
        AllahName(23, "الرَّافِعُ", "Ar-Rafi'", "উন্নতকারী", "The Exalter"),
        AllahName(24, "الْمُعِزُّ", "Al-Mu'izz", "সম্মানদানকারী", "The Bestower of Honor"),
        AllahName(25, "الْمُذِلُّ", "Al-Mudhill", "অপমানিতকারী", "The Humiliator"),
        AllahName(26, "السَّمِيعُ", "As-Sami'", "সর্বশ্রোতা", "The All-Hearing"),
        AllahName(27, "الْبَصِيرُ", "Al-Baseer", "সর্বদ্রষ্টা", "The All-Seeing"),
        AllahName(28, "الْحَكَمُ", "Al-Hakam", "সর্বোচ্চ বিচারক", "The Judge"),
        AllahName(29, "الْعَدْلُ", "Al-Adl", "পরম ন্যায়পরায়ণ", "The Utterly Just"),
        AllahName(30, "اللَّطِيفُ", "Al-Lateef", "সূক্ষ্মদৃষ্টিসম্পন্ন, স্নেহশীল", "The Subtle One")
    )
}
