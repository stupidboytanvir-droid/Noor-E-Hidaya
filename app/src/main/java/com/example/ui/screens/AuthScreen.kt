package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.auth.AuthState
import com.example.data.security.FIREBASE_APP_CHECK_CONSOLE_URL
import com.example.data.security.getOrCreateDeviceDebugToken
import com.example.data.security.installManualDebugToken
import com.example.ui.components.Icon3DType
import com.example.ui.components.ThreeDimensionalIcon
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldMuted
import com.example.ui.theme.DarkEmeraldCard
import com.example.ui.theme.DarkEmeraldElevated
import com.example.ui.theme.DarkEmeraldSurface
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.MintGlow
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SlateGrayMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.uiText
import com.example.ui.viewmodel.AuthViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

enum class AuthTabSelection {
    GOOGLE_AUTH,
    PHONE_OTP
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val focusManager = LocalFocusManager.current

    val authState by authViewModel.authState.collectAsState()
    val phoneNumber by authViewModel.phoneNumberInput.collectAsState()
    val otpInput by authViewModel.otpInput.collectAsState()

    var selectedTab by remember { mutableStateOf(AuthTabSelection.GOOGLE_AUTH) }
    var activeCodeSent by remember { mutableStateOf<AuthState.PhoneCodeSent?>(null) }
    var resendCountdown by remember { mutableIntStateOf(45) }
    var isCountdownActive by remember { mutableStateOf(false) }

