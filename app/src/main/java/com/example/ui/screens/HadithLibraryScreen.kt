package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.audio.ArabicTtsManager
import com.example.data.cloud.CloudSyncRepository
import com.example.data.local.AppDatabase
import com.example.data.local.HadithBookmarkEntity
import com.example.data.model.Hadith
import com.example.data.model.HadithBook
import com.example.data.model.HadithChapter
import com.example.data.preference.AppPreferences
import com.example.data.repository.ExpandedHadithBookContent
import com.example.data.repository.HadithMasterRepository
import com.example.data.local.HadithDatabaseLoader
import com.example.data.local.HadithDatabaseLoader.toDomain
import com.example.data.local.HadithEntity
import com.example.data.repository.HadithRepository
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.Icon3DType
import com.example.ui.components.SectionHeader
import com.example.ui.components.ThreeDimensionalIcon
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class HadithReaderViewMode {
    BOOK_INDEX,        // Level 1: Primary Hadith Collections List
    CHAPTER_INDEX,     // Level 2: Book Chapters / Kitab Table of Contents
    FULL_BOOK_READER   // Level 3: Paginated / Continuous Reading Experience
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithLibraryScreen(
    onBack: () -> Unit,
    ttsManager: ArabicTtsManager? = null,
    audioPlayer: com.example.data.audio.AudioPlayerManager? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resolvedAudioPlayer = audioPlayer ?: remember { com.example.data.audio.AudioPlayerManager.getInstance(context) }
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current
    val coroutineScope = rememberCoroutineScope()
    val database = remember { AppDatabase.getInstance(context) }
    val hadithBookmarkDao = remember { database.hadithBookmarkDao() }
    val prefs = remember { AppPreferences(context) }
    val cloudSync = remember { CloudSyncRepository(context, prefs, database) }
    val worshipTracker = remember { com.example.data.repository.WorshipTrackerRepository(context, prefs, database, cloudSync) }

    val savedBookmarks by hadithBookmarkDao.getAllBookmarks().collectAsState(initial = emptyList())
    val bookmarkedIds = remember(savedBookmarks) { savedBookmarks.map { it.hadithId }.toSet() }

    // Last Read persistence
    val lastReadBookId by prefs.lastReadHadithBook.collectAsState(initial = null)
    val lastReadChapterId by prefs.lastReadHadithChapter.collectAsState(initial = null)
    val lastReadHadithNum by prefs.lastReadHadithNum.collectAsState(initial = null)
    val lastReadPage by prefs.lastReadHadithPage.collectAsState(initial = 1)
    val fontScale by prefs.hadithFontScale.collectAsState(initial = 1.0f)

    // Navigation and state within Hadith Book Reader
    var currentViewMode by remember { mutableStateOf(HadithReaderViewMode.BOOK_INDEX) }
    var selectedBook by remember { mutableStateOf<HadithBook?>(null) }
    var selectedChapter by remember { mutableStateOf<HadithChapter?>(null) }
    var showBookmarksOnly by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showJumpDialog by remember { mutableStateOf(false) }
    var showFontSizeSheet by remember { mutableStateOf(false) }

    // Physical Page-by-page or continuous reader mode
    var isContinuousScroll by remember { mutableStateOf(true) }
    var activePageIndex by remember { mutableIntStateOf(1) }

    // Seed offline database from assets in background on first access
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            HadithDatabaseLoader.populateDatabaseIfEmpty(context, database)
        }
    }

    // Track real Hadith reading duration (minutes) while reading full book/chapter
    LaunchedEffect(currentViewMode == HadithReaderViewMode.FULL_BOOK_READER) {
        if (currentViewMode == HadithReaderViewMode.FULL_BOOK_READER) {
            while (true) {
                kotlinx.coroutines.delay(60_000L)
                withContext(Dispatchers.IO) {
                    worshipTracker.recordHadithMinutes(1)
                }
            }
        }
    }

    // Hardware back button navigation handling
    BackHandler {
        when (currentViewMode) {
            HadithReaderViewMode.FULL_BOOK_READER -> {
                if (showBookmarksOnly) {
                    showBookmarksOnly = false
                }
                currentViewMode = HadithReaderViewMode.CHAPTER_INDEX
            }
            HadithReaderViewMode.CHAPTER_INDEX -> {
                currentViewMode = HadithReaderViewMode.BOOK_INDEX
                selectedChapter = null
            }
            HadithReaderViewMode.BOOK_INDEX -> {
                if (showBookmarksOnly) {
                    showBookmarksOnly = false
                } else {
                    onBack()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground),
        containerColor = ObsidianBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val titleText = when {
                            showBookmarksOnly -> uiText("সংরক্ষিত প্রিয় হাদিস", "Saved Favorite Hadiths")
                            currentViewMode == HadithReaderViewMode.FULL_BOOK_READER -> selectedBook?.localizedName(uiLang) ?: uiText("হাদিস রিডার", "Hadith Reader")
                            currentViewMode == HadithReaderViewMode.CHAPTER_INDEX -> selectedBook?.localizedName(uiLang) ?: uiText("অধ্যায় সূচিপত্র", "Chapters Index")
                            else -> uiText("হাদিস কিতাব ও রিডার", "Hadith Books & Reader")
                        }
                        val subtitleText = when {
                            showBookmarksOnly -> uiText("${savedBookmarks.size}টি হাদিস বুকমার্কে সংরক্ষিত", "${savedBookmarks.size} Hadiths bookmarked")
                            currentViewMode == HadithReaderViewMode.FULL_BOOK_READER -> selectedChapter?.localizedName(uiLang) ?: uiText("পূর্ণাঙ্গ কিতাব পঠন", "Full Book Reading")
                            currentViewMode == HadithReaderViewMode.CHAPTER_INDEX -> uiText(
                                "${selectedBook?.totalChaptersCount ?: 0}টি অধ্যায় • ${selectedBook?.localizedTotalHadithCount(uiLang) ?: ""}",
                                "${selectedBook?.totalChaptersCount ?: 0} Chapters • ${selectedBook?.localizedTotalHadithCount(uiLang) ?: ""}"
                            )
                            else -> uiText("সিহাহ সিত্তাহ ও প্রামাণিক হাদিস গ্রন্থাবলী", "Sihah Sittah & Authentic Hadith Collections")
                        }

                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subtitleText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            when (currentViewMode) {
                                HadithReaderViewMode.FULL_BOOK_READER -> {
                                    if (showBookmarksOnly) showBookmarksOnly = false
                                    currentViewMode = HadithReaderViewMode.CHAPTER_INDEX
                                }
                                HadithReaderViewMode.CHAPTER_INDEX -> {
                                    currentViewMode = HadithReaderViewMode.BOOK_INDEX
                                    selectedChapter = null
                                }
                                HadithReaderViewMode.BOOK_INDEX -> {
                                    if (showBookmarksOnly) showBookmarksOnly = false
                                    else onBack()
                                }
                            }
                        },
                        modifier = Modifier.testTag("hadith_library_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ChampagneGold
                        )
                    }
                },
                actions = {
                    if (currentViewMode == HadithReaderViewMode.FULL_BOOK_READER) {
                        // Quick Jump to Hadith / Page
                        IconButton(
                            onClick = { showJumpDialog = true },
                            modifier = Modifier.testTag("hadith_quick_jump_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PinDrop,
                                contentDescription = "Quick Jump",
                                tint = ChampagneGold
                            )
                        }

                        // Toggle Page-wise vs Continuous
                        IconButton(
                            onClick = {
                                isContinuousScroll = !isContinuousScroll
                                Toast.makeText(
                                    context,
                                    if (isContinuousScroll) localizedText(uiLang, "নিরবচ্ছিন্ন স্ক্রোল মোড", "Continuous scroll mode")
                                    else localizedText(uiLang, "পৃষ্ঠাভিত্তিক মোড", "Page-by-page mode"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.testTag("toggle_page_scroll_mode")
                        ) {
                            Icon(
                                imageVector = if (isContinuousScroll) Icons.Filled.ViewStream else Icons.Filled.AutoStories,
                                contentDescription = "Scroll Mode",
                                tint = MintGlow
                            )
                        }

                        // Text Size / Display Settings
                        IconButton(
                            onClick = { showFontSizeSheet = true },
                            modifier = Modifier.testTag("hadith_font_size_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FormatSize,
                                contentDescription = "Font Size",
                                tint = SlateGrayMuted
                            )
                        }
                    }

                    // Bookmarks filter toggle button
                    IconButton(
                        onClick = {
                            showBookmarksOnly = !showBookmarksOnly
                            if (showBookmarksOnly) {
                                currentViewMode = HadithReaderViewMode.BOOK_INDEX
                            }
                        },
                        modifier = Modifier.testTag("toggle_bookmarks_hadith_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (savedBookmarks.isNotEmpty()) {
                                    Badge(
                                        containerColor = ChampagneGold,
                                        contentColor = Color.Black
                                    ) {
                                        Text(savedBookmarks.size.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (showBookmarksOnly) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Bookmarks",
                                tint = if (showBookmarksOnly) ChampagneGold else SlateGrayMuted
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // ==========================================
            // SEARCH & QUICK LAST READ RESUME CARD
            // ==========================================
            if (currentViewMode != HadithReaderViewMode.FULL_BOOK_READER) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = com.example.data.security.InputSanitizer.sanitizeSearchQuery(it, 100) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hadith_search_input"),
                    placeholder = {
                        Text(
                            text = uiText(
                                "হাদিস, রাবী বা বিষয় দিয়ে খুঁজুন (যেমন: নিয়ত, বুখারী ১)...",
                                "Search by book, chapter, or keyword..."
                            ),
                            color = SlateGrayMuted,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = ChampagneGold
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear",
                                    tint = SlateGrayMuted
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedContainerColor = DarkEmeraldSurface,
                        unfocusedContainerColor = DarkEmeraldSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Last Read Resume Card (if present)
                if (!lastReadBookId.isNullOrEmpty() && !showBookmarksOnly && searchQuery.isEmpty()) {
                    val lastBook = HadithMasterRepository.HADITH_BOOKS.find { it.id == lastReadBookId }
                    if (lastBook != null) {
                        FrostedGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedBook = lastBook
                                    val ch = HadithMasterRepository.getChapter(lastBook.id, lastReadChapterId ?: "")
                                    selectedChapter = ch ?: HadithMasterRepository.getChaptersForBook(lastBook.id).firstOrNull()
                                    activePageIndex = lastReadPage
                                    currentViewMode = HadithReaderViewMode.FULL_BOOK_READER
                                }
                                .testTag("hadith_last_read_card"),
                            cornerRadius = 14.dp,
                            backgroundColor = DarkEmeraldSurface,
                            borderColor = ChampagneGold.copy(alpha = 0.45f)
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
                                            .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ThreeDimensionalIcon(
                                            type = Icon3DType.HOLY_QURAN,
                                            size = 22.dp,
                                            isActive = true,
                                            accentColor = ChampagneGold
                                        )
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = uiText("সর্বশেষ পঠিত পাতা", "Last Read Page"),
                                                color = ChampagneGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = uiText("পৃষ্ঠা $lastReadPage", "Page $lastReadPage"),
                                                color = MintGlow,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        val displayHadithNum = lastReadHadithNum ?: formatLocalizedInt(1, uiLang)
                                        Text(
                                            text = uiText(
                                                "${lastBook.nameBengali} • হাদিস নং $displayHadithNum",
                                                "${lastBook.localizedName(uiLang)} • Hadith #$displayHadithNum"
                                            ),
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        selectedBook = lastBook
                                        val ch = HadithMasterRepository.getChapter(lastBook.id, lastReadChapterId ?: "")
                                        selectedChapter = ch ?: HadithMasterRepository.getChaptersForBook(lastBook.id).firstOrNull()
                                        activePageIndex = lastReadPage
                                        currentViewMode = HadithReaderViewMode.FULL_BOOK_READER
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ChampagneGold,
                                        contentColor = ObsidianBackground
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = uiText("পড়া চালিয়ে যান", "Continue"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ObsidianBackground
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // ==========================================
            // MAIN VIEW CONTENT SWITCHER
            // ==========================================
            when {
                showBookmarksOnly -> {
                    // Bookmarked items list
                    val bookmarkedList = savedBookmarks.mapNotNull { bm ->
                        ExpandedHadithBookContent.getHadithById(bm.hadithId)
                            ?: Hadith(
                                id = bm.hadithId,
                                bookId = bm.bookId,
                                bookNameBengali = bm.bookName,
                                chapterId = "saved",
                                chapterNameBengali = bm.chapterName,
                                hadithNumber = bm.hadithNumber,
                                narrator = bm.narrator,
                                arabicText = bm.arabicText,
                                banglaText = bm.banglaText,
                                explanation = bm.explanation,
                                grade = bm.grade
                            )
                    }

                    if (bookmarkedList.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Filled.BookmarkBorder,
                            title = uiText("কোনো সংরক্ষিত হাদিস নেই", "No Saved Hadiths"),
                            subtitle = uiText(
                                "পছন্দের হাদিসের পাশে বুকমার্ক আইকনে চাপ দিলে এখানে প্রদর্শিত হবে।",
                                "Tap the bookmark icon next to any Hadith to save it here."
                            )
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            items(bookmarkedList, key = { it.id }) { hadith ->
                                HadithReaderCard(
                                    hadith = hadith,
                                    fontScale = fontScale,
                                    isBookmarked = true,
                                    onToggleBookmark = {
                                        coroutineScope.launch {
                                            hadithBookmarkDao.removeBookmark(hadith.id)
                                            cloudSync.removeHadithBookmarkFromCloud("", hadith.id)
                                            Toast.makeText(
                                                context,
                                                localizedText(uiLang, "বুকমার্ক থেকে অপসারিত", "Removed from bookmarks"),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onCopyShare = { copyHadithToClipboard(context, hadith, uiLang, contentLang) },
                                    onPlayTts = {
                                        val playlistItems = bookmarkedList.mapNotNull { item ->
                                            val (primary, backups) = resolvedAudioPlayer.buildArabicTtsUrls(item.arabicText)
                                            if (primary.isBlank()) return@mapNotNull null
                                            com.example.data.audio.AudioTrackItem(
                                                url = primary,
                                                title = item.localizedBookName(uiLang),
                                                subtitle = item.localizedChapterName(uiLang),
                                                mediaId = "hadith_${item.id}",
                                                fallbackUrls = backups,
                                                fallbackArabicText = item.arabicText
                                            )
                                        }
                                        val idx = playlistItems.indexOfFirst { it.mediaId == "hadith_${hadith.id}" }
                                        if (playlistItems.isNotEmpty() && idx >= 0) {
                                            resolvedAudioPlayer.playPlaylist(
                                                items = playlistItems,
                                                startIndex = idx,
                                                ttsManager = ttsManager
                                            )
                                        } else {
                                            resolvedAudioPlayer.playArabicTextSpeech(
                                                text = hadith.arabicText,
                                                title = hadith.localizedBookName(uiLang),
                                                subtitle = hadith.localizedChapterName(uiLang),
                                                speed = 0.85f,
                                                ttsManager = ttsManager,
                                                preferOnline = true
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                currentViewMode == HadithReaderViewMode.BOOK_INDEX -> {
                    // Level 1: Primary Hadith Book Collections
                    val books = remember(searchQuery) {
                        if (searchQuery.isBlank()) HadithMasterRepository.HADITH_BOOKS
                        else HadithMasterRepository.HADITH_BOOKS.filter {
                            it.nameBengali.contains(searchQuery, ignoreCase = true) ||
                            it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                            it.compiler.contains(searchQuery, ignoreCase = true) ||
                            it.compilerEnglish.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 40.dp)
                    ) {
                        item {
                            SectionHeader(
                                title = uiText("প্রামাণিক হাদিস গ্রন্থমালা", "Authentic Hadith Collections"),
                                subtitle = uiText(
                                    "কিতাব নির্বাচন করে অধ্যায়ভিত্তিক সম্পূর্ণ গ্রন্থ পাঠ করুন",
                                    "Select a collection to read chapter by chapter"
                                )
                            )
                        }

                        items(books, key = { it.id }) { book ->
                            HadithBookCard(
                                book = book,
                                onClick = {
                                    selectedBook = book
                                    currentViewMode = HadithReaderViewMode.CHAPTER_INDEX
                                }
                            )
                        }
                    }
                }

                currentViewMode == HadithReaderViewMode.CHAPTER_INDEX -> {
                    // Level 2: Book Chapters / Kitab Table of Contents
                    val book = selectedBook ?: HadithMasterRepository.HADITH_BOOKS.first()
                    var loadedChapters by remember(book.id) {
                        mutableStateOf(HadithDatabaseLoader.loadBookChaptersFromAsset(context, book.id))
                    }

                    LaunchedEffect(book.id) {
                        val assetChs = withContext(Dispatchers.IO) {
                            HadithDatabaseLoader.loadBookChaptersFromAsset(context, book.id)
                        }
                        if (assetChs.isNotEmpty()) {
                            loadedChapters = assetChs
                        }
                    }

                    val chapters = remember(loadedChapters, searchQuery) {
                        if (searchQuery.isBlank()) loadedChapters
                        else loadedChapters.filter {
                            it.nameBengali.contains(searchQuery, ignoreCase = true) ||
                            it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                            it.nameArabic.contains(searchQuery) ||
                            it.chapterNumber.toString().contains(searchQuery)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 40.dp)
                    ) {
                        item {
                            // Book Header Ribbon
                            FrostedGlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                cornerRadius = 14.dp,
                                backgroundColor = DarkEmeraldSurface,
                                borderColor = ChampagneGold.copy(alpha = 0.35f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = book.localizedName(uiLang),
                                            color = ChampagneGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        )
                                        Text(
                                            text = book.localizedCompiler(uiLang),
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = book.localizedDescription(contentLang),
                                            color = SlateGrayMuted,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Full Book Read Button
                                    Button(
                                        onClick = {
                                            selectedChapter = chapters.firstOrNull()
                                            activePageIndex = 1
                                            currentViewMode = HadithReaderViewMode.FULL_BOOK_READER
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MintGlow,
                                            contentColor = ObsidianBackground
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(
                                            text = uiText("প্রথম পাতা থেকে", "From Page 1"),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ObsidianBackground
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            SectionHeader(
                                title = uiText("কিতাবের অধ্যায়সমূহ (${chapters.size})", "Chapters (${chapters.size})"),
                                subtitle = uiText(
                                    "যে কোনো অধ্যায়ে ট্যাপ করে হাদিস পড়া শুরু করুন",
                                    "Tap any chapter to start reading Hadiths"
                                )
                            )
                        }

                        itemsIndexed(chapters, key = { _, ch -> ch.id }) { index, chapter ->
                            HadithChapterCard(
                                chapter = chapter,
                                index = index + 1,
                                onClick = {
                                    selectedChapter = chapter
                                    activePageIndex = chapter.startPage
                                    currentViewMode = HadithReaderViewMode.FULL_BOOK_READER
                                }
                            )
                        }
                    }
                }

                currentViewMode == HadithReaderViewMode.FULL_BOOK_READER -> {
                    // Level 3: Full Book / Chapter Paginated & Continuous Reader
                    val book = selectedBook ?: HadithMasterRepository.HADITH_BOOKS.first()
                    val chapter = selectedChapter ?: HadithMasterRepository.getChaptersForBook(book.id).firstOrNull()

                    var dbHadiths by remember(book.id, chapter?.chapterNumber) {
                        val staticList = if (chapter != null) {
                            val inChapter = ExpandedHadithBookContent.getHadithsForChapter(book.id, chapter.id)
                            if (inChapter.isNotEmpty()) inChapter
                            else ExpandedHadithBookContent.getHadithsForBook(book.id)
                        } else {
                            ExpandedHadithBookContent.getHadithsForBook(book.id)
                        }
                        mutableStateOf(staticList)
                    }

                    // Asynchronously fetch from Room database
                    LaunchedEffect(book.id, chapter?.chapterNumber) {
                        val hadithDao = database.hadithDao()
                        val roomList = withContext(Dispatchers.IO) {
                            if (chapter != null) {
                                hadithDao.getHadithsByChapterPaged(book.id, chapter.chapterNumber, limit = 100, offset = 0)
                            } else {
                                hadithDao.getHadithsPaged(book.id, limit = 100, offset = 0)
                            }
                        }
                        if (roomList.isNotEmpty()) {
                            dbHadiths = roomList.map { it.toDomain() }
                        }
                    }

                    val bookHadiths = dbHadiths

                    // Save Last Read position automatically and record chapter engagement
                    LaunchedEffect(book.id, chapter?.id, bookHadiths.firstOrNull()?.id) {
                        bookHadiths.firstOrNull()?.let { firstH ->
                            prefs.saveLastReadHadith(
                                bookId = book.id,
                                chapterId = chapter?.id ?: "",
                                hadithId = firstH.id,
                                hadithNumber = firstH.hadithNumber,
                                pageNumber = firstH.pageNumber
                            )
                            withContext(Dispatchers.IO) {
                                worshipTracker.recordHadithRead(1)
                            }
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        // Reader Header Pill (Showing Chapter Title & Page Indicator)
                        FrostedGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            cornerRadius = 12.dp,
                            backgroundColor = DarkEmeraldSurface,
                            borderColor = HairlineBorder
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(ChampagneGold.copy(alpha = 0.15f))
                                            .border(0.5.dp, ChampagneGold, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${chapter?.chapterNumber ?: 1}",
                                            color = ChampagneGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = chapter?.localizedName(uiLang) ?: book.localizedName(uiLang),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = uiText(
                                                "হাদিস সংখ্যা: ${bookHadiths.size}টি • পৃষ্ঠা: $activePageIndex",
                                                "Hadiths: ${bookHadiths.size} • Page: $activePageIndex"
                                            ),
                                            color = MintGlow,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Text(
                                    text = if (isContinuousScroll) uiText("স্ক্রোল পঠন", "Scroll Mode") else uiText("পৃষ্ঠা $activePageIndex", "Page $activePageIndex"),
                                    color = SlateGrayMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Hadiths Display Area
                        val listState = rememberLazyListState()

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(bookHadiths, key = { it.id }) { hadith ->
                                val isBookmarked = bookmarkedIds.contains(hadith.id)

                                HadithReaderCard(
                                    hadith = hadith,
                                    fontScale = fontScale,
                                    isBookmarked = isBookmarked,
                                    onToggleBookmark = {
                                        val entity = HadithBookmarkEntity(
                                            hadithId = hadith.id,
                                            bookId = hadith.bookId,
                                            bookName = hadith.bookNameBengali,
                                            chapterName = hadith.chapterNameBengali,
                                            hadithNumber = hadith.hadithNumber,
                                            narrator = hadith.narrator,
                                            arabicText = hadith.arabicText,
                                            banglaText = hadith.banglaText,
                                            explanation = hadith.explanation,
                                            grade = hadith.grade
                                        )
                                        coroutineScope.launch {
                                            if (isBookmarked) {
                                                hadithBookmarkDao.removeBookmark(hadith.id)
                                                cloudSync.autoSyncHadithBookmark(entity, isAdded = false)
                                                Toast.makeText(
                                                    context,
                                                    localizedText(uiLang, "বুকমার্ক থেকে অপসারিত", "Removed from bookmarks"),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                hadithBookmarkDao.addBookmark(entity)
                                                cloudSync.autoSyncHadithBookmark(entity, isAdded = true)
                                                Toast.makeText(
                                                    context,
                                                    localizedText(uiLang, "হাদিস সংরক্ষিত ও ক্লাউডে সিঙ্ক হয়েছে", "Hadith saved & synced to cloud"),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    onCopyShare = { copyHadithToClipboard(context, hadith, uiLang, contentLang) },
                                    onPlayTts = {
                                        val playlistItems = bookHadiths.mapNotNull { item ->
                                            val (primary, backups) = resolvedAudioPlayer.buildArabicTtsUrls(item.arabicText)
                                            if (primary.isBlank()) return@mapNotNull null
                                            com.example.data.audio.AudioTrackItem(
                                                url = primary,
                                                title = item.localizedBookName(uiLang),
                                                subtitle = item.localizedChapterName(uiLang),
                                                mediaId = "hadith_${item.id}",
                                                fallbackUrls = backups,
                                                fallbackArabicText = item.arabicText
                                            )
                                        }
                                        val idx = playlistItems.indexOfFirst { it.mediaId == "hadith_${hadith.id}" }
                                        if (playlistItems.isNotEmpty() && idx >= 0) {
                                            resolvedAudioPlayer.playPlaylist(
                                                items = playlistItems,
                                                startIndex = idx,
                                                ttsManager = ttsManager
                                            )
                                        } else {
                                            resolvedAudioPlayer.playArabicTextSpeech(
                                                text = hadith.arabicText,
                                                title = hadith.localizedBookName(uiLang),
                                                subtitle = hadith.localizedChapterName(uiLang),
                                                speed = 0.85f,
                                                ttsManager = ttsManager,
                                                preferOnline = true
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        // Bottom Navigation Footer (Next / Previous Chapter navigation)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val allChs = HadithMasterRepository.getChaptersForBook(book.id)
                            val currentIndex = allChs.indexOfFirst { it.id == chapter?.id }

                            OutlinedButton(
                                onClick = {
                                    if (currentIndex > 0) {
                                        selectedChapter = allChs[currentIndex - 1]
                                        activePageIndex = allChs[currentIndex - 1].startPage
                                    }
                                },
                                enabled = currentIndex > 0,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ChampagneGold),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(HairlineBorder))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Chapter",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(uiText("পূর্ববর্তী অধ্যায়", "Prev Chapter"), fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    if (currentIndex < allChs.size - 1) {
                                        selectedChapter = allChs[currentIndex + 1]
                                        activePageIndex = allChs[currentIndex + 1].startPage
                                    }
                                },
                                enabled = currentIndex < allChs.size - 1,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ChampagneGold),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(HairlineBorder))
                            ) {
                                Text(uiText("পরবর্তী অধ্যায়", "Next Chapter"), fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Chapter",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // DIALOGS & BOTTOM SHEETS
        // ==========================================

        // 1. Quick Jump to Hadith Number / Page Dialog
        if (showJumpDialog) {
            var inputNumber by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showJumpDialog = false },
                title = {
                    Text(
                        text = uiText("হাদিস বা পৃষ্ঠা নম্বরে লাফ দিন", "Jump to Hadith Number"),
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = uiText(
                                "পছন্দের হাদিস নম্বর লিখুন (যেমন: ১, ৯, ১৩, ২২৪):",
                                "Enter Hadith number (e.g., 1, 9, 13, 224):"
                            ),
                            color = SlateGrayMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inputNumber,
                            onValueChange = { inputNumber = it },
                            placeholder = { Text(uiText("হাদিস নম্বর...", "Hadith number..."), color = SlateGrayMuted, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ChampagneGold,
                                unfocusedBorderColor = HairlineBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showJumpDialog = false
                            if (inputNumber.isNotBlank()) {
                                val trimmed = inputNumber.trim()
                                val match = ExpandedHadithBookContent.ENCYCLOPEDIC_HADITHS.find {
                                    it.hadithNumber == trimmed || it.localizedHadithNumber("en") == trimmed
                                }
                                if (match != null) {
                                    val b = HadithMasterRepository.HADITH_BOOKS.find { it.id == match.bookId }
                                    selectedBook = b
                                    selectedChapter = HadithMasterRepository.getChapter(match.bookId, match.chapterId)
                                    activePageIndex = match.pageNumber
                                    currentViewMode = HadithReaderViewMode.FULL_BOOK_READER
                                    Toast.makeText(
                                        context,
                                        localizedText(
                                            uiLang,
                                            "হাদিস নং ${match.hadithNumber}-এ নিয়ে যাওয়া হয়েছে",
                                            "Jumped to Hadith #${match.localizedHadithNumber("en")}"
                                        ),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        localizedText(uiLang, "হাদিস নম্বর পাওয়া যায়নি", "Hadith number not found"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold)
                    ) {
                        Text(uiText("যাও", "Go"), color = ObsidianBackground, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showJumpDialog = false }) {
                        Text(uiText("বাতিল", "Cancel"), color = SlateGrayMuted)
                    }
                },
                containerColor = DarkEmeraldSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // 2. Font Size Scaling Sheet
        if (showFontSizeSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFontSizeSheet = false },
                containerColor = DarkEmeraldSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = uiText("হাদিস লেখার আকার (ফন্ট স্কেল)", "Hadith Text Size (Font Scale)"),
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(uiText("ছোট (A-)", "Small (A-)"), color = SlateGrayMuted, fontSize = 12.sp)
                        Slider(
                            value = fontScale,
                            onValueChange = { coroutineScope.launch { prefs.setHadithFontScale(it) } },
                            valueRange = 0.85f..1.4f,
                            steps = 5,
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = ChampagneGold,
                                activeTrackColor = MintGlow
                            )
                        )
                        Text(uiText("বড় (A+)", "Large (A+)"), color = SlateGrayMuted, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showFontSizeSheet = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MintGlow)
                    ) {
                        Text(uiText("সম্পন্ন", "Done"), color = ObsidianBackground, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ==========================================
// COMPONENT UI CARDS & SUB-VIEWS
// ==========================================

@Composable
fun HadithBookCard(
    book: HadithBook,
    onClick: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("hadith_book_card_${book.id}"),
        cornerRadius = 14.dp,
        backgroundColor = DarkEmeraldSurface,
        borderColor = HairlineBorder
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
                // Book Icon / Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldElevated)
                        .border(1.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    ThreeDimensionalIcon(
                        type = Icon3DType.HOLY_QURAN,
                        size = 26.dp,
                        isActive = true,
                        accentColor = ChampagneGold
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = book.localizedName(uiLang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (book.verifiedSahih) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MintGlow.copy(alpha = 0.15f))
                                    .border(0.5.dp, MintGlow.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = uiText("সহীহ সনদ", "Authentic"),
                                    color = MintGlow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = book.localizedCompiler(uiLang),
                        color = SlateGrayMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = book.localizedTotalHadithCount(uiLang),
                            color = ChampagneGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = uiText("• ${book.totalChaptersCount}টি কিতাব/অধ্যায়", "• ${book.totalChaptersCount} Chapters"),
                            color = SlateGrayMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open Book",
                tint = ChampagneGold,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun HadithChapterCard(
    chapter: HadithChapter,
    index: Int,
    onClick: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("hadith_chapter_card_${chapter.id}"),
        cornerRadius = 12.dp,
        backgroundColor = DarkEmeraldSurface,
        borderColor = HairlineBorder
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
                // Chapter Number Box
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkEmeraldElevated)
                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Column {
                    Text(
                        text = chapter.localizedName(uiLang),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = chapter.nameArabic,
                        color = ChampagneGold.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    Text(
                        text = uiText(
                            "হাদিস নং: ${chapter.hadithRange} • পৃষ্ঠা: ${chapter.startPage}-${chapter.endPage}",
                            "Hadith #: ${chapter.localizedHadithRange(uiLang)} • Page: ${chapter.startPage}-${chapter.endPage}"
                        ),
                        color = SlateGrayMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Read Chapter",
                tint = MintGlow,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun HadithReaderCard(
    hadith: Hadith,
    fontScale: Float = 1.0f,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onCopyShare: () -> Unit,
    onPlayTts: () -> Unit
) {
    val uiLang = LocalAppLanguage.current
    val contentLang = LocalContentLanguage.current
    var expandedExplanation by remember { mutableStateOf(false) }

    FrostedGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hadith_card_${hadith.id}"),
        cornerRadius = 16.dp,
        backgroundColor = LocalAppColors.current.card,
        borderColor = LocalAppColors.current.border
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Top Ribbon: Book Name, Hadith Number, Grade & Page Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(LocalAppColors.current.elevated)
                            .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = hadith.localizedBookName(uiLang),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    Text(
                        text = uiText(
                            "হাদিস নং ${hadith.hadithNumber}",
                            "Hadith #${hadith.localizedHadithNumber(uiLang)}"
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Page indicator
                    Text(
                        text = uiText("পৃষ্ঠা ${hadith.pageNumber}", "Page ${hadith.pageNumber}"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )

                    // Authenticity Grade
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MintGlow.copy(alpha = 0.12f))
                            .border(0.5.dp, MintGlow.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = hadith.localizedGrade(uiLang),
                            color = MintGlow,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Chapter Title & Narrator info
            Text(
                text = uiText(
                    "অধ্যায়: ${hadith.chapterNameBengali}",
                    "Chapter: ${hadith.localizedChapterName(uiLang)}"
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                fontSize = (11.5f * fontScale).sp
            )
            Text(
                text = uiText(
                    "রাবী: ${hadith.narrator}",
                    "Narrator: ${hadith.localizedNarrator(contentLang)}"
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = (11f * fontScale).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic Sanad & Matn Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(LocalAppColors.current.elevated)
                    .border(0.5.dp, LocalAppColors.current.border, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = hadith.arabicText,
                    color = LocalAppColors.current.arabicTextColor,
                    fontSize = (18f * fontScale).sp,
                    lineHeight = (28f * fontScale).sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val hadithTranslit = remember(hadith.id, contentLang) {
                hadith.localizedTransliteration(contentLang)
            }
            if (hadithTranslit.isNotBlank()) {
                Text(
                    text = "${uiText("উচ্চারণ:", "Transliteration:")} $hadithTranslit",
                    color = MintGlow.copy(alpha = 0.92f),
                    fontSize = (12f * fontScale).sp,
                    lineHeight = (18f * fontScale).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Translation (Localized per contentLang)
            Text(
                text = hadith.localizedTranslation(contentLang),
                color = LocalAppColors.current.translationTextColor,
                fontSize = (13f * fontScale).sp,
                lineHeight = (20f * fontScale).sp
            )

            // Explanatory Note / Tadabbur
            val explanationText = hadith.localizedExplanation(contentLang)
            if (explanationText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedVisibility(visible = expandedExplanation) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MintGlow.copy(alpha = 0.08f))
                            .border(0.5.dp, MintGlow.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiText("হাদিসের শিক্ষা ও তাদাব্বুর:", "Lessons & Reflection:"),
                                    color = ChampagneGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = explanationText,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = (11f * fontScale).sp,
                                lineHeight = (16f * fontScale).sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .clickable { expandedExplanation = !expandedExplanation }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expandedExplanation) uiText("শিক্ষা সংক্ষেপ করুন", "Hide Lessons") else uiText("শিক্ষা ও তাদাব্বুর দেখুন", "View Lessons & Reflection"),
                        color = ChampagneGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = if (expandedExplanation) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Toolbar (TTS, Share/Copy, Bookmark)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play Arabic Audio / TTS
                IconButton(
                    onClick = onPlayTts,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Pronounce Arabic",
                        tint = MintGlow,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Copy & Share
                IconButton(
                    onClick = onCopyShare,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = SlateGrayMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Bookmark toggle
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) ChampagneGold else SlateGrayMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SlateGrayMuted,
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = SlateGrayMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun copyHadithToClipboard(
    context: Context,
    hadith: Hadith,
    uiLang: String = "bn",
    contentLang: String = "bn"
) {
    val explanationText = hadith.localizedExplanation(contentLang)
    val isBnUi = com.example.data.util.SupportedLanguage.normalizeCode(uiLang) == "bn"
    val shareText = buildString {
        if (!isBnUi) {
            appendLine("📖 ${hadith.localizedBookName(uiLang)} - Hadith #${hadith.localizedHadithNumber(uiLang)} (Page ${hadith.pageNumber})")
            appendLine("Chapter: ${hadith.localizedChapterName(uiLang)}")
            appendLine("Narrator: ${hadith.localizedNarrator(contentLang)}")
            appendLine("Grade: ${hadith.localizedGrade(uiLang)}")
        } else {
            appendLine("📖 ${hadith.bookNameBengali} - হাদিস নং ${hadith.hadithNumber} (পৃষ্ঠা ${hadith.pageNumber})")
            appendLine("অধ্যায়: ${hadith.chapterNameBengali}")
            appendLine("রাবী: ${hadith.narrator}")
            appendLine("মান: ${hadith.grade}")
        }
        appendLine()
        appendLine("Arabic:")
        appendLine(hadith.arabicText)
        appendLine()
        appendLine(localizedText(contentLang, "বাংলা অনুবাদ:", "Translation:"))
        appendLine(hadith.localizedTranslation(contentLang))
        if (explanationText.isNotEmpty()) {
            appendLine()
            appendLine(localizedText(contentLang, "শিক্ষা ও তাৎপর্য:", "Lessons & Reflection:"))
            appendLine(explanationText)
        }
        appendLine()
        appendLine("— ${localizedText(uiLang, "নূর-এ-হেদায়েত", "Noor-e-Hidayah")}")
    }
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Hadith", shareText)
    clipboard.setPrimaryClip(clip)

    // Also trigger system share sheet
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, localizedText(uiLang, "হাদিস শেয়ার করুন", "Share Hadith"))
    context.startActivity(shareIntent)

    Toast.makeText(
        context,
        localizedText(uiLang, "হাদিস ক্লিপবোর্ডে কপি করা হয়েছে", "Hadith copied to clipboard"),
        Toast.LENGTH_SHORT
    ).show()
}
