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
import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.data.security.configureAppCheck
import com.example.data.update.AppUpdateCheckWorker
import com.example.data.update.InAppUpdateManager
import com.example.data.util.LocaleHelper
import com.example.ui.theme.LocalAppLanguage
import com.example.ui.theme.LocalContentLanguage
import com.example.ui.theme.localizedText
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val quranViewModel: QuranViewModel by viewModels()
    private val tasbeehViewModel: TasbeehViewModel by viewModels()
    private val toolsViewModel: ToolsViewModel by viewModels()
    private val moreViewModel: MoreViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val appUpdateManager: InAppUpdateManager by lazy {
        InAppUpdateManager(this)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        // Create high-importance notification channels for prayer & app update alerts
        com.example.data.notification.PrayerNotificationWorker.createNotificationChannel(this)
        InAppUpdateManager.createUpdateNotificationChannel(this)

        // Runtime App Integrity & Anti-Tampering verification
        val securityReport = com.example.data.security.SecurityGuard.getSecurityReport(this)
        android.util.Log.i("MainActivity", "App Integrity: ${securityReport.environmentSummary}")

        // If user launched app by tapping an update notification, start APK download immediately
        handleUpdateNotificationIntent(intent)

        // Schedule 6-hour background GitHub update checks and run immediate startup check
        AppUpdateCheckWorker.schedulePeriodicCheck(this)
        lifecycleScope.launch {
            val githubUpdate = appUpdateManager.checkGitHubUpdates(notifyUser = true)
            if (githubUpdate == null) {
                try {
                    appUpdateManager.checkForUpdates { isAvailable ->
                        if (isAvailable) {
                            appUpdateManager.startUpdate(this@MainActivity, immediate = false)
                        }
                    }
                } catch (_: Exception) {}
            }
        }

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

                        val updateUiState by appUpdateManager.updateState.collectAsState()
                        var dismissedUpdateDialog by remember { mutableStateOf(false) }
                        val githubUpdateInfo = (updateUiState as? InAppUpdateManager.UpdateUiState.AvailableGitHub)?.info
                        if (githubUpdateInfo != null && !dismissedUpdateDialog) {
                            AlertDialog(
                                onDismissRequest = { dismissedUpdateDialog = true },
                                modifier = Modifier.testTag("github_update_dialog"),
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.SystemUpdate,
                                        contentDescription = localizedText(
                                            uiLanguage,
                                            "নতুন অ্যাপ আপডেট",
                                            "New App Update"
                                        ),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                title = {
                                    Text(
                                        text = localizedText(
                                            uiLanguage,
                                            "নতুন আপডেট পাওয়া গেছে (v${githubUpdateInfo.versionName})",
                                            "New Update Available (v${githubUpdateInfo.versionName})"
                                        ),
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                text = {
                                    val summary = githubUpdateInfo.releaseNotes.take(260).ifBlank {
                                        localizedText(
                                            uiLanguage,
                                            "নতুন বাগ ফিক্স এবং উন্নত অভিজ্ঞতা সহ নতুন APK সংস্করণ প্রস্তুত। নিচের বাটনে ট্যাপ করে সরাসরি ডাউনলোড করুন।",
                                            "A new APK version with bug fixes and improvements is ready. Tap below to download directly."
                                        )
                                    }
                                    Text(text = summary)
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            dismissedUpdateDialog = true
                                            InAppUpdateManager.downloadApkDirectly(
                                                context = this@MainActivity,
                                                downloadUrl = githubUpdateInfo.apkDownloadUrl,
                                                versionName = githubUpdateInfo.versionName
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier
                                            .defaultMinSize(minHeight = 48.dp)
                                            .testTag("github_update_download_button")
                                    ) {
                                        Text(
                                            text = localizedText(
                                                uiLanguage,
                                                "ডাউনলোড করুন",
                                                "Download APK"
                                            )
                                        )
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { dismissedUpdateDialog = true },
                                        modifier = Modifier
                                            .defaultMinSize(minHeight = 48.dp)
                                            .testTag("github_update_later_button")
                                    ) {
                                        Text(
                                            text = localizedText(uiLanguage, "পরে", "Later"),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleUpdateNotificationIntent(intent)
    }

    private fun handleUpdateNotificationIntent(intent: Intent?) {
        val downloadUrl = intent?.getStringExtra(InAppUpdateManager.EXTRA_UPDATE_DOWNLOAD_URL)
        if (!downloadUrl.isNullOrBlank()) {
            val versionName = intent.getStringExtra(InAppUpdateManager.EXTRA_UPDATE_VERSION_NAME).orEmpty()
            intent.removeExtra(InAppUpdateManager.EXTRA_UPDATE_DOWNLOAD_URL)
            intent.removeExtra(InAppUpdateManager.EXTRA_UPDATE_VERSION_NAME)
            InAppUpdateManager.downloadApkDirectly(
                context = this,
                downloadUrl = downloadUrl,
                versionName = versionName
            )
        }
    }

    override fun onDestroy() {
        appUpdateManager.unregister()
        super.onDestroy()
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
