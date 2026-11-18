package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
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
import com.example.data.audio.ArabicTtsManager
import com.example.data.audio.ArabicVoiceTone
import com.example.data.audio.AudioPlayerManager
import com.example.data.audio.PlaybackState
import com.example.data.model.Dua
import com.example.data.model.DuaRepository
import com.example.data.model.PrayerName
import com.example.data.model.WaqtCategory
import com.example.ui.components.CreateDuaListDialog
import com.example.ui.components.CustomDuaCollectionFolderCard
import com.example.ui.components.DuaCategoryCircularCard
import com.example.ui.components.DuaCategoryDirectory
import com.example.ui.components.DuaCategoryItem
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.Icon3DType
import com.example.ui.components.MyDuaCollectionFavoriteCard
import com.example.ui.components.ThreeDimensionalIcon
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DuaScreen(
    audioPlayer: AudioPlayerManager,
    ttsManager: ArabicTtsManager? = null,
    mainViewModel: MainViewModel? = null,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val playbackState by audioPlayer.playbackState.collectAsState()
    val ttsState = ttsManager?.state?.collectAsState()?.value
    val schedule = mainViewModel?.schedule?.collectAsState()?.value

    // Current waqt detection
    val currentWaqtCategory = remember(schedule?.currentWaqt) {
        when (schedule?.currentWaqt) {
            PrayerName.FAJR, PrayerName.SUNRISE -> WaqtCategory.FAJR_MORNING
            PrayerName.DHUHR, PrayerName.ASR -> WaqtCategory.DHUHR_ASR
            PrayerName.MAGHRIB -> WaqtCategory.MAGHRIB_EVENING
            PrayerName.ISHA -> WaqtCategory.ISHA_NIGHT
            else -> DuaRepository.getCurrentWaqtCategory()
        }
    }

    var selectedCategoryItem by remember { mutableStateOf<DuaCategoryItem?>(null) }
    var isFavoritesSelected by remember { mutableStateOf(false) }
    var selectedCustomCollection by remember { mutableStateOf<String?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }
    var showCreateListDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var speakingDuaId by remember { mutableStateOf<String?>(null) }
    val duaCounterMap = remember { mutableStateMapOf<String, Int>() }
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { com.example.data.preference.AppPreferences(context) }
    val db = remember { com.example.data.local.AppDatabase.getInstance(context) }
    val cloudSyncRepo = remember { com.example.data.cloud.CloudSyncRepository(context, prefs, db) }
    val worshipTracker = remember { com.example.data.repository.WorshipTrackerRepository(context, prefs, db, cloudSyncRepo) }
    val persistedFavs by prefs.favoriteDuaIds.collectAsState(initial = emptySet())
    val defaultFavs = remember { setOf("dua_sayyidul_istighfar", "dua_rabbana_atina", "dua_iftar_sahih", "dua_tashahhud_attahiyyat") }
    val favoriteDuaIds = if (persistedFavs.isEmpty()) defaultFavs else persistedFavs
    val customCollections by prefs.customDuaCollections.collectAsState(initial = emptySet())

    val isSubScreenOpen = selectedCategoryItem != null || isFavoritesSelected || selectedCustomCollection != null || isSearchActive

    BackHandler(enabled = isSubScreenOpen) {
        if (searchQuery.isNotEmpty()) {
            searchQuery = ""
        } else {
            selectedCategoryItem = null
            isFavoritesSelected = false
            selectedCustomCollection = null
            isSearchActive = false
        }
    }

    // Synchronize speaking state when TTS finishes
    LaunchedEffect(ttsState?.isSpeaking) {
        if (ttsState?.isSpeaking == false) {
            speakingDuaId = null
        }
    }

    val subScreenDuas = remember(
        selectedCategoryItem,
        isFavoritesSelected,
        selectedCustomCollection,
        isSearchActive,
        searchQuery,
        favoriteDuaIds,
        currentWaqtCategory
    ) {
        var list = when {
            isFavoritesSelected -> DuaRepository.DUAS.filter { favoriteDuaIds.contains(it.id) }
            selectedCustomCollection != null -> DuaRepository.DUAS.filter { favoriteDuaIds.contains(it.id) }
            selectedCategoryItem != null -> DuaRepository.DUAS.filter(selectedCategoryItem!!.filterPredicate)
            else -> DuaRepository.DUAS
        }

        if (searchQuery.isNotBlank()) {
            val query = searchQuery.trim().lowercase()
            list = list.filter {
                it.titleBengali.lowercase().contains(query) ||
                it.titleEnglish.lowercase().contains(query) ||
                it.meaningBengali.lowercase().contains(query) ||
                it.meaningEnglish.lowercase().contains(query) ||
                it.transliterationBengali.lowercase().contains(query) ||
                it.transliteration.lowercase().contains(query) ||
                it.reference.lowercase().contains(query) ||
                it.textArabic.contains(query)
            }
        }
        list
    }

    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isSubScreenOpen) {
            // ================= SUB-SCREEN: CATEGORY / FAVORITES / SEARCH DUAS =================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Header with back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            selectedCategoryItem = null
                            isFavoritesSelected = false
                            selectedCustomCollection = null
                            isSearchActive = false
                            searchQuery = ""
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DarkEmeraldSurface)
                            .border(0.5.dp, HairlineBorder, CircleShape)
                            .testTag("subscreen_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = uiText("ফিরে যান", "Back"),
                            tint = ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        val screenTitle = when {
                            isFavoritesSelected -> uiText("পছন্দের দু'আ (Favourite)", "Favourite Duas")
                            selectedCustomCollection != null -> selectedCustomCollection!!
                            selectedCategoryItem != null -> selectedCategoryItem!!.localizedTitle(uiLang)
                            else -> uiText("দু'আ ও যিকির অনুসন্ধান", "Search Duas & Adhkar")
                        }
                        val screenSubtitle = when {
                            isFavoritesSelected -> uiText("আপনার সংরক্ষিত আমল ও খাস মোনাজাত", "Your bookmarked supplications")
                            selectedCustomCollection != null -> uiText("কাস্টম আমল তালিকা", "Custom Dua list")
                            selectedCategoryItem != null -> selectedCategoryItem!!.localizedSubtitle(uiLang)
                            else -> uiText("হিসনুল মুসলিম সহীহ হাদিস ভাণ্ডার", "Authentic Hisnul Muslim collection")
                        }

                        Text(
                            text = screenTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = screenSubtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkEmeraldSurface)
                            .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = uiText("${formatLocalizedInt(subScreenDuas.size, uiLang)}টি", "${subScreenDuas.size}"),
                            color = MintGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Search Bar inside Sub-screen
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subscreen_dua_search_input"),
                    placeholder = {
                        Text(
                            text = uiText("এই ক্যাটাগরিতে দু'আ বা অর্থ খুঁজুন...", "Search dua or meaning in this category..."),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        )
                    },
                    leadingIcon = {
                        ThreeDimensionalIcon(
                            type = Icon3DType.SEARCH_LENS,
                            size = 18.dp,
                            isActive = true,
                            accentColor = ChampagneGold
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear",
                                    tint = SlateGrayMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkEmeraldSurface,
                        unfocusedContainerColor = DarkEmeraldSurface,
                        focusedBorderColor = ChampagneGold.copy(alpha = 0.8f),
                        unfocusedBorderColor = HairlineBorder,
                        cursorColor = ChampagneGold,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Soulful Male Voice Tone Selector Bar
                if (ttsManager != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkEmeraldSurface)
                            .border(0.5.dp, HairlineBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ThreeDimensionalIcon(
                                type = Icon3DType.ARABIC_TTS,
                                size = 18.dp,
                                isActive = true,
                                accentColor = MintGlow
                            )
                            Text(
                                text = uiText("সুললিত পুরুষ সুর:", "Recitation Tone:"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MintGlow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            ArabicVoiceTone.values().forEach { tone ->
                                val isSelected = ttsState?.selectedTone == tone
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MintGlow.copy(alpha = 0.2f) else DarkEmeraldElevated)
                                        .border(
                                            0.5.dp,
                                            if (isSelected) MintGlow else HairlineBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            ttsManager.setVoiceTone(tone)
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = when (tone) {
                                            ArabicVoiceTone.SOULFUL_MELODIOUS -> uiText("সুললিত", "Melodious")
                                            ArabicVoiceTone.DEEP_REVERENT -> uiText("গম্ভীর", "Reverent")
                                            ArabicVoiceTone.CLEAR_TAJWEED -> uiText("তালিম", "Tajweed")
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) MintGlow else SlateGrayMuted,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Results count badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiText(
                            "${formatLocalizedInt(subScreenDuas.size, uiLang)}টি সহীহ আমল প্রদর্শিত হচ্ছে",
                            "Showing ${subScreenDuas.size} authentic supplications"
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        )
                    )

                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = uiText("অনুসন্ধান মুছুন", "Clear Search"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.clickable { searchQuery = "" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Filtered Duas List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(subScreenDuas, key = { it.id }) { dua ->
                        val duaMediaId = "dua_${dua.id}"
                        val duaTitle = dua.localizedTitle(contentLang)
                        val ttsPrefix = "tts_${duaTitle.hashCode()}"
                        val isStreamingThis = playbackState.currentMediaId == duaMediaId ||
                            playbackState.currentMediaId.startsWith(ttsPrefix) ||
                            (playbackState.currentUrl == dua.audioUrl && dua.audioUrl.isNotEmpty())
                        val isSpeakingThis = speakingDuaId == dua.id && ttsState?.isSpeaking == true
                        val isCurrentlyPlaying = (isStreamingThis && playbackState.isPlaying) || isSpeakingThis
                        val isBuffering = isStreamingThis && playbackState.isBuffering

                        val currentTapCount = duaCounterMap[dua.id] ?: 0
                        val isFav = favoriteDuaIds.contains(dua.id)

                        DuaCard(
                            dua = dua,
                            isPlaying = isCurrentlyPlaying,
                            isBuffering = isBuffering,
                            tapCount = currentTapCount,
                            isFavorite = isFav,
                            onToggleFavorite = {
                                val isFavNow = favoriteDuaIds.contains(dua.id)
                                coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                    prefs.toggleFavoriteDua(dua.id)
                                    cloudSyncRepo.autoSyncFavoriteDua(dua.id, isAdded = !isFavNow)
                                }
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onIncrementCount = {
                                val newCount = if (currentTapCount >= dua.repetitions) 0 else currentTapCount + 1
                                duaCounterMap[dua.id] = newCount
                                coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                    worshipTracker.recordDuaEngagement(1)
                                }
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onPlayAudio = {
                                if (isCurrentlyPlaying || isBuffering) {
                                    if (isSpeakingThis) {
                                        ttsManager?.stop()
                                        speakingDuaId = null
                                    }
                                    if (isStreamingThis) {
                                        audioPlayer.stop()
                                    }
                                } else {
                                    ttsManager?.stop()
                                    speakingDuaId = null
                                    val reciterSubtitle = localizedText(
                                        uiLang,
                                        "শেখ মিশারী আল-আফাসী (বিশুদ্ধ তিলাওয়াত)",
                                        "Sheikh Mishary Al-Afasy (Recitation)"
                                    )
                                    val playlistItems = subScreenDuas.mapNotNull { item ->
                                        val title = item.localizedTitle(contentLang)
                                        val (primary, backups) = if (item.audioUrl.isNotBlank()) {
                                            item.audioUrl to emptyList()
                                        } else {
                                            audioPlayer.buildArabicTtsUrls(item.textArabic)
                                        }
                                        if (primary.isBlank()) return@mapNotNull null
                                        com.example.data.audio.AudioTrackItem(
                                            url = primary,
                                            title = title,
                                            subtitle = if (item.audioUrl.isNotBlank()) reciterSubtitle else item.localizedCategory(uiLang),
                                            mediaId = "dua_${item.id}",
                                            fallbackUrls = backups,
                                            fallbackArabicText = item.textArabic
                                        )
                                    }
                                    val targetMediaId = "dua_${dua.id}"
                                    val startIdx = playlistItems.indexOfFirst { it.mediaId == targetMediaId }
                                    if (playlistItems.isNotEmpty() && startIdx >= 0) {
                                        audioPlayer.playPlaylist(
                                            items = playlistItems,
                                            startIndex = startIdx,
                                            ttsManager = ttsManager
                                        )
                                    } else {
                                        audioPlayer.playArabicTextSpeech(
                                            text = dua.textArabic,
                                            title = duaTitle,
                                            subtitle = dua.localizedCategory(uiLang),
                                            ttsManager = ttsManager,
                                            preferOnline = true
                                        )
                                    }
                                    coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                        worshipTracker.recordDuaEngagement(1)
                                    }
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                        )
                    }
                }
            }
        } else {
            // ================= MAIN DUA HUB: CATEGORY GRID & MY COLLECTIONS =================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = uiText("দু'আ", "Dua"),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        )
                        Text(
                            text = uiText(
                                "হিসনুল মুসলিম ও দৈনন্দিন মাসনুন আমল",
                                "Hisnul Muslim & Daily Sunnah Supplications"
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 12.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = {
                            isSearchActive = true
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkEmeraldSurface)
                            .border(0.5.dp, HairlineBorder, CircleShape)
                            .testTag("open_dua_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = uiText("দু'আ খুঁজুন", "Search Dua"),
                            tint = ChampagneGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Current Waqt Smart Contextual Amal Banner
                FrostedGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val matchingCat = DuaCategoryDirectory.CATEGORIES.find { it.id == "pillar_azkar" }
                                ?: DuaCategoryDirectory.CATEGORIES[0]
                            selectedCategoryItem = matchingCat
                            searchQuery = ""
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                    borderColor = MintGlow.copy(alpha = 0.45f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkEmeraldElevated)
                                    .border(0.5.dp, MintGlow.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                ThreeDimensionalIcon(
                                    type = Icon3DType.DUA_HANDS,
                                    size = 24.dp,
                                    isActive = true,
                                    accentColor = MintGlow
                                )
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(MintGlow)
                                    )
                                    Text(
                                        text = uiText(
                                            "বর্তমান সময়োপযোগী আমল: ${currentWaqtCategory.localizedTitle(uiLang)}",
                                            "Recommended Now: ${currentWaqtCategory.localizedTitle(uiLang)}"
                                        ),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MintGlow,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Text(
                                    text = currentWaqtCategory.localizedSubtitle(uiLang),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateGrayMuted,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkEmeraldSurface)
                                .border(0.5.dp, MintGlow.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = uiText("আমল দেখুন", "View Duas"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MintGlow,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Category Grid Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiText("আমল বিভাগসমূহ", "Dua Categories"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = uiText("৬টি প্রধান স্তম্ভ", "6 Core Pillars"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2-Column Grid of 6 Circular Medallions
                val categoryChunks = remember { DuaCategoryDirectory.CATEGORIES.chunked(2) }
                categoryChunks.forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowCategories.forEach { cat ->
                            val count = remember(cat) { DuaRepository.DUAS.count(cat.filterPredicate) }
                            Box(modifier = Modifier.weight(1f)) {
                                DuaCategoryCircularCard(
                                    category = cat,
                                    duaCount = count,
                                    onClick = {
                                        selectedCategoryItem = cat
                                        isFavoritesSelected = false
                                        selectedCustomCollection = null
                                        isSearchActive = false
                                        searchQuery = ""
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                )
                            }
                        }
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // MY DUA COLLECTIONS Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = uiText("আমার দু'আ সংকলন", "My Dua Collections"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = uiText(
                                "কাস্টম সংরক্ষিত তালিকা ও ব্যক্তিগত পছন্দের আমল",
                                "Custom saved lists & personal favourites"
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            showCreateListDialog = true
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = DarkEmeraldSurface,
                            contentColor = ChampagneGold
                        ),
                        border = BorderStroke(0.5.dp, ChampagneGold.copy(alpha = 0.6f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("create_new_dua_list_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiText("+ নতুন তালিকা", "+ New list"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Favorite Folder Card
                MyDuaCollectionFavoriteCard(
                    favoriteCount = favoriteDuaIds.size,
                    onClick = {
                        isFavoritesSelected = true
                        selectedCategoryItem = null
                        selectedCustomCollection = null
                        isSearchActive = false
                        searchQuery = ""
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                )

                // Custom User Collection Folders
                customCollections.forEach { folderName ->
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomDuaCollectionFolderCard(
                        name = folderName,
                        duaCount = favoriteDuaIds.size,
                        onClick = {
                            selectedCustomCollection = folderName
                            selectedCategoryItem = null
                            isFavoritesSelected = false
                            isSearchActive = false
                            searchQuery = ""
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // Dialog for creating a new custom Dua list
        if (showCreateListDialog) {
            CreateDuaListDialog(
                onDismiss = { showCreateListDialog = false },
                onCreate = { name ->
                    coroutineScope.launch {
                        prefs.addCustomDuaCollection(name)
                    }
                    showCreateListDialog = false
                }
            )
        }

        // 5. Fallback indicator ONLY when offline Android TTS is active (Media3 playback uses GlobalMiniPlayerBar)
        val showFloatingBar = (ttsState?.isSpeaking == true && !playbackState.isActive)
        AnimatedVisibility(
            visible = showFloatingBar,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            val title = if (speakingDuaId != null) {
                DuaRepository.DUAS.find { it.id == speakingDuaId }?.localizedTitle(contentLang)
                    ?: uiText("আরবি উচ্চারণ শুনছেন", "Playing Arabic pronunciation")
            } else {
                playbackState.currentTitle
            }

            val subtitle = if (speakingDuaId != null) {
                uiText("বিশুদ্ধ পুরুষ কণ্ঠের তিলাওয়াত (১০০% সহীহ উচ্চারণ)", "Clear male Arabic recitation")
            } else {
                if (playbackState.isBuffering) uiText("বাফারিং হচ্ছে...", "Buffering...") else playbackState.currentSubtitle
            }

            FrostedGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                borderColor = MintGlow.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MintGlow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Headphones,
                                contentDescription = null,
                                tint = MintGlow,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MintGlow
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (ttsState?.isSpeaking == true) {
                                    ttsManager?.stop()
                                    speakingDuaId = null
                                } else {
                                    audioPlayer.togglePlayPause()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying || ttsState?.isSpeaking == true) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = ChampagneGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                ttsManager?.stop()
                                speakingDuaId = null
                                audioPlayer.stop()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop",
                                tint = SlateGrayMuted,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DuaCard(
    dua: Dua,
    isPlaying: Boolean,
    isBuffering: Boolean,
    tapCount: Int,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onIncrementCount: () -> Unit,
    onPlayAudio: () -> Unit
) {
    var expandedVirtues by remember { mutableStateOf(false) }
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current

    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dua_card_${dua.id}")
            .animateContentSize(),
        borderColor = if (isPlaying) MintGlow.copy(alpha = 0.7f) else HairlineBorder
    ) {
        // 1. Header with title and repetition / audio controls
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
                            .border(0.5.dp, HairlineBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = dua.localizedCategory(contentLang),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ChampagneGold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    if (dua.repetitions > 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkEmeraldElevated)
                                .border(0.5.dp, MintGlow.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = uiText("${formatLocalizedInt(dua.repetitions, uiLang)} বার পাঠ", "${dua.repetitions}x"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MintGlow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dua.localizedTitle(contentLang),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Audio & Play Button + Bookmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Bookmark Icon
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isFavorite) ChampagneGold else SlateGrayMuted.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Audio play icon
                IconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier.size(38.dp)
                ) {
                    if (isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ChampagneGold,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) MintGlow.copy(alpha = 0.2f) else DarkEmeraldSurface)
                                .border(0.5.dp, if (isPlaying) MintGlow else HairlineBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Play Audio",
                                tint = if (isPlaying) MintGlow else ChampagneGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Arabic Text with complete Harakat rendered in AmiriFontFamily
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkEmeraldElevated)
                .border(
                    0.5.dp,
                    if (isPlaying) MintGlow.copy(alpha = 0.3f) else HairlineBorder,
                    RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = dua.textArabic,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = AmiriFontFamily,
                        fontSize = 21.sp,
                        lineHeight = 38.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (isPlaying) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MintGlow)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiText("পুরুষ কণ্ঠে পূর্ণ উচ্চারণ পাঠ হচ্ছে...", "Playing Arabic recitation..."),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MintGlow,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Localized Transliteration / Pronunciation
        val localizedPronunciation = dua.localizedTransliteration(contentLang)
        if (localizedPronunciation.isNotBlank()) {
            Text(
                text = uiText("উচ্চারণ:", "Transliteration:"),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MintGlow,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = localizedPronunciation,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // 4. Localized Meaning
        Text(
            text = uiText("অর্থ:", "Meaning:"),
            style = MaterialTheme.typography.labelSmall.copy(
                color = ChampagneGold,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = dua.localizedMeaning(contentLang),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = TextPrimary.copy(alpha = 0.95f),
                lineHeight = 21.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Hadith Reference & Repetition Counter Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiText("সূত্র: ${dua.reference}", "Reference: ${dua.reference}"),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = SlateGrayMuted.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )

            // Interactive Repetition Counter pill
            if (dua.repetitions > 1) {
                val isCompleted = tapCount >= dua.repetitions
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCompleted) MintGlow.copy(alpha = 0.15f) else DarkEmeraldSurface)
                        .border(
                            0.5.dp,
                            if (isCompleted) MintGlow else ChampagneGold.copy(alpha = 0.4f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onIncrementCount() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.TouchApp,
                            contentDescription = null,
                            tint = if (isCompleted) MintGlow else ChampagneGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isCompleted) {
                                uiText("সম্পূর্ণ (${formatLocalizedInt(dua.repetitions, uiLang)})", "Done (${dua.repetitions})")
                            } else {
                                uiText(
                                    "${formatLocalizedInt(tapCount, uiLang)} / ${formatLocalizedInt(dua.repetitions, uiLang)} বার",
                                    "$tapCount / ${dua.repetitions}"
                                )
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isCompleted) MintGlow else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // 6. Virtues & Amal Notes
        if (dua.virtuesBengali.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedVirtues = !expandedVirtues }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (expandedVirtues) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = ChampagneGold.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (expandedVirtues) {
                        uiText("ফজিলত ও আমলের নিয়ম সংক্ষেপ করুন", "Hide virtues & guidelines")
                    } else {
                        uiText("ফজিলত ও আমলের নিয়ম দেখুন", "View virtues & guidelines")
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ChampagneGold.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            AnimatedVisibility(visible = expandedVirtues) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkEmeraldSurface)
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = dua.virtuesBengali,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp,
                            lineHeight = 17.sp
                        )
                    )
                }
            }
        }
    }
}