    // React to successful login & preserve active OTP session across Loading state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                activeCodeSent = null
                onAuthSuccess()
            }
            is AuthState.PhoneCodeSent -> {
                activeCodeSent = state
                isCountdownActive = true
                resendCountdown = 45
            }
            is AuthState.Idle -> {
                activeCodeSent = null
            }
            else -> Unit
        }
    }

    // Resend countdown timer
    LaunchedEffect(isCountdownActive, resendCountdown) {
        if (isCountdownActive && resendCountdown > 0) {
            delay(1000L)
            resendCountdown -= 1
        } else if (resendCountdown == 0) {
            isCountdownActive = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFF040D0C),
                        0.32f to Color(0xFF0A1B18),
                        0.72f to DarkEmeraldSurface,
                        1.0f to ObsidianBackground
                    )
                )
            )
            .drawBehind {
                // Subtle golden halo behind the top crest
                val topCenter = Offset(size.width * 0.5f, size.height * 0.14f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ChampagneGold.copy(alpha = 0.16f),
                            MintGlow.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = topCenter,
                        radius = size.minDimension * 0.58f
                    ),
                    radius = size.minDimension * 0.58f,
                    center = topCenter
                )

                // Delicate Islamic Rub el Hizb (8-point geometric star) watermark behind header
                val starRadius = size.minDimension * 0.34f
                val strokeColor = ChampagneGold.copy(alpha = 0.07f)
                for (rotDeg in listOf(0.0, 45.0)) {
                    val path = Path()
                    for (i in 0 until 4) {
                        val angle = (rotDeg + i * 90.0) * PI / 180.0
                        val x = topCenter.x + (starRadius * cos(angle)).toFloat()
                        val y = topCenter.y + (starRadius * sin(angle)).toFloat()
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()
                    drawPath(path = path, color = strokeColor, style = Stroke(width = 1.2.dp.toPx()))
                }
                drawCircle(
                    color = ChampagneGold.copy(alpha = 0.06f),
                    radius = starRadius * 0.72f,
                    center = topCenter,
                    style = Stroke(width = 1.dp.toPx())
                )

                // Subtle bottom emerald aura
                val bottomGlow = Offset(size.width * 0.5f, size.height * 0.92f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MintGlow.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = bottomGlow,
                        radius = size.minDimension * 0.65f
                    ),
                    radius = size.minDimension * 0.65f,
                    center = bottomGlow
                )
            }
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TOP SPIRITUAL CREST & BRANDING
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer subtle golden halo ring
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        ChampagneGold.copy(alpha = 0.55f),
                                        ChampagneGold.copy(alpha = 0.10f),
                                        MintGlow.copy(alpha = 0.35f)
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                    // Inner medallion
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF1A3530),
                                        Color(0xFF0A1917)
                                    )
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        ChampagneGold.copy(alpha = 0.85f),
                                        ChampagneGoldMuted.copy(alpha = 0.35f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ThreeDimensionalIcon(
                            type = Icon3DType.MOSQUE_HOME,
                            size = 48.dp,
                            isActive = true,
                            accentColor = ChampagneGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "نُورُ الْهِدَايَةِ",
                    fontFamily = AmiriFontFamily,
                    fontSize = 22.sp,
                    color = ChampagneGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = uiText("নূর-এ-হেদায়াত", "Noor-e-Hidayah"),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        letterSpacing = 0.3.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = uiText(
                        "আপনার প্রতিদিনের আমল, তাসবিহ ও কুরআন পাঠ নিরাপদে সংরক্ষণ করুন",
                        "Keep your daily worship, Tasbeeh & Quran journey synced securely"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGrayMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Compact 3-pillar trust highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    AuthHighlightChip(
                        icon = Icons.Default.CloudDone,
                        label = uiText("ক্লাউড ব্যাকআপ", "Cloud Sync"),
                        accent = MintGlow
                    )
                    AuthHighlightChip(
                        icon = Icons.Default.VerifiedUser,
                        label = uiText("আলাদা প্রোফাইল", "Private Data"),
                        accent = ChampagneGold
                    )
                    AuthHighlightChip(
                        icon = Icons.Default.AutoAwesome,
                        label = uiText("বিজ্ঞাপনমুক্ত", "Ad-Free"),
                        accent = Color(0xFF7CE8C1)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ELEVATED AUTH CARD
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    ChampagneGold.copy(alpha = 0.42f),
                                    HairlineBorder,
                                    Color.White.copy(alpha = 0.05f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    color = DarkEmeraldCard.copy(alpha = 0.88f),
                    tonalElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 22.dp)
                    ) {
                        // Refined Segmented Mode Switcher (Material Icons, zero emojis)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF071210))
                                .border(1.dp, HairlineBorder, RoundedCornerShape(14.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AuthTabButton(
                                icon = Icons.Default.AccountCircle,
                                title = uiText("গুগল একাউন্ট", "Google Account"),
                                isSelected = selectedTab == AuthTabSelection.GOOGLE_AUTH,
                                onClick = {
                                    selectedTab = AuthTabSelection.GOOGLE_AUTH
                                    activeCodeSent = null
                                    authViewModel.resetToPhoneInput()
                                },
                                modifier = Modifier.weight(1f)
                            )

                            AuthTabButton(
                                icon = Icons.Default.PhoneAndroid,
                                title = uiText("মোবাইল ওটিপি", "Phone OTP"),
                                isSelected = selectedTab == AuthTabSelection.PHONE_OTP,
                                onClick = {
                                    selectedTab = AuthTabSelection.PHONE_OTP
                                    activeCodeSent = null
                                    authViewModel.resetToPhoneInput()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Animated Error Banner & Debug Token Helper
                        AnimatedVisibility(
                            visible = authState is AuthState.Error,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            val errorState = authState as? AuthState.Error
                            if (errorState != null) {
                                Column {
                                    AuthErrorBanner(
                                        errorState = errorState,
                                        context = context,
                                        activity = activity,
                                        onDismiss = { authViewModel.resetToPhoneInput() },
                                        onGoogleFallback = {
                                            activity?.let { act ->
                                                selectedTab = AuthTabSelection.GOOGLE_AUTH
                                                authViewModel.signInWithGoogle(act)
                                            }
                                        },
                                        onDebugTokenApplied = {
                                            activity?.let { act ->
                                                authViewModel.signInWithGoogle(act)
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }

                        // Smooth Tab Content Crossfade
                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                fadeIn(spring(stiffness = Spring.StiffnessMediumLow)) togetherWith
                                    fadeOut(spring(stiffness = Spring.StiffnessMediumLow))
                            },
                            label = "auth_tab_crossfade"
                        ) { tab ->
                            when (tab) {
                                AuthTabSelection.GOOGLE_AUTH -> {
                                    GoogleAuthContent(
                                        authState = authState,
                                        onSignInClick = {
                                            activity?.let { act ->
                                                focusManager.clearFocus()
                                                authViewModel.signInWithGoogle(act)
                                            }
                                        }
                                    )
                                }

                                AuthTabSelection.PHONE_OTP -> {
                                    PhoneOtpAuthContent(
                                        authState = authState,
                                        activeCodeSent = activeCodeSent,
                                        phoneNumber = phoneNumber,
                                        otpInput = otpInput,
                                        resendCountdown = resendCountdown,
                                        isCountdownActive = isCountdownActive,
                                        onPhoneChange = { authViewModel.onPhoneNumberChanged(it) },
                                        onOtpChange = { authViewModel.onOtpChanged(it) },
                                        onSendOtp = {
                                            activity?.let {
                                                focusManager.clearFocus()
                                                authViewModel.sendPhoneOtp(it)
                                            }
                                        },
                                        onResendOtp = {
                                            activity?.let {
                                                authViewModel.resendPhoneOtp(it)
                                            }
                                        },
                                        onVerifyOtp = {
                                            focusManager.clearFocus()
                                            authViewModel.verifyOtp()
                                        },
                                        onChangeNumber = {
                                            activeCodeSent = null
                                            authViewModel.resetToPhoneInput()
                                        },
                                        onSwitchToGoogle = {
                                            selectedTab = AuthTabSelection.GOOGLE_AUTH
                                            activeCodeSent = null
                                            authViewModel.resetToPhoneInput()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BOTTOM ACTIONS: Guest CTA & Privacy Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        authViewModel.continueAsGuest()
                        onAuthSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("continue_as_guest_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.14f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkEmeraldElevated.copy(alpha = 0.38f),
                        contentColor = TextPrimary
                    )
                ) {
                    Text(
                        text = uiText("অতিথি হিসেবে অ্যাপ ঘুরে দেখুন", "Explore as Guest"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = uiText("পরবর্তী", "Next"),
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF081412))
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = uiText("নিরাপত্তা", "Security"),
                        tint = MintGlow,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = uiText(
                            "১০০% বিজ্ঞাপনমুক্ত • সাদাকাহ জারিয়াহ • এনক্রিপ্টেড ক্লাউড",
                            "100% Ad-Free • Sadaqah Jariyah • Encrypted Cloud"
                        ),
                        color = SlateGrayMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthHighlightChip(
    icon: ImageVector,
    label: String,
    accent: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(DarkEmeraldElevated.copy(alpha = 0.65f))
            .border(0.75.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = label,
            color = TextPrimary.copy(alpha = 0.88f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AuthTabButton(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) ChampagneGold.copy(alpha = 0.18f) else Color.Transparent,
        label = "tab_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) ChampagneGold.copy(alpha = 0.7f) else Color.Transparent,
        label = "tab_border"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) ChampagneGold else SlateGrayMuted,
        label = "tab_content"
    )

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            color = contentColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AuthErrorBanner(
    errorState: AuthState.Error,
    context: Context,
    activity: Activity?,
    onDismiss: () -> Unit,
    onGoogleFallback: () -> Unit,
    onDebugTokenApplied: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF3E181D))
            .border(1.dp, Color(0xFFE57373).copy(alpha = 0.65f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = uiText("ত্রুটি", "Error"),
                tint = Color(0xFFFFB4AB),
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(20.dp)
            )
            Text(
                text = errorState.message,
                color = Color.White,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(32.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = uiText("বন্ধ করুন", "Dismiss"),
                    tint = Color(0xFFFFDAD6),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (errorState.isAppCheckError && BuildConfig.DEBUG) {
            var showDebugTokenInput by remember { mutableStateOf(false) }
            var debugTokenText by remember {
                mutableStateOf(getOrCreateDeviceDebugToken(context))
            }
            var tokenStatusMsg by remember { mutableStateOf<String?>(null) }

            TextButton(
                onClick = { showDebugTokenInput = !showDebugTokenInput },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("toggle_debug_token_helper_button")
            ) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showDebugTokenInput) {
                        uiText("Debug Token অপশন লুকান", "Hide Debug Token Option")
                    } else {
                        uiText("এই ফোনের Debug Token দেখুন ও লগইন করুন", "Use App Check Debug Token")
                    },
                    color = ChampagneGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (showDebugTokenInput) {
                val copiedMsg = uiText(
                    "টোকেন কপি হয়েছে! এখন Firebase Console-এ পেস্ট করুন।",
                    "Token copied! Paste it in Firebase Console."
                )
                val browserErrMsg = uiText(
                    "ব্রাউজার খুলতে সমস্যা হয়েছে।",
                    "Unable to open browser."
                )
                val invalidUuidMsg = uiText(
                    "সঠিক UUID ফরম্যাটের Debug Token দিন",
                    "Enter a valid UUID Debug Token"
                )
                Text(
                    text = uiText(
                        "১) নিচের টোকেনটি কপি করুন → ২) Firebase Console খুলে App Check → Manage debug tokens-এ যোগ করুন → ৩) নিচের বাটনে ট্যাপ করুন:",
                        "1) Copy this token -> 2) Open Firebase Console -> App Check -> Manage debug tokens -> Add -> 3) Tap Apply & Sign In:"
                    ),
                    color = Color(0xFFFFE0B2),
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )
                OutlinedTextField(
                    value = debugTokenText,
                    onValueChange = { debugTokenText = it },
                    placeholder = {
                        Text(
                            text = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
                            color = SlateGrayMuted,
                            fontSize = 12.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debug_token_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedContainerColor = DarkEmeraldElevated,
                        unfocusedContainerColor = DarkEmeraldElevated
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            cm?.setPrimaryClip(
                                ClipData.newPlainText("AppCheckDebugToken", debugTokenText.trim())
                            )
                            tokenStatusMsg = copiedMsg
                        },
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("copy_debug_token_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = uiText("কপি করুন", "Copy"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(FIREBASE_APP_CHECK_CONSOLE_URL)
                                ).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                tokenStatusMsg = browserErrMsg
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("open_firebase_console_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ChampagneGold)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = uiText("Firebase খুলুন", "Console"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Button(
                    onClick = {
                        val installed = installManualDebugToken(context, debugTokenText)
                        if (installed) {
                            tokenStatusMsg = null
                            showDebugTokenInput = false
                            onDebugTokenApplied()
                        } else {
                            tokenStatusMsg = invalidUuidMsg
                        }
                    },
                    enabled = debugTokenText.trim().length >= 16 && activity != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("apply_debug_token_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = uiText("টোকেন সেট করুন ও গুগল লগইন করুন", "Apply Token & Sign In"),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                tokenStatusMsg?.let { status ->
                    Text(
                        text = status,
                        color = Color(0xFFFFCC80),
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        if (errorState.suggestGoogleFallback && activity != null) {
            Button(
                onClick = onGoogleFallback,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("error_google_fallback_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1F1F1F)
                )
            ) {
                GoogleLogoMark(modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiText(
                        "গুগল একাউন্ট দিয়ে তাৎক্ষণিক লগইন করুন",
                        "Sign in instantly with Google Account"
                    ),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Crisp multi-color Google "G" emblem drawn on Canvas (no emoji, sharp on every DPI).
 */
@Composable
private fun GoogleLogoMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.21f
        val inset = strokeWidth / 2f
        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
        val topLeft = Offset(inset, inset)

        // Red (top)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = -145f,
            sweepAngle = 105f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Yellow (left)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 145f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Green (bottom)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 40f,
            sweepAngle = 105f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Blue (right lower curve)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -10f,
            sweepAngle = 55f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        // Blue horizontal bar
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(size.width * 0.50f, size.height * 0.50f),
            end = Offset(size.width * 0.96f, size.height * 0.50f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Butt
        )
    }
}

/**
 * Crisp Bangladesh flag badge drawn on Canvas for the +880 prefix pill.
 */
@Composable
private fun BangladeshFlagIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRoundRect(
            color = Color(0xFF006A4E),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFF42A41),
            radius = size.minDimension * 0.28f,
            center = Offset(size.width * 0.46f, size.height * 0.5f)
        )
    }
}

/**
 * Google Sign-In Section — Refined One-Tap Card & Crisp Primary CTA
 */
@Composable
private fun GoogleAuthContent(
    authState: AuthState,
    onSignInClick: () -> Unit
) {
    val isLoading = authState is AuthState.Loading

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiText("এক ট্যাপে নিরাপদ গুগল সাইন-ইন", "One-Tap Google Sign-In"),
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = uiText(
                "কোনো পাসওয়ার্ড লাগবে না। প্রতিটি জিমেইল একাউন্টে আপনার আমলের হিসাব আলাদাভাবে সংরক্ষিত থাকবে।",
                "No password required. Each Google account keeps its own private worship progress in the cloud."
            ),
            style = MaterialTheme.typography.bodySmall.copy(
                color = SlateGrayMuted,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Concise 2-row benefit summary inside elevated panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkEmeraldElevated.copy(alpha = 0.72f))
                .border(1.dp, ChampagneGold.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AuthFeatureRow(
                icon = Icons.Default.Sync,
                iconTint = MintGlow,
                title = uiText("স্বয়ংক্রিয় ক্লাউড ব্যাকআপ", "Automatic Cloud Restore"),
                subtitle = uiText(
                    "ফোন বদলালেও কুরআন, তাসবিহ ও নামাজের হিসাব ফিরে পাবেন",
                    "Restore your Quran, Tasbeeh & Salah logs on any device"
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(HairlineBorder)
            )
            AuthFeatureRow(
                icon = Icons.Default.Shield,
                iconTint = ChampagneGold,
                title = uiText("প্রতিটি একাউন্ট সম্পূর্ণ আলাদা", "Isolated Per-User Profile"),
                subtitle = uiText(
                    "নতুন একাউন্টে ০ থেকে নতুনভাবে শুরু হবে — অন্য ডেটা মিশবে না",
                    "New accounts start clean at 0 without mixing other data"
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Primary Google Sign-In CTA Button
        Button(
            onClick = onSignInClick,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("google_signin_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF1F1F1F),
                disabledContainerColor = Color.White.copy(alpha = 0.65f),
                disabledContentColor = Color(0xFF1F1F1F).copy(alpha = 0.65f)
            ),
            shape = RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color(0xFF1F1F1F),
                    strokeWidth = 2.2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = uiText("সাইন ইন হচ্ছে...", "Signing in..."),
                    color = Color(0xFF1F1F1F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            } else {
                GoogleLogoMark(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = uiText("Google দিয়ে লগইন করুন", "Continue with Google"),
                    color = Color(0xFF1F1F1F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun AuthFeatureRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.14f))
                .border(0.75.dp, iconTint.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = SlateGrayMuted,
                fontSize = 11.5.sp,
                lineHeight = 15.5.sp
            )
        }
    }
}

/**
 * Bangladeshi Phone OTP Authentication Section
 */
@Composable
private fun PhoneOtpAuthContent(
    authState: AuthState,
    activeCodeSent: AuthState.PhoneCodeSent?,
    phoneNumber: String,
    otpInput: String,
    resendCountdown: Int,
    isCountdownActive: Boolean,
    onPhoneChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onResendOtp: () -> Unit,
    onVerifyOtp: () -> Unit,
    onChangeNumber: () -> Unit,
    onSwitchToGoogle: () -> Unit
) {
    val isLoading = authState is AuthState.Loading
    val codeSentState = (authState as? AuthState.PhoneCodeSent) ?: activeCodeSent

    Column(modifier = Modifier.fillMaxWidth()) {
        if (codeSentState == null) {
            // STEP 1: Phone number entry
            Text(
                text = uiText("বাংলাদেশি মোবাইল নম্বর লিখুন", "Enter Bangladeshi Mobile Number"),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiText(
                    "আপনার নম্বরে ৬ সংখ্যার ফ্রি এসএমএস ওটিপি কোড পাঠানো হবে",
                    "We'll send a 6-digit SMS verification code to your number"
                ),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SlateGrayMuted,
                    fontSize = 12.5.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Input Row with Crisp BD +880 Prefix Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkEmeraldElevated)
                        .border(1.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    BangladeshFlagIcon(
                        modifier = Modifier
                            .width(22.dp)
                            .height(15.dp)
                    )
                    Text(
                        text = "+880",
                        color = ChampagneGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = onPhoneChange,
                    placeholder = {
                        Text(
                            text = "1712345678",
                            color = SlateGrayMuted.copy(alpha = 0.55f),
                            fontSize = 15.sp
                        )
                    },
                    trailingIcon = {
                        if (phoneNumber.isNotEmpty()) {
                            IconButton(
                                onClick = { onPhoneChange("") },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = uiText("মুছুন", "Clear"),
                                    tint = SlateGrayMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 56.dp)
                        .testTag("phone_number_input"),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (phoneNumber.length >= 10) onSendOtp()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ChampagneGold,
                        unfocusedBorderColor = HairlineBorder,
                        focusedContainerColor = DarkEmeraldElevated,
                        unfocusedContainerColor = DarkEmeraldElevated
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            val isButtonEnabled = phoneNumber.length >= 10 && !isLoading
            Button(
                onClick = onSendOtp,
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("send_otp_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground,
                    disabledContainerColor = DarkEmeraldElevated,
                    disabledContentColor = SlateGrayMuted
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ObsidianBackground,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiText("কোড পাঠানো হচ্ছে...", "Sending code..."),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = uiText("ওটিপি কোড পাঠান", "Send OTP Code"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = uiText("পাঠান", "Send"),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onSwitchToGoogle,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
            ) {
                Text(
                    text = uiText(
                        "এসএমএস ছাড়াই তাৎক্ষণিক গুগল একাউন্টে প্রবেশ করুন",
                        "Sign in instantly with Google instead"
                    ),
                    color = MintGlow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // STEP 2: OTP Verification entry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiText("৬ সংখ্যার ওটিপি কোড দিন", "Enter 6-Digit OTP"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = uiText(
                            "${codeSentState.phoneNumber} নম্বরে পাঠানো হয়েছে",
                            "Sent to ${codeSentState.phoneNumber}"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MintGlow,
                            fontSize = 12.sp
                        )
                    )
                }

                TextButton(
                    onClick = onChangeNumber,
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("change_phone_number_button"),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = uiText("পরিবর্তন", "Change"),
                        color = ChampagneGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6-digit visual progress slots above clean accessible OTP input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (index in 0 until 6) {
                    val ch = otpInput.getOrNull(index)?.toString() ?: ""
                    val isCurrent = index == otpInput.length
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkEmeraldElevated)
                            .border(
                                width = if (isCurrent || ch.isNotEmpty()) 1.5.dp else 1.dp,
                                color = when {
                                    ch.isNotEmpty() -> MintGlow.copy(alpha = 0.8f)
                                    isCurrent -> ChampagneGold
                                    else -> HairlineBorder
                                },
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ch.ifEmpty { "•" },
                            color = if (ch.isNotEmpty()) ChampagneGold else SlateGrayMuted.copy(alpha = 0.4f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = otpInput,
                onValueChange = onOtpChange,
                placeholder = {
                    Text(
                        text = uiText("এখানে ৬ সংখ্যার কোড লিখুন", "Type 6-digit code here"),
                        color = SlateGrayMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 54.dp)
                    .testTag("otp_code_input"),
                shape = RoundedCornerShape(14.dp),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = ChampagneGold,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (otpInput.length == 6) onVerifyOtp()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintGlow,
                    unfocusedBorderColor = HairlineBorder,
                    focusedContainerColor = DarkEmeraldElevated,
                    unfocusedContainerColor = DarkEmeraldElevated
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCountdownActive) {
                    Text(
                        text = uiText(
                            "পুনরায় কোড পাঠানোর সময় বাকি: ${resendCountdown}s",
                            "Resend available in ${resendCountdown}s"
                        ),
                        color = SlateGrayMuted,
                        fontSize = 12.sp
                    )
                } else {
                    TextButton(
                        onClick = onResendOtp,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("resend_otp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiText("পুনরায় কোড পাঠান", "Resend Code"),
                            color = ChampagneGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onVerifyOtp,
                enabled = otpInput.length == 6 && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("verify_otp_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MintGlow,
                    contentColor = ObsidianBackground,
                    disabledContainerColor = DarkEmeraldElevated,
                    disabledContentColor = SlateGrayMuted
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ObsidianBackground,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiText("যাচাই করা হচ্ছে...", "Verifying..."),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = uiText("যাচাই", "Verify"),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiText("যাচাই করুন ও প্রবেশ করুন", "Verify & Continue"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                }
            }
        }
    }
}
