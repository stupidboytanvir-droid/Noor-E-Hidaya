package com.example.data.repository

import com.example.data.model.Hadith
import com.example.data.model.HadithBook
import com.example.data.model.HadithChapter

object HadithRepository {

    val SAHIH_BOOKS: List<HadithBook> get() = HadithMasterRepository.HADITH_BOOKS

    val ALL_HADITHS: List<Hadith> get() = ExpandedHadithBookContent.ENCYCLOPEDIC_HADITHS

    fun searchHadiths(query: String, selectedBookId: String? = null): List<Hadith> {
        val trimmed = query.trim().lowercase()
        return ALL_HADITHS.filter { hadith ->
            val matchesBook = selectedBookId == null || hadith.bookId == selectedBookId
            val matchesQuery = if (trimmed.isEmpty()) {
                true
            } else {
                hadith.hadithNumber.contains(trimmed) ||
                hadith.bookNameBengali.lowercase().contains(trimmed) ||
                hadith.banglaText.lowercase().contains(trimmed) ||
                hadith.narrator.lowercase().contains(trimmed) ||
                hadith.chapterNameBengali.lowercase().contains(trimmed) ||
                hadith.arabicText.contains(trimmed)
            }
            matchesBook && matchesQuery
        }
    }
}
