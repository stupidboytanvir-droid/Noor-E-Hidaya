package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.components.FloatingBottomDock
import com.example.ui.screens.*
import com.example.ui.theme.NoorEHidayahTheme
import com.example.ui.theme.ObsidianBackground
import com.example.ui.viewmodel.*

import android.content.Context
import com.example.data.security.configureAppCheck
import com.example.data.util.LocaleHelper
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.LocalContentLanguage

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val quranViewModel: QuranViewModel by viewModels()
    private val tasbeehViewModel: TasbeehViewModel by viewModels()
    private val toolsViewModel: ToolsViewModel by viewModels()
    private val moreViewModel: MoreViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        // Create high-importance notification channel for prayer alerts
        com.example.data.notification.PrayerNotificationWorker.createNotificationChannel(this)

        // Runtime App Integrity & Anti-Tampering verification
        val securityReport = com.example.data.security.SecurityGuard.getSecurityReport(this)
        android.util.Log.i("MainActivity", "App Integrity: ${securityReport.environmentSummary}")

        // Check for Google Play In-App Updates in background
        try {
            val updateManager = com.example.data.update.InAppUpdateManager(this)
            updateManager.checkForUpdates { isAvailable ->
                if (isAvailable) {
                    updateManager.startUpdate(this, immediate = false)
                }
            }
        } catch (_: Exception) {}

        setContent {
            val themeStyleId by mainViewModel.themeStyle.collectAsState()
            val themeModeId by mainViewModel.themeMode.collectAsState()
            val customAccentHex by mainViewModel.customAccentHex.collectAsState()
            val customArabicColor by mainViewModel.customArabicColor.collectAsState()
            val customTranslationColor by mainViewModel.customTranslationColor.collectAsState()
            val customTransliterationColor by mainViewModel.customTransliterationColor.collectAsState()
            val canvasThemeId by mainViewModel.canvasTheme.collectAsState()
            val accentThemeId by mainViewModel.accentTheme.collectAsState()
            val uiFontId by mainViewModel.uiFont.collectAsState()
            val arabicFontId by mainViewModel.arabicFont.collectAsState()
            val textScale by mainViewModel.textScale.collectAsState()
            val onboardingCompleted by mainViewModel.onboardingCompleted.collectAsState()
            val initialLanguageSelected by mainViewModel.initialLanguageSelected.collectAsState()
            val requestedSurah by mainViewModel.requestedSurahNumber.collectAsState()
            val uiLanguage by mainViewModel.uiLanguage.collectAsState()
            val contentLanguage by mainViewModel.contentLanguage.collectAsState()

            var showAuthScreen by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

            androidx.compose.runtime.LaunchedEffect(requestedSurah) {
                requestedSurah?.let { surahNum ->
                    quranViewModel.openSurahByNumber(surahNum)
                    mainViewModel.clearRequestedSurah()
                }
            }

            val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner, onboardingCompleted) {
                val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                    if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME && onboardingCompleted) {
                        mainViewModel.autoDetectLocationIfPermitted(force = false)
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            LaunchedEffect(onboardingCompleted) {
                if (onboardingCompleted) {
                    mainViewModel.autoDetectLocationIfPermitted(force = false)
                }
            }

            CompositionLocalProvider(
                LocalAppLanguage provides uiLanguage,
                LocalContentLanguage provides contentLanguage
            ) {
                NoorEHidayahTheme(
                    themeMode = com.example.ui.theme.ThemeMode.fromId(themeModeId),
                    customAccentColorHex = customAccentHex,
                    customArabicColorHex = customArabicColor,
                    customTranslationColorHex = customTranslationColor,
                    customTransliterationColorHex = customTransliterationColor,
                    canvasTheme = com.example.ui.theme.AppCanvasTheme.fromId(canvasThemeId),
                    accentTheme = com.example.ui.theme.AppAccentTheme.fromId(accentThemeId),
                    uiFont = com.example.ui.theme.AppUiFont.fromId(uiFontId),
                    arabicFont = com.example.ui.theme.AppArabicFont.fromId(arabicFontId),
                    textScale = textScale,
                    style = com.example.ui.theme.ThemeStyle.fromId(themeStyleId)
                ) {
                    if (!onboardingCompleted) {
                        OnboardingScreen(
                            onComplete = { mainViewModel.completeOnboarding() },
                            onLocationGranted = { mainViewModel.autoDetectLocationIfPermitted(force = true) },
                            initialLanguageSelected = initialLanguageSelected,
                            onSelectInitialLanguage = { ui, content ->
                                mainViewModel.selectInitialLanguage(ui, content)
                            }
                        )
                    } else if (showAuthScreen) {
                        androidx.activity.compose.BackHandler {
                            showAuthScreen = false
                        }
                        AuthScreen(
                            authViewModel = authViewModel,
                            onAuthSuccess = { showAuthScreen = false }
                        )
                    } else {
                        MainAppScaffold(
                            mainViewModel = mainViewModel,
                            quranViewModel = quranViewModel,
                            tasbeehViewModel = tasbeehViewModel,
                            toolsViewModel = toolsViewModel,
                            moreViewModel = moreViewModel,
                            authViewModel = authViewModel,
                            onOpenAuth = { showAuthScreen = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppScaffold(
    mainViewModel: MainViewModel,
    quranViewModel: QuranViewModel,
    tasbeehViewModel: TasbeehViewModel,
    toolsViewModel: ToolsViewModel,
    moreViewModel: MoreViewModel,
    authViewModel: AuthViewModel,
    onOpenAuth: () -> Unit
) {
    val currentTab by mainViewModel.currentTab.collectAsState()
    val quranUiState by quranViewModel.uiState.collectAsState()
    val toolsActiveTool by toolsViewModel.activeTool.collectAsState()
    val playbackState by quranViewModel.playbackState.collectAsState()

    val showGlobalMiniPlayer = playbackState.isActive &&
        !(currentTab == AppTab.QURAN && quranUiState.selectedSurah != null) &&
        !(currentTab == AppTab.TOOLS && toolsActiveTool == ActiveToolTab.RUQYAH_SHARIAH)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                com.example.ui.components.GlobalMiniPlayerBar(
                    playbackState = playbackState,
                    visible = showGlobalMiniPlayer,
                    onTogglePlayPause = { quranViewModel.audioPlayer.togglePlayPause() },
                    onSkipPrevious = { quranViewModel.audioPlayer.skipPrevious() },
                    onSkipNext = { quranViewModel.audioPlayer.skipNext() },
                    onStop = { quranViewModel.audioPlayer.stop() }
                )
                FloatingBottomDock(
                    currentTab = currentTab,
                    onTabSelected = { mainViewModel.selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "main_screen_crossfade"
            ) { tab ->
                when (tab) {
                    AppTab.HOME -> {
                        HomeScreen(mainViewModel = mainViewModel)
                    }
                    AppTab.QURAN -> {
                        QuranScreen(quranViewModel = quranViewModel)
                    }
                    AppTab.DUA -> {
                        DuaScreen(
                            audioPlayer = quranViewModel.audioPlayer,
                            ttsManager = toolsViewModel.ttsManager,
                            mainViewModel = mainViewModel
                        )
                    }
                    AppTab.TOOLS -> {
                        ToolsScreen(
                            toolsViewModel = toolsViewModel,
                            tasbeehViewModel = tasbeehViewModel,
                            moreViewModel = moreViewModel,
                            onNavigateToTab = { mainViewModel.selectTab(it) }
                        )
                    }
                    AppTab.MORE -> {
                        MoreScreen(
                            moreViewModel = moreViewModel,
                            authViewModel = authViewModel,
                            onNavigateToAuth = onOpenAuth
                        )
                    }
                }
            }
        }
    }
}
