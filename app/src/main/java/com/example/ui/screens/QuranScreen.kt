package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.QariDirectory
import com.example.data.model.Surah
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.ThreeDimensionalIcon
import com.example.ui.components.Icon3DType
import com.example.ui.theme.*
import com.example.ui.viewmodel.QuranAudioMode
import com.example.ui.viewmodel.QuranUiState
import com.example.ui.viewmodel.QuranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    quranViewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val uiState by quranViewModel.uiState.collectAsState()
    val playbackState by quranViewModel.playbackState.collectAsState()
    val bookmarks by quranViewModel.bookmarks.collectAsState()
    val surahDownloads by quranViewModel.surahDownloads.collectAsState()
    val downloadedSurahs by quranViewModel.downloadedSurahNumbers.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) } // 0: Surah Index, 1: Bookmarks
    var showSettingsSheet by remember { mutableStateOf(false) }
    var selectedWordForTadabbur by remember { mutableStateOf<Pair<com.example.data.model.QuranWord, Ayah>?>(null) }

    BackHandler(enabled = uiState.selectedSurah != null) {
        quranViewModel.closeSurah()
    }

    // Track real active Quran reading/listening minutes (zero-based for new users)
    val isActivelyReadingOrListening = uiState.selectedSurah != null || playbackState.isPlaying
    LaunchedEffect(isActivelyReadingOrListening) {
        if (isActivelyReadingOrListening) {
            while (true) {
                kotlinx.coroutines.delay(60_000L)
                quranViewModel.recordQuranReadingMinute(1)
            }
        }
    }

    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.selectedSurah == null) {
            // Surah Index Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // 1. TOP QUICK BAR (4 PRIMARY TABS + SETTINGS GEAR)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkEmeraldElevated)
                            .border(0.5.dp, HairlineBorder, RoundedCornerShape(12.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        TabPill(
                            title = uiText("তিলাওয়াত (১১৪)", "Recite (114)"),
                            isSelected = activeSubTab == 0,
                            onClick = {
                                activeSubTab = 0
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            modifier = Modifier.weight(1.1f)
                        )
                        TabPill(
                            title = uiText("তাফসির ও অনুধাবন", "Tafsir"),
                            isSelected = activeSubTab == 1,
                            onClick = {
                                activeSubTab = 1
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            modifier = Modifier.weight(1.3f)
                        )
                        TabPill(
                            title = uiText("অনুসন্ধান", "Search"),
                            isSelected = activeSubTab == 2,
                            onClick = {
                                activeSubTab = 2
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            modifier = Modifier.weight(0.9f)
                        )
                        TabPill(
                            title = uiText("বুকমার্ক (${formatLocalizedInt(bookmarks.size, uiLang)})", "Saved (${bookmarks.size})"),
                            isSelected = activeSubTab == 3,
                            onClick = {
                                activeSubTab = 3
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            modifier = Modifier.weight(1.0f)
                        )
                    }

                    // Top settings gear icon on the trailing edge
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkEmeraldElevated)
                            .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = uiText("কুরআন অডিও ও সেটিংস", "Quran Audio & Settings"),
                            tint = ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. QUICK ACCESS CHIPS ROW (Ayat al-Kursi, Yaseen, Al-Kahf, Al-Mulk, Ar-Rahman)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    val quickChips = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn") {
                        listOf(
                            Triple("✨ আয়াতুল কুরসী", 2, "সূরা বাক্বারা"),
                            Triple("📖 সূরা ইয়াসীন", 36, "কুরআনের হৃৎপিণ্ড"),
                            Triple("🛡️ সূরা আল-কাহাফ", 18, "জুমু'আর বিশেষ আমল"),
                            Triple("👑 সূরা আল-মুলক", 67, "কবরের আযাব থেকে মুক্তি"),
                            Triple("🌸 সূরা আর-রহমান", 55, "নেয়ামতের ঘোষণা")
                        )
                    } else {
                        listOf(
                            Triple("✨ ${localizedText(uiLang, "আয়াতুল কুরসী", "Ayatul Kursi")}", 2, "Al-Baqarah"),
                            Triple("📖 ${localizedText(uiLang, "সূরা ইয়াসীন", "Surah Yaseen")}", 36, "Heart of the Quran"),
                            Triple("🛡️ ${localizedText(uiLang, "সূরা আল-কাহাফ", "Surah Al-Kahf")}", 18, "Friday Sunnah"),
                            Triple("👑 ${localizedText(uiLang, "সূরা আল-মুলক", "Surah Al-Mulk")}", 67, "Nightly Protection"),
                            Triple("🌸 ${localizedText(uiLang, "সূরা আর-রহমান", "Surah Ar-Rahman")}", 55, "Divine Favors")
                        )
                    }
                    items(quickChips) { (chipLabel, surahNumber, _) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkEmeraldSurface)
                                .border(0.5.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    val surah = uiState.filteredSurahs.find { it.number == surahNumber }
                                    if (surah != null) {
                                        quranViewModel.openSurah(surah)
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = chipLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (activeSubTab) {
                    0 -> {
                        // TAB 0: 114 Surahs Recitation Index
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.filteredSurahs, key = { it.number }) { surah ->
                                val isBookmarked = bookmarks.any {
                                    it.key == "surah_${surah.number}" || (it.surahNumber == surah.number && it.ayahNumber == 0)
                                }
                                val removedToast = uiText(
                                    "সূরা ${surah.localizedName(contentLang)} বুকমার্ক থেকে সরানো হয়েছে",
                                    "Removed Surah ${surah.localizedName(contentLang)} from bookmarks"
                                )
                                val addedToast = uiText(
                                    "সূরা ${surah.localizedName(contentLang)} সফলভাবে বুকমার্ক করা হয়েছে",
                                    "Bookmarked Surah ${surah.localizedName(contentLang)}"
                                )
                                SurahCard(
                                    surah = surah,
                                    isBookmarked = isBookmarked,
                                    onBookmarkToggle = {
                                        quranViewModel.toggleSurahBookmark(surah)
                                        Toast.makeText(context, if (isBookmarked) removedToast else addedToast, Toast.LENGTH_SHORT).show()
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onClick = {
                                        quranViewModel.openSurah(surah)
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                )
                            }
                        }
                    }
                    1 -> {
                        // TAB 1: Tafsir & Scholarly Commentaries Studio
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                FrostedGlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        ThreeDimensionalIcon(
                                            type = Icon3DType.TADABBUR_QURAN,
                                            size = 32.dp,
                                            isActive = true,
                                            accentColor = ChampagneGold
                                        )
                                        Column {
                                            Text(
                                                text = uiText("তাফসীর ও আধ্যাত্মিক অনুধাবন স্টুডিও", "Tafsir & Spiritual Reflection Studio"),
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    color = ChampagneGold,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = uiText(
                                                    "ইবনে কাসীর, আস-সা'দী ও মা'আরিফুল কুরআনের বিশুদ্ধ ব্যাখ্যা",
                                                    "Authentic insights from Ibn Kathir, As-Sa'di & Ma'ariful Quran"
                                                ),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = SlateGrayMuted,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            items(com.example.data.model.TafsirRepository.TAFSIR_ARTICLES, key = { it.id }) { article ->
                                TafsirArticleCard(
                                    article = article,
                                    onReadSurah = {
                                        val surah = uiState.filteredSurahs.find { it.number == article.surahNumber }
                                        if (surah != null) {
                                            quranViewModel.openSurah(surah)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    2 -> {
                        // TAB 2: Search with live instant filter
                        Column(modifier = Modifier.fillMaxSize()) {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = { quranViewModel.search(com.example.data.security.InputSanitizer.sanitizeSearchQuery(it, 100)) },
                                placeholder = {
                                    Text(
                                        text = uiText("সূরা খুঁজুন (নাম বা নম্বর)...", "Search Surah (name or number)..."),
                                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateGrayMuted)
                                    )
                                },
                                leadingIcon = {
                                    ThreeDimensionalIcon(
                                        type = Icon3DType.SEARCH_LENS,
                                        size = 22.dp,
                                        isActive = true,
                                        accentColor = ChampagneGold
                                    )
                                },
                                trailingIcon = {
                                    if (uiState.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { quranViewModel.search("") }) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = uiText("মুছুন", "Clear"),
                                                tint = SlateGrayMuted
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("quran_search_field"),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DarkEmeraldSurface,
                                    unfocusedContainerColor = DarkEmeraldSurface,
                                    focusedBorderColor = ChampagneGold.copy(alpha = 0.8f),
                                    unfocusedBorderColor = HairlineBorder,
                                    cursorColor = ChampagneGold,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = uiText(
                                    "খোঁজার ফলাফল: ${formatLocalizedInt(uiState.filteredSurahs.size, uiLang)}টি সূরা",
                                    "Search results: ${uiState.filteredSurahs.size} Surahs"
                                ),
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(uiState.filteredSurahs, key = { it.number }) { surah ->
                                    val isBookmarked = bookmarks.any {
                                        it.key == "surah_${surah.number}" || (it.surahNumber == surah.number && it.ayahNumber == 0)
                                    }
                                    val removedToast = uiText(
                                        "সূরা ${surah.localizedName(contentLang)} বুকমার্ক থেকে সরানো হয়েছে",
                                        "Removed Surah ${surah.localizedName(contentLang)} from bookmarks"
                                    )
                                    val addedToast = uiText(
                                        "সূরা ${surah.localizedName(contentLang)} সফলভাবে বুকমার্ক করা হয়েছে",
                                        "Bookmarked Surah ${surah.localizedName(contentLang)}"
                                    )
                                    SurahCard(
                                        surah = surah,
                                        isBookmarked = isBookmarked,
                                        onBookmarkToggle = {
                                            quranViewModel.toggleSurahBookmark(surah)
                                            Toast.makeText(context, if (isBookmarked) removedToast else addedToast, Toast.LENGTH_SHORT).show()
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        onClick = {
                                            quranViewModel.openSurah(surah)
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    3 -> {
                        // TAB 3: Bookmarks List
                        if (bookmarks.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    ThreeDimensionalIcon(
                                        type = Icon3DType.BOOKMARK_RIBBON,
                                        size = 54.dp,
                                        isActive = false,
                                        accentColor = SlateGrayMuted
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = uiText("কোনো আয়াত বুকমার্ক করা নেই", "No bookmarked Ayahs yet"),
                                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateGrayMuted)
                                    )
                                    Text(
                                        text = uiText(
                                            "তিলাওয়াতের সময় বুকমার্ক আইকনে ট্যাপ করে সংরক্ষণ করুন",
                                            "Tap the bookmark icon while reading to save"
                                        ),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SlateGrayMuted.copy(alpha = 0.7f),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(bookmarks, key = { it.key }) { bookmark ->
                                    BookmarkCard(
                                        bookmark = bookmark,
                                        onClick = {
                                            val surah = uiState.filteredSurahs.find { it.number == bookmark.surahNumber }
                                            if (surah != null) {
                                                quranViewModel.openSurah(surah)
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Surah Reader View
            val surah = uiState.selectedSurah!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                // Top Navigation & Font Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { quranViewModel.closeSurah() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = surah.localizedName(contentLang),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = ChampagneGold
                            )
                        )
                        Text(
                            text = "${surah.nameArabic} • ${formatLocalizedInt(surah.numberOfAyahs, uiLang)} ${uiText("আয়াত", "Ayahs")} • ${surah.localizedRevelation(uiLang)}",
                            style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                        )
                    }

                    // Font Size Adjustment & Surah Bookmark
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isSurahBookmarked = bookmarks.any {
                            it.key == "surah_${surah.number}" || (it.surahNumber == surah.number && it.ayahNumber == 0)
                        }
                        val removedToast = uiText(
                            "সূরা ${surah.localizedName(contentLang)} বুকমার্ক থেকে সরানো হয়েছে",
                            "Removed Surah ${surah.localizedName(contentLang)} from bookmarks"
                        )
                        val addedToast = uiText(
                            "সূরা ${surah.localizedName(contentLang)} সফলভাবে বুকমার্ক করা হয়েছে",
                            "Bookmarked Surah ${surah.localizedName(contentLang)}"
                        )
                        IconButton(
                            onClick = {
                                quranViewModel.toggleSurahBookmark(surah)
                                Toast.makeText(context, if (isSurahBookmarked) removedToast else addedToast, Toast.LENGTH_SHORT).show()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        ) {
                            Icon(
                                imageVector = if (isSurahBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = uiText("সূরা বুকমার্ক", "Bookmark Surah"),
                                tint = if (isSurahBookmarked) ChampagneGold else SlateGrayMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (uiState.fontSizeSp > 18) quranViewModel.setFontSize(uiState.fontSizeSp - 2)
                            }
                        ) {
                            Text(
                                text = "A-",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted)
                            )
                        }
                        IconButton(
                            onClick = {
                                if (uiState.fontSizeSp < 32) quranViewModel.setFontSize(uiState.fontSizeSp + 2)
                            }
                        ) {
                            Text(
                                text = "A+",
                                style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. DUAL AUDIO PLAYBACK MODES CONTROL CARD
                FrostedGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (playbackState.isPlaying) MintGlow.copy(alpha = 0.5f) else HairlineBorder
                ) {
                    // Mode Toggle Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkEmeraldElevated)
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val isContinuous = uiState.audioMode == QuranAudioMode.FULL_SURAH_CONTINUOUS
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isContinuous) ChampagneGold else Color.Transparent)
                                .clickable {
                                    quranViewModel.setAudioMode(QuranAudioMode.FULL_SURAH_CONTINUOUS)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                                    contentDescription = null,
                                    tint = if (isContinuous) ObsidianBackground else TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = uiText("একনাগাড়ে সম্পূর্ণ সূরা", "Continuous Full Surah"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isContinuous) ObsidianBackground else TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        val isVerseByVerse = uiState.audioMode == QuranAudioMode.VERSE_BY_VERSE
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isVerseByVerse) MintGlow else Color.Transparent)
                                .clickable {
                                    quranViewModel.setAudioMode(QuranAudioMode.VERSE_BY_VERSE)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FormatListNumbered,
                                    contentDescription = null,
                                    tint = if (isVerseByVerse) ObsidianBackground else TextPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = uiText("আয়াতওয়ারী পৃথক", "Verse by Verse"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isVerseByVerse) ObsidianBackground else TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Specific Interactive Actions
                    if (uiState.audioMode == QuranAudioMode.FULL_SURAH_CONTINUOUS) {
                        val isSurahPlaying = playbackState.isPlaying && uiState.currentPlayingAyah != null
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isSurahPlaying) {
                                        uiText("সূরা একনাগাড়ে পাঠ হচ্ছে...", "Playing Surah continuously...")
                                    } else {
                                        uiText("প্রথম থেকে শেষ আয়াত অবিরাম শুনুন", "Listen continuously from Ayah 1 to end")
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSurahPlaying) MintGlow else ChampagneGold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = if (isSurahPlaying) {
                                        uiText(
                                            "চলছে: আয়াত ${formatLocalizedInt(uiState.currentPlayingAyah?.ayahNumber ?: 0, uiLang)} / ${formatLocalizedInt(surah.numberOfAyahs, uiLang)}",
                                            "Playing: Ayah ${uiState.currentPlayingAyah?.ayahNumber} / ${surah.numberOfAyahs}"
                                        )
                                    } else {
                                        uiText(
                                            "বিরামহীন ধারাবাহিক তিলাওয়াত (${formatLocalizedInt(surah.numberOfAyahs, uiLang)} আয়াত)",
                                            "Gapless continuous recitation (${surah.numberOfAyahs} Ayahs)"
                                        )
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                                )
                            }

                            Button(
                                onClick = {
                                    if (isSurahPlaying) {
                                        quranViewModel.audioPlayer.togglePlayPause()
                                    } else {
                                        quranViewModel.playSurahFromStart()
                                    }
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSurahPlaying) MintGlow else ChampagneGold,
                                    contentColor = ObsidianBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSurahPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isSurahPlaying) uiText("বিরতি", "Pause") else uiText("সম্পূর্ণ সূরা শুনুন", "Play Full Surah"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Verse-by-Verse mode with Single Ayah Loop toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiText("আয়াতওয়ারী পৃথক অধ্যয়ন ও হিফজ", "Verse-by-Verse Study & Hifz"),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MintGlow,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = uiText(
                                        "নির্দিষ্ট আয়াত বারবার শুনে মাখরাজ ও হিফজ অনুশীলন করুন",
                                        "Repeat specific Ayahs to practice pronunciation and memorization"
                                    ),
                                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (uiState.isLoopingSingleAyah) MintGlow.copy(alpha = 0.2f) else DarkEmeraldElevated)
                                    .border(
                                        0.5.dp,
                                        if (uiState.isLoopingSingleAyah) MintGlow else HairlineBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        quranViewModel.toggleLoopSingleAyah()
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isLoopingSingleAyah) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                                        contentDescription = null,
                                        tint = if (uiState.isLoopingSingleAyah) MintGlow else SlateGrayMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (uiState.isLoopingSingleAyah) uiText("লুপ চালু", "Loop On") else uiText("লুপ বন্ধ", "Loop Off"),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (uiState.isLoopingSingleAyah) MintGlow else SlateGrayMuted,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Offline Surah Download & Instant Cache Status Row
                    val dlKey = com.example.data.audio.OfflineAudioManager.keyFor(uiState.selectedQariId, surah.number)
                    val dlProgress = surahDownloads[dlKey]
                    val isSurahOffline = downloadedSurahs.contains(surah.number)
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = HairlineBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    dlProgress?.isDownloading == true ->
                                        uiText(
                                            "অফলাইনে ডাউনলোড হচ্ছে (${formatLocalizedInt(dlProgress.downloadedAyahs, uiLang)}/${formatLocalizedInt(dlProgress.totalAyahs, uiLang)})...",
                                            "Downloading offline (${dlProgress.downloadedAyahs}/${dlProgress.totalAyahs})..."
                                        )
                                    isSurahOffline ->
                                        uiText("অফলাইন অডিও সংরক্ষিত (ইন্টারনেট ছাড়াই চলবে)", "Saved offline (plays without internet)")
                                    else ->
                                        uiText("অবিরাম ও অফলাইন শুনতে সূরা ডাউনলোড করুন", "Download Surah for 100% offline playback")
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSurahOffline) MintGlow else SlateGrayMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSurahOffline) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                            if (dlProgress?.isDownloading == true && dlProgress.totalAyahs > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (dlProgress.downloadedAyahs.toFloat() / dlProgress.totalAyahs.toFloat()).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MintGlow,
                                    trackColor = DarkEmeraldElevated
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (dlProgress?.isDownloading == true) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = ChampagneGold
                            )
                        } else if (isSurahOffline) {
                            TextButton(
                                onClick = {
                                    quranViewModel.deleteDownloadedSurah(surah)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                                modifier = Modifier.testTag("surah_delete_offline_btn")
                            ) {
                                Icon(Icons.Filled.CloudDone, contentDescription = null, tint = MintGlow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(uiText("অফলাইন মুছুন", "Remove"), color = SlateGrayMuted, fontSize = 11.sp)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    quranViewModel.downloadSurah(surah)
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                border = androidx.compose.foundation.BorderStroke(0.75.dp, ChampagneGold.copy(alpha = 0.6f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("surah_download_offline_btn")
                            ) {
                                Icon(Icons.Filled.CloudDownload, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(uiText("ডাউনলোড", "Download"), color = ChampagneGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. HIFZ & MEMORIZATION ASSISTANT
                FrostedGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (uiState.isHifzMode) DarkEmeraldElevated else DarkEmeraldSurface,
                    borderColor = if (uiState.isHifzMode) ChampagneGold.copy(alpha = 0.6f) else HairlineBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Psychology,
                                contentDescription = null,
                                tint = if (uiState.isHifzMode) ChampagneGold else SlateGrayMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = uiText("হিফজ ও মুখস্থকরণ সহকারী", "Hifz & Memorization Assistant"),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = if (uiState.isHifzMode) ChampagneGold else TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = uiText(
                                        "আয়াত বারবার শুনে মুখস্থ করা ও স্মৃতি যাচাই মোড",
                                        "Loop Ayahs and hide Arabic script to test memorization"
                                    ),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateGrayMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Switch(
                            checked = uiState.isHifzMode,
                            onCheckedChange = {
                                quranViewModel.toggleHifzMode()
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ChampagneGold,
                                checkedTrackColor = MintGlow.copy(alpha = 0.5f),
                                uncheckedThumbColor = SlateGrayMuted,
                                uncheckedTrackColor = DarkEmeraldSurface
                            )
                        )
                    }

                    if (uiState.isHifzMode) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = HairlineBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Looping & Repetition Selector
                        Text(
                            text = uiText("প্রতিটি আয়াত পুনরাবৃত্তির সংখ্যা (Loop Count):", "Repeat count per Ayah:"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val repeatOptions = if (com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn") {
                                listOf(1 to "১ বার", 3 to "৩ বার", 5 to "৫ বার", 10 to "১০ বার", -1 to "লুপ ∞")
                            } else {
                                listOf(1 to "1x", 3 to "3x", 5 to "5x", 10 to "10x", -1 to "Loop ∞")
                            }
                            repeatOptions.forEach { (target, label) ->
                                val isSelected = uiState.ayahRepeatTarget == target
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ChampagneGold else DarkEmeraldSurface)
                                        .border(
                                            0.5.dp,
                                            if (isSelected) ChampagneGold else HairlineBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            quranViewModel.setAyahRepeatTarget(target)
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) ObsidianBackground else TextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Hide/Reveal Arabic Text Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (uiState.isArabicTextHidden) Color(0xFF231805) else DarkEmeraldSurface)
                                .border(
                                    0.5.dp,
                                    if (uiState.isArabicTextHidden) ChampagneGold else HairlineBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    quranViewModel.toggleHideArabicText()
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.isArabicTextHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null,
                                    tint = if (uiState.isArabicTextHidden) ChampagneGold else MintGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = if (uiState.isArabicTextHidden) {
                                            uiText("আরবি টেক্সট লুকানো আছে (মেমোরি টেস্ট)", "Arabic Script Hidden (Memory Test)")
                                        } else {
                                            uiText("আরবি টেক্সট দৃশ্যমান আছে", "Arabic Script Visible")
                                        },
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = if (uiState.isArabicTextHidden) ChampagneGold else TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = if (uiState.isArabicTextHidden) {
                                            uiText("আয়াত মুখস্থ পাঠ করুন, মেলাতে আয়াতে ট্যাপ করুন", "Recite from memory, tap an Ayah to reveal")
                                        } else {
                                            uiText("লুকিয়ে স্মৃতি পরীক্ষা করতে ট্যাপ করুন", "Tap to hide Arabic script and test recall")
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SlateGrayMuted,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (uiState.isArabicTextHidden) Icons.Filled.CheckCircle else Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = if (uiState.isArabicTextHidden) ChampagneGold else SlateGrayMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ayahs List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.ayahs, key = { "${it.surahNumber}_${it.ayahNumber}" }) { ayah ->
                        val isPlayingThis = playbackState.isPlaying && uiState.currentPlayingAyah == ayah
                        val isBufferingThis = playbackState.isBuffering && uiState.currentPlayingAyah == ayah
                        val isBookmarked = bookmarks.any { it.key == "${ayah.surahNumber}_${ayah.ayahNumber}" }
                        val isRevealed = uiState.revealedAyahNumbers.contains(ayah.ayahNumber)

                        AyahCard(
                            ayah = ayah,
                            fontSizeSp = uiState.fontSizeSp,
                            isPlaying = isPlayingThis,
                            isBuffering = isBufferingThis,
                            isBookmarked = isBookmarked,
                            isContinuousMode = uiState.audioMode == QuranAudioMode.FULL_SURAH_CONTINUOUS,
                            isLoopingSingleAyah = uiState.isLoopingSingleAyah && isPlayingThis,
                            isHifzMode = uiState.isHifzMode,
                            isArabicTextHidden = uiState.isArabicTextHidden,
                            isRevealed = isRevealed,
                            repeatProgress = if (isPlayingThis && uiState.isHifzMode) {
                                "${uiState.currentAyahPlayedCount + 1}/${if (uiState.ayahRepeatTarget == -1) "∞" else uiState.ayahRepeatTarget}"
                            } else null,
                            onToggleReveal = { quranViewModel.toggleRevealAyah(ayah.ayahNumber) },
                            onPlay = {
                                quranViewModel.playAyahAudio(ayah)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onBookmark = {
                                quranViewModel.toggleBookmark(ayah)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onWordClick = { word ->
                                selectedWordForTadabbur = Pair(word, ayah)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        )
                    }
                }
            }
        }

        // 3. Floating Bottom Audio Dock for Quran
        val showPlayer = playbackState.isPlaying || playbackState.isBuffering || uiState.currentPlayingAyah != null
        AnimatedVisibility(
            visible = showPlayer,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            FloatingAudioPlayerBar(
                playbackState = playbackState,
                uiState = uiState,
                onPauseResume = { quranViewModel.audioPlayer.togglePlayPause() },
                onStop = { quranViewModel.audioPlayer.stop() },
                onNextAyah = {
                    quranViewModel.playNextAyah()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onPreviousAyah = {
                    quranViewModel.playPreviousAyah()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onToggleMode = {
                    val nextMode = if (uiState.audioMode == QuranAudioMode.FULL_SURAH_CONTINUOUS) {
                        QuranAudioMode.VERSE_BY_VERSE
                    } else {
                        QuranAudioMode.FULL_SURAH_CONTINUOUS
                    }
                    quranViewModel.setAudioMode(nextMode)
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onToggleLoop = {
                    quranViewModel.toggleLoopSingleAyah()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            )
        }

        // 4. Word-by-Word & Tadabbur Bottom Sheet
        selectedWordForTadabbur?.let { (word, ayah) ->
            WordTadabburBottomSheet(
                word = word,
                ayah = ayah,
                onDismiss = { selectedWordForTadabbur = null }
            )
        }

        // 5. Quran Settings Bottom Sheet
        if (showSettingsSheet) {
            QuranSettingsBottomSheet(
                uiState = uiState,
                onToggleAudioMode = {
                    val nextMode = if (uiState.audioMode == QuranAudioMode.FULL_SURAH_CONTINUOUS) {
                        QuranAudioMode.VERSE_BY_VERSE
                    } else {
                        QuranAudioMode.FULL_SURAH_CONTINUOUS
                    }
                    quranViewModel.setAudioMode(nextMode)
                },
                onSelectQari = { qariId ->
                    quranViewModel.setQari(qariId)
                },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}

@Composable
fun SurahCard(
    surah: Surah,
    isBookmarked: Boolean = false,
    onBookmarkToggle: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val isMeccan = surah.revelationType.equals("Meccan", ignoreCase = true)
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current

    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("surah_card_${surah.number}")
            .clickable(onClick = onClick),
        cornerRadius = 16.dp,
        backgroundColor = DarkEmeraldSurface,
        borderColor = if (isBookmarked) ChampagneGold.copy(alpha = 0.55f) else ChampagneGold.copy(alpha = 0.20f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Left: Circular index badge wrapped in a gold ring
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkEmeraldElevated)
                        .border(1.dp, if (isBookmarked) ChampagneGold else ChampagneGold.copy(alpha = 0.65f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatLocalizedInt(surah.number, uiLang),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }

                // Middle: Surah Localized Name + Inline Bookmark Button, meaning/transliteration, Ayah count, Makki/Madani status pill
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = surah.localizedName(contentLang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )

                        // Inline Quick Bookmark Icon
                        if (onBookmarkToggle != null) {
                            IconButton(
                                onClick = onBookmarkToggle,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = uiText("সূরা বুকমার্ক করুন", "Bookmark Surah"),
                                    tint = if (isBookmarked) ChampagneGold else SlateGrayMuted.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val subName = if (com.example.data.util.SupportedLanguage.normalizeCode(contentLang) == "bn") {
                            surah.nameEnglish
                        } else {
                            surah.localizedTranslation(contentLang)
                        }
                        Text(
                            text = "$subName • ${formatLocalizedInt(surah.numberOfAyahs, uiLang)} ${uiText("আয়াত", "Ayahs")}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isMeccan) ChampagneGold.copy(alpha = 0.12f) else MintGlow.copy(alpha = 0.12f))
                                .border(
                                    0.5.dp,
                                    if (isMeccan) ChampagneGold.copy(alpha = 0.45f) else MintGlow.copy(alpha = 0.45f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 1.5.dp)
                        ) {
                            Text(
                                text = surah.localizedRevelation(uiLang),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isMeccan) ChampagneGold else MintGlow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            // Right: Calligraphic Arabic Surah name in Amiri font colored in Brushed Champagne Gold
            Text(
                text = surah.nameArabic,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = AmiriFontFamily,
                    color = ChampagneGold,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun BookmarkCard(
    bookmark: com.example.data.local.BookmarkEntity,
    onClick: () -> Unit
) {
    val isSurahBookmark = bookmark.ayahNumber <= 0
    val uiLang = LocalAppLanguage.current
    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isSurahBookmark) {
                            uiText(
                                "সূরা ${bookmark.surahNameEnglish} (সম্পূর্ণ সূরা)",
                                "Surah ${bookmark.surahNameEnglish} (Full Surah)"
                            )
                        } else {
                            uiText(
                                "${bookmark.surahNameEnglish} - আয়াত ${formatLocalizedInt(bookmark.ayahNumber, uiLang)}",
                                "${bookmark.surahNameEnglish} - Ayah ${bookmark.ayahNumber}"
                            )
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                    if (isSurahBookmark) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MintGlow.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = uiText("সূরা বুকমার্ক", "Surah Saved"),
                                color = MintGlow,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isSurahBookmark) {
                        uiText("সংরক্ষিত প্রিয় সূরা • ${bookmark.ayahTextArabic}", "Saved Surah • ${bookmark.surahNameArabic}")
                    } else {
                        bookmark.ayahTextArabic
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = if (isSurahBookmark) null else AmiriFontFamily,
                        color = TextPrimary,
                        fontSize = if (isSurahBookmark) 13.sp else 16.sp,
                        textAlign = if (isSurahBookmark) TextAlign.Left else TextAlign.Right
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            ThreeDimensionalIcon(
                type = Icon3DType.BOOKMARK_RIBBON,
                size = 26.dp,
                isActive = true,
                accentColor = ChampagneGold
            )
        }
    }
}

@Composable
fun AyahCard(
    ayah: Ayah,
    fontSizeSp: Int,
    isPlaying: Boolean,
    isBuffering: Boolean,
    isBookmarked: Boolean,
    isContinuousMode: Boolean,
    isLoopingSingleAyah: Boolean,
    isHifzMode: Boolean = false,
    isArabicTextHidden: Boolean = false,
    isRevealed: Boolean = false,
    repeatProgress: String? = null,
    onToggleReveal: () -> Unit = {},
    onPlay: () -> Unit,
    onBookmark: () -> Unit,
    onWordClick: (com.example.data.model.QuranWord) -> Unit = {}
) {
    var showWordByWord by remember { mutableStateOf(false) }
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current

    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_card_${ayah.ayahNumber}"),
        borderColor = if (isPlaying) MintGlow.copy(alpha = 0.7f) else HairlineBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) MintGlow.copy(alpha = 0.2f) else DarkEmeraldElevated)
                        .border(0.5.dp, if (isPlaying) MintGlow else HairlineBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatLocalizedInt(ayah.ayahNumber, uiLang),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isPlaying) MintGlow else ChampagneGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                if (isPlaying) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isHifzMode) ChampagneGold else MintGlow)
                        )
                        if (isHifzMode && repeatProgress != null) {
                            Text(
                                text = uiText("হিফজ লুপ: $repeatProgress", "Hifz Loop: $repeatProgress"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ChampagneGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        } else {
                            Text(
                                text = when {
                                    isLoopingSingleAyah -> uiText("লুপে পাঠ হচ্ছে...", "Looping Ayah...")
                                    isContinuousMode -> uiText("একনাগাড়ে পাঠ...", "Continuous play...")
                                    else -> uiText("পাঠ হচ্ছে...", "Playing...")
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MintGlow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (showWordByWord) ChampagneGold.copy(alpha = 0.2f) else DarkEmeraldElevated)
                        .border(0.5.dp, if (showWordByWord) ChampagneGold else HairlineBorder, RoundedCornerShape(8.dp))
                        .clickable { showWordByWord = !showWordByWord }
                        .padding(horizontal = 7.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = if (showWordByWord) ChampagneGold else SlateGrayMuted,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = uiText("শব্দে শব্দে", "Word-by-Word"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (showWordByWord) ChampagneGold else SlateGrayMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                IconButton(onClick = onBookmark, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) MintGlow else SlateGrayMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onPlay, modifier = Modifier.size(32.dp)) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ChampagneGold,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                            contentDescription = "Play Audio",
                            tint = if (isPlaying) MintGlow else ChampagneGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val shouldHideArabic = isHifzMode && isArabicTextHidden && !isRevealed

        if (shouldHideArabic) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkEmeraldElevated.copy(alpha = 0.85f))
                    .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onToggleReveal)
                    .padding(vertical = 16.dp, horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = uiText("আয়াতটি মুখস্থ পাঠ করুন (দেখতে ট্যাপ করুন)", "Recite from memory (tap to reveal)"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isHifzMode && isArabicTextHidden && isRevealed) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onToggleReveal,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = SlateGrayMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(uiText("পুনরায় লুকান", "Hide again"), fontSize = 10.sp, color = SlateGrayMuted)
                        }
                    }
                }

                // Arabic text with complete Harakat and Amiri Font (always untouched Arabic)
                Text(
                    text = ayah.textArabic,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = AmiriFontFamily,
                        fontSize = fontSizeSp.sp,
                        lineHeight = (fontSizeSp * 1.6).sp,
                        textAlign = TextAlign.Right,
                        color = LocalAppColors.current.arabicTextColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (showWordByWord) {
            val words = remember(ayah) {
                com.example.data.model.WordByWordRepository.getWordsForAyah(
                    ayah.surahNumber,
                    ayah.ayahNumber,
                    ayah.textArabic
                )
            }
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text(
                    text = uiText("শব্দের মূল ধাতু ও ব্যাকরণ দেখতে শব্দে ট্যাপ করুন:", "Tap any word to inspect its root & grammar:"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ChampagneGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = true
                ) {
                    items(words) { word ->
                        WordChipCard(
                            word = word,
                            onClick = { onWordClick(word) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content Language Transliteration & Translation (independent of UI language)
        val primaryTransliteration = ayah.localizedTransliteration(contentLang)
        if (primaryTransliteration.isNotBlank()) {
            Text(
                text = "${uiText("উচ্চারণ:", "Transliteration:")} $primaryTransliteration",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = LocalAppColors.current.transliterationTextColor,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            text = "${uiText("অর্থ:", "Translation:")} ${ayah.localizedTranslation(contentLang)}",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = LocalAppColors.current.translationTextColor,
                lineHeight = 22.sp
            )
        )
    }
}

@Composable
fun WordChipCard(
    word: com.example.data.model.QuranWord,
    onClick: () -> Unit
) {
    val contentLang = LocalContentLanguage.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkEmeraldElevated)
            .border(0.5.dp, HairlineBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = word.arabic,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = AmiriFontFamily,
                    color = ChampagneGold,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = word.localizedMeaning(contentLang),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = word.localizedTransliteration(contentLang),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MintGlow,
                    fontSize = 9.sp
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordTadabburBottomSheet(
    word: com.example.data.model.QuranWord,
    ayah: Ayah,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SlateGrayMuted.copy(alpha = 0.5f))
            )
        }
    ) {
        val uiLang = LocalAppLanguage.current
        val contentLang = LocalContentLanguage.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ChampagneGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = uiText("শব্দে শব্দে কুরআন ও তাদাব্বুর", "Word-by-Word Quran & Tadabbur"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkEmeraldElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = uiText("আয়াত ${formatLocalizedInt(ayah.ayahNumber, uiLang)}", "Ayah ${ayah.ayahNumber}"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MintGlow,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Word Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(DarkEmeraldElevated, ObsidianBackground)
                        )
                    )
                    .border(1.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(vertical = 18.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = word.arabic,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = AmiriFontFamily,
                            color = ChampagneGold,
                            fontSize = 38.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${uiText("উচ্চারণ:", "Transliteration:")} ${word.localizedTransliteration(contentLang)}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MintGlow,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiText("অর্থ:", "Meaning:")} \"${word.localizedMeaning(contentLang)}\"",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Linguistic context & Root word cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Root Word Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldElevated)
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = uiText("মূল ধাতু", "Root Letters"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = word.rootWord,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = AmiriFontFamily,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }
                }

                // Grammar Role Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldElevated)
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = uiText("ব্যাকরণিক পরিচয়", "Grammar Form"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = word.localizedGrammarContext(uiLang),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Full Ayah Translation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkEmeraldElevated.copy(alpha = 0.6f))
                    .border(0.5.dp, HairlineBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = uiText("সম্পূর্ণ আয়াতের অনুবাদ:", "Full Ayah Translation:"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ayah.localizedTranslation(LocalContentLanguage.current),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingAudioPlayerBar(
    playbackState: com.example.data.audio.PlaybackState,
    uiState: QuranUiState,
    onPauseResume: () -> Unit,
    onStop: () -> Unit,
    onNextAyah: () -> Unit,
    onPreviousAyah: () -> Unit,
    onToggleMode: () -> Unit,
    onToggleLoop: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current
    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        borderColor = MintGlow.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Track Info
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MintGlow.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Headphones,
                        contentDescription = null,
                        tint = MintGlow,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = playbackState.currentTitle.ifBlank {
                            "${uiState.selectedSurah?.localizedName(contentLang) ?: uiText("কুরআন তিলাওয়াত", "Quran Recitation")} • ${uiText("আয়াত", "Ayah")} ${uiState.currentPlayingAyah?.ayahNumber ?: 1}"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (playbackState.error != null) playbackState.error!!
                        else if (playbackState.isBuffering) uiText("বাফারিং হচ্ছে...", "Buffering...")
                        else "${playbackState.currentSubtitle} • ${uiState.audioMode.localizedShortTitle(uiLang)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (playbackState.error != null) ProhibitedRose else MintGlow,
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Mode Indicator Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkEmeraldSurface)
                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable(onClick = onToggleMode)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = uiState.audioMode.localizedShortTitle(uiLang),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Controls Row: Prev, Play/Pause, Next, Loop, Stop
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Loop button for Verse-by-Verse
                if (uiState.audioMode == QuranAudioMode.VERSE_BY_VERSE) {
                    IconButton(onClick = onToggleLoop, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = if (uiState.isLoopingSingleAyah) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                            contentDescription = "Loop",
                            tint = if (uiState.isLoopingSingleAyah) MintGlow else SlateGrayMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Previous Ayah
                IconButton(onClick = onPreviousAyah, modifier = Modifier.size(30.dp)) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous Ayah",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Play / Pause
                IconButton(onClick = onPauseResume, modifier = Modifier.size(34.dp)) {
                    if (playbackState.isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ChampagneGold,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (playbackState.isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                            contentDescription = "Play/Pause",
                            tint = ChampagneGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Next Ayah
                IconButton(onClick = onNextAyah, modifier = Modifier.size(30.dp)) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next Ayah",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Stop
                IconButton(onClick = onStop, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Stop",
                        tint = SlateGrayMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TabPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) DarkEmeraldCard else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (isSelected) ChampagneGold else SlateGrayMuted,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 12.sp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSettingsBottomSheet(
    uiState: QuranUiState,
    onToggleAudioMode: () -> Unit,
    onSelectQari: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(SlateGrayMuted.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = uiText("কুরআন অডিও ও তিলাওয়াত সেটিংস", "Quran Audio & Recitation Settings"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = uiText("বন্ধ করুন", "Close"), tint = SlateGrayMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Audio Playback Mode
            Text(
                text = uiText("অডিও প্লেব্যাক মোড:", "Audio Playback Mode:"),
                style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 12.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isContinuous = uiState.audioMode == QuranAudioMode.FULL_SURAH_CONTINUOUS
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isContinuous) DarkEmeraldElevated else DarkEmeraldSurface)
                        .border(
                            0.5.dp,
                            if (isContinuous) ChampagneGold else HairlineBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { if (!isContinuous) onToggleAudioMode() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiText("ধারাবাহিক পূর্ণ সূরা", "Continuous Full Surah"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isContinuous) ChampagneGold else SlateGrayMuted,
                            fontWeight = if (isContinuous) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isContinuous) DarkEmeraldElevated else DarkEmeraldSurface)
                        .border(
                            0.5.dp,
                            if (!isContinuous) ChampagneGold else HairlineBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { if (isContinuous) onToggleAudioMode() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiText("আয়াতভিত্তিক পাঠ", "Verse by Verse"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (!isContinuous) ChampagneGold else SlateGrayMuted,
                            fontWeight = if (!isContinuous) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Qari Selector Header & Verified Male-Reciter Policy Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiText("ক্বারী নির্বাচন (Qari Selector):", "Select Qari (Reciter):"),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                )

                // Verified 100% Male Reciters Policy Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MintGlow.copy(alpha = 0.12f))
                        .border(0.5.dp, MintGlow.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = uiText("🛡️ ভেরিফায়েড পুরুষ ক্বারী", "🛡️ Verified Male Qaris"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MintGlow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of Renowned Qaris
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QariDirectory.MALE_RECITERS.forEach { qari ->
                    val isSelected = uiState.selectedQariId == qari.id
                    val selectedToast = uiText("ক্বারী: ${qari.name} নির্বাচন করা হয়েছে", "Selected Qari: ${qari.name}")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) DarkEmeraldElevated else DarkEmeraldSurface)
                            .border(
                                width = if (isSelected) 1.dp else 0.5.dp,
                                color = if (isSelected) ChampagneGold else HairlineBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onSelectQari(qari.id)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(context, selectedToast, Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Mic / Reciter Medallion
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) ChampagneGold.copy(alpha = 0.2f) else DarkEmeraldElevated)
                                .border(
                                    0.5.dp,
                                    if (isSelected) ChampagneGold else HairlineBorder,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RecordVoiceOver,
                                contentDescription = null,
                                tint = if (isSelected) ChampagneGold else SlateGrayMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Qari Info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = qari.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = if (isSelected) ChampagneGold else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MintGlow.copy(alpha = 0.15f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = uiText("সক্রিয়", "Active"),
                                            color = MintGlow,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${qari.arabicName} • ${qari.style}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateGrayMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Selected indicator
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = uiText("নির্বাচিত", "Selected"),
                                tint = MintGlow,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .border(1.dp, SlateGrayMuted.copy(alpha = 0.5f), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TafsirArticleCard(
    article: com.example.data.model.TafsirArticle,
    onReadSurah: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        borderColor = if (expanded) ChampagneGold.copy(alpha = 0.7f) else HairlineBorder
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkEmeraldSurface)
                                .border(0.5.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = article.ayahReference,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ChampagneGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = article.surahNameBengali,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = article.themeBengali,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MintGlow,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = article.surahNameArabic,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = ClassicalAmiriFontFamily,
                        color = ChampagneGold.copy(alpha = 0.9f),
                        fontSize = 17.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Snippet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkEmeraldElevated)
                    .padding(10.dp)
            ) {
                Text(
                    text = article.arabicSnippet,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = ClassicalAmiriFontFamily,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        lineHeight = 26.sp,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.translationBengali,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${uiText("উৎস:", "Source:")} ${article.scholarSource}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SlateGrayMuted,
                    fontSize = 10.sp
                )
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = HairlineBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = uiText("তাফসীর ও অনুধাবন সারসংক্ষেপ:", "Tafsir & Reflection Summary:"),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.tafsirSummaryBengali,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = uiText("মূল শিক্ষা ও ঈমানী দীক্ষা:", "Key Spiritual Lessons:"),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MintGlow,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                article.keyLessons.forEach { lesson ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("•", color = ChampagneGold, fontSize = 12.sp)
                        Text(
                            text = lesson,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimary.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) uiText("সংক্ষেপ করুন ▲", "Collapse ▲") else uiText("বিস্তারিত তাফসীর পড়ুন ▼", "Read Detailed Tafsir ▼"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable { expanded = !expanded }
                )

                Button(
                    onClick = onReadSurah,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGlow.copy(alpha = 0.2f),
                        contentColor = MintGlow
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(uiText("সূরা তিলাওয়াত", "Read Surah"), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
