package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthState
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.Icon3DType
import com.example.ui.components.ThreeDimensionalIcon
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthViewModel
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
                    colors = listOf(
                        ObsidianBackground,
                        DarkEmeraldSurface,
                        ObsidianBackground
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP HEADER: 3D Mosque Logo & Spiritual Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ChampagneGold.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(1.5.dp, ChampagneGold.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ThreeDimensionalIcon(
                        type = Icon3DType.MOSQUE_HOME,
                        size = 50.dp,
                        isActive = true,
                        accentColor = ChampagneGold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = uiText("নূর-এ-হেদায়াত", "Noor-e-Hidayah"),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiText("Noor-e-Hidayah • নূরানী দ্বীনি সফর", "Noor-e-Hidayah • Spiritual Companion"),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = uiText(
                        "আমল সংরক্ষণ ও নিরাপদ সাইন-ইন করতে একাউন্টে প্রবেশ করুন",
                        "Sign in to sync your worship records and access cloud backup safely"
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SlateGrayMuted,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // MIDDLE CARD: Tab Switcher & Dynamic Input
            FrostedGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Segmented Tab Switcher (Google Primary vs Phone OTP)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkEmeraldElevated)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TabButton(
                            title = uiText("🌐 গুগল একাউন্ট", "🌐 Google Account"),
                            isSelected = selectedTab == AuthTabSelection.GOOGLE_AUTH,
                            onClick = {
                                selectedTab = AuthTabSelection.GOOGLE_AUTH
                                activeCodeSent = null
                                authViewModel.resetToPhoneInput()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        TabButton(
                            title = uiText("📱 মোবাইল ওটিপি", "📱 Phone OTP"),
                            isSelected = selectedTab == AuthTabSelection.PHONE_OTP,
                            onClick = {
                                selectedTab = AuthTabSelection.PHONE_OTP
                                activeCodeSent = null
                                authViewModel.resetToPhoneInput()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error Message Banner if error occurs
                    if (authState is AuthState.Error) {
                        val errorState = authState as AuthState.Error
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF4A1F24))
                                .border(0.5.dp, Color(0xFFE57373), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = uiText("ত্রুটি", "Error"),
                                    tint = Color(0xFFEF9A9A),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = errorState.message,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { authViewModel.resetToPhoneInput() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = uiText("বন্ধ", "Close"),
                                        tint = SlateGrayMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            if (errorState.isAppCheckError && com.example.BuildConfig.DEBUG) {
                                var showDebugTokenInput by remember { mutableStateOf(false) }
                                var debugTokenText by remember {
                                    mutableStateOf(com.example.data.security.getOrCreateDeviceDebugToken(context))
                                }
                                var tokenStatusMsg by remember { mutableStateOf<String?>(null) }

                                TextButton(
                                    onClick = { showDebugTokenInput = !showDebugTokenInput },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("toggle_debug_token_helper_button")
                                ) {
                                    Text(
                                        text = if (showDebugTokenInput) {
                                            uiText("Debug Token অপশন লুকান", "Hide Debug Token Option")
                                        } else {
                                            uiText("🔑 এই ফোনের Debug Token দেখুন ও লগইন করুন", "🔑 Use App Check Debug Token")
                                        },
                                        color = ChampagneGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                if (showDebugTokenInput) {
                                    Text(
                                        text = uiText(
                                            "১) নিচের টোকেনটি কপি করুন → ২) Firebase Console খুলে App Check → Manage debug tokens-এ যোগ করুন → ৩) নিচের 'টোকেন সেট করুন ও গুগল লগইন করুন' বাটনে ট্যাপ করুন:",
                                            "1) Copy this token -> 2) Open Firebase Console -> App Check -> Manage debug tokens -> Add -> 3) Tap 'Apply Token & Sign In':"
                                        ),
                                        color = Color(0xFFFFE0B2),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
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
                                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE)
                                                    as? android.content.ClipboardManager
                                                cm?.setPrimaryClip(
                                                    android.content.ClipData.newPlainText(
                                                        "AppCheckDebugToken",
                                                        debugTokenText.trim()
                                                    )
                                                )
                                                tokenStatusMsg = "টোকেন কপি হয়েছে! এখন Firebase Console-এ পেস্ট করুন।"
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .defaultMinSize(minHeight = 48.dp)
                                                .testTag("copy_debug_token_button"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                                        ) {
                                            Text(
                                                text = uiText("কপি করুন", "Copy Token"),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                try {
                                                    val intent = android.content.Intent(
                                                        android.content.Intent.ACTION_VIEW,
                                                        android.net.Uri.parse(
                                                            com.example.data.security.FIREBASE_APP_CHECK_CONSOLE_URL
                                                        )
                                                    ).apply {
                                                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    }
                                                    context.startActivity(intent)
                                                } catch (_: Exception) {
                                                    tokenStatusMsg = "ব্রাউজার খুলতে সমস্যা হয়েছে।"
                                                }
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .defaultMinSize(minHeight = 48.dp)
                                                .testTag("open_firebase_console_button"),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ChampagneGold)
                                        ) {
                                            Text(
                                                text = uiText("Firebase খুলুন", "Open Console"),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = {
                                            val installed = com.example.data.security.installManualDebugToken(
                                                context,
                                                debugTokenText
                                            )
                                            if (installed) {
                                                tokenStatusMsg = null
                                                showDebugTokenInput = false
                                                activity?.let { authViewModel.signInWithGoogle(it) }
                                            } else {
                                                tokenStatusMsg = "সঠিক UUID ফরম্যাটের Debug Token দিন"
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
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = uiText("টোকেন সেট করুন ও গুগল লগইন করুন", "Apply Token & Sign In"),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    tokenStatusMsg?.let { status ->
                                        Text(
                                            text = status,
                                            color = Color(0xFFFFCC80),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            if (errorState.suggestGoogleFallback && activity != null) {
                                OutlinedButton(
                                    onClick = {
                                        selectedTab = AuthTabSelection.GOOGLE_AUTH
                                        authViewModel.signInWithGoogle(activity)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 48.dp)
                                        .testTag("error_google_fallback_button"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.White,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(
                                        text = uiText(
                                            "🌐 গুগল একাউন্ট দিয়ে তাৎক্ষণিক লগইন করুন",
                                            "🌐 Sign in instantly with Google Account"
                                        ),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Content Switcher
                    AnimatedContent(
                        targetState = selectedTab,
                        label = "auth_tab_crossfade"
                    ) { tab ->
                        when (tab) {
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
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTTOM ACTIONS: Guest Access & Trust Badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Continue as guest button
                TextButton(
                    onClick = {
                        authViewModel.continueAsGuest()
                        onAuthSuccess()
                    },
                    modifier = Modifier.testTag("continue_as_guest_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = uiText("অতিথি হিসেবে অ্যাপ অন্বেষণ করুন", "Explore as Guest"),
                            color = SlateGrayMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = uiText("পরবর্তী", "Next"),
                            tint = SlateGrayMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Security & Privacy guarantee pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkEmeraldElevated.copy(alpha = 0.6f))
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = uiText("নিরাপত্তা", "Security"),
                        tint = MintGlow,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = uiText(
                            "১০০% বিজ্ঞাপনমুক্ত • সাদাকাহ জারিয়াহ • নিরাপদ ক্লাউড",
                            "100% Ad-Free • Sadaqah Jariyah • Secure Cloud"
                        ),
                        color = SlateGrayMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) ChampagneGold.copy(alpha = 0.2f) else Color.Transparent
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) ChampagneGold else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) ChampagneGold else SlateGrayMuted,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
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
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiText(
                    "আপনার নম্বরে ৬ সংখ্যার ফ্রি এসএমএস ওটিপি কোড পাঠানো হবে",
                    "A 6-digit SMS OTP code will be sent to your number"
                ),
                style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Phone Input Row with +880 Prefix Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // BD Prefix Badge
                Box(
                    modifier = Modifier
                        .height(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldElevated)
                        .border(1.dp, ChampagneGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🇧🇩", fontSize = 16.sp)
                        Text(
                            text = "+880",
                            color = ChampagneGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Phone Number TextField
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = onPhoneChange,
                    placeholder = {
                        Text("1712345678", color = SlateGrayMuted.copy(alpha = 0.6f), fontSize = 14.sp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("phone_number_input"),
                    shape = RoundedCornerShape(12.dp),
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

            // Send OTP Button
            val isButtonEnabled = phoneNumber.length >= 10 && authState !is AuthState.Loading
            Button(
                onClick = onSendOtp,
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("send_otp_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChampagneGold,
                    contentColor = ObsidianBackground,
                    disabledContainerColor = DarkEmeraldElevated,
                    disabledContentColor = SlateGrayMuted
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ObsidianBackground,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiText("কোড পাঠানো হচ্ছে...", "Sending code..."),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiText("ওটিপি কোড পাঠান", "Send OTP Code"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = uiText("পাঠান", "Send"),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onSwitchToGoogle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiText(
                        "এসএমএস ছাড়াই তাৎক্ষণিক গুগল একাউন্ট দিয়ে লগইন করুন →",
                        "Sign in instantly with Google without SMS →"
                    ),
                    color = MintGlow,
                    fontSize = 11.sp,
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
                Column {
                    Text(
                        text = uiText("৬ সংখ্যার ওটিপি কোড দিন", "Enter 6-Digit OTP Code"),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = uiText(
                            "${codeSentState.phoneNumber} নম্বরে পাঠানো হয়েছে",
                            "Sent to ${codeSentState.phoneNumber}"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = MintGlow)
                    )
                }

                TextButton(
                    onClick = onChangeNumber,
                    modifier = Modifier.testTag("change_phone_number_button")
                ) {
                    Text(
                        text = uiText("নম্বর পরিবর্তন", "Change Number"),
                        color = ChampagneGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6-digit OTP Input TextField
            OutlinedTextField(
                value = otpInput,
                onValueChange = onOtpChange,
                placeholder = {
                    Text(
                        "• • • • • •",
                        color = SlateGrayMuted,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("otp_code_input"),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    color = ChampagneGold,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp
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

            Spacer(modifier = Modifier.height(14.dp))

            // Resend link with countdown
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
                        fontSize = 11.sp
                    )
                } else {
                    TextButton(
                        onClick = onResendOtp,
                        modifier = Modifier.testTag("resend_otp_button")
                    ) {
                        Text(
                            text = uiText("🔄 পুনরায় কোড পাঠান", "🔄 Resend Code"),
                            color = ChampagneGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Verify Button
            Button(
                onClick = onVerifyOtp,
                enabled = otpInput.length == 6 && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("verify_otp_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MintGlow,
                    contentColor = ObsidianBackground,
                    disabledContainerColor = DarkEmeraldElevated,
                    disabledContentColor = SlateGrayMuted
                ),
                shape = RoundedCornerShape(12.dp)
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
                        fontSize = 13.sp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = uiText("যাচাই", "Verify"),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = uiText("যাচাই করুন ও প্রবেশ করুন", "Verify & Continue"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Google Sign-In Section
 */
@Composable
private fun GoogleAuthContent(
    authState: AuthState,
    onSignInClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiText("এক ট্যাপে গুগল দিয়ে সাইন ইন", "One-Tap Google Sign-In"),
            style = MaterialTheme.typography.titleSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = uiText(
                "কোনো পাসওয়ার্ড মনে রাখার প্রয়োজন নেই। আপনার গুগল একাউন্টে স্বয়ংক্রিয়ভাবে সংরক্ষিত হবে।",
                "No password required. Your data syncs automatically with your Google Account."
            ),
            style = MaterialTheme.typography.bodySmall.copy(
                color = SlateGrayMuted,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Native Google Account Picker info card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkEmeraldElevated)
                .border(1.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ChampagneGold.copy(alpha = 0.2f))
                        .border(1.dp, ChampagneGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = ChampagneGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiText("গুগল একাউন্ট", "Google Account"),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = uiText(
                            "ডিভাইসের গুগল একাউন্টে নিরাপদ ব্যাকআপ",
                            "Secure backup via your device Google account"
                        ),
                        color = SlateGrayMuted,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MintGlow.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = uiText("প্রস্তুত", "Ready"),
                        color = MintGlow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Google Sign-In Button
        Button(
            onClick = onSignInClick,
            enabled = authState !is AuthState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("google_signin_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiText("সাইন ইন হচ্ছে...", "Signing in..."),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🌐",
                        fontSize = 18.sp
                    )
                    Text(
                        text = uiText("Google দিয়ে লগইন করুন", "Continue with Google"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
