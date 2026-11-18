package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.DailyAiNasihah
import com.example.data.ai.DailyNasihahUiState
import com.example.ui.theme.*

/**
 * Dynamic AI-Powered Daily Believer's Nasihah Card.
 * Renders fresh daily reflections generated via Firebase AI Logic (gemini-3.8-flash)
 * strictly matching the currently selected UI language, with Regenerate, Copy, and Share actions.
 */
@Composable
fun IslamicNasihatPlayCard(
    uiState: DailyNasihahUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val uiLang = LocalAppLanguage.current

    FrostedGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_daily_nasihah_card"),
        backgroundColor = DarkEmeraldElevated,
        borderColor = ChampagneGold.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: AI Icon + Title + Refresh/Regenerate Button
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ChampagneGold.copy(alpha = 0.28f), DarkEmeraldSurface)
                                )
                            )
                            .border(0.5.dp, ChampagneGold.copy(alpha = 0.55f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = uiText("মুমিনের নসিহত", "Believer's Nasihah"),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                        val subtitleLabel = when (uiState) {
                            is DailyNasihahUiState.Ready -> uiState.nasihah.categoryLabel.ifBlank {
                                uiText("এআই দৈনিক হিকমাহ", "AI Daily Wisdom")
                            }
                            is DailyNasihahUiState.Error -> uiState.cachedFallback?.categoryLabel
                                ?: uiText("এআই দৈনিক হিকমাহ", "AI Daily Wisdom")
                            DailyNasihahUiState.Loading -> uiText("এআই দৈনিক হিকমাহ", "AI Daily Wisdom")
                        }
                        Text(
                            text = subtitleLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MintGlow,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                // Refresh / Generate New Nasihah button (min 48dp touch target via IconButton)
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onRefresh()
                    },
                    enabled = uiState !is DailyNasihahUiState.Loading,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("ai_nasihah_refresh_button")
                ) {
                    if (uiState is DailyNasihahUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ChampagneGold,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = uiText("নতুন নসিহত", "New Nasihah"),
                            tint = ChampagneGold,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = uiState,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                label = "ai_nasihah_state"
            ) { state ->
                when (state) {
                    DailyNasihahUiState.Loading -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MintGlow,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = uiText("আজকের এআই নসিহত তৈরি হচ্ছে...", "Generating today's AI Nasihah..."),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateGrayMuted,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    is DailyNasihahUiState.Ready -> {
                        NasihahBodyContent(
                            nasihah = state.nasihah,
                            uiLang = uiLang,
                            context = context,
                            onRefresh = onRefresh
                        )
                    }

                    is DailyNasihahUiState.Error -> {
                        val fallback = state.cachedFallback
                        if (fallback != null) {
                            NasihahBodyContent(
                                nasihah = fallback,
                                uiLang = uiLang,
                                context = context,
                                onRefresh = onRefresh
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MintGlow,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = uiText("আজকের এআই নসিহত তৈরি হচ্ছে...", "Generating today's AI Nasihah..."),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SlateGrayMuted,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NasihahBodyContent(
    nasihah: DailyAiNasihah,
    uiLang: String,
    context: Context,
    onRefresh: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = nasihah.title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.5.sp,
                lineHeight = 20.sp
            )
        )

        if (nasihah.arabicQuote.isNotBlank()) {
            val contentLang = LocalContentLanguage.current
            val quoteTransliteration = remember(nasihah.arabicQuote, contentLang) {
                com.example.data.util.ArabicTransliterationHelper.transliterate(
                    arabic = nasihah.arabicQuote,
                    langCode = contentLang
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkEmeraldSurface)
                    .border(0.5.dp, ChampagneGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = nasihah.arabicQuote,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = AmiriFontFamily,
                        color = ChampagneGold,
                        fontSize = 15.5.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                )
                if (quoteTransliteration.isNotBlank() && quoteTransliteration != nasihah.arabicQuote) {
                    Text(
                        text = quoteTransliteration,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MintGlow.copy(alpha = 0.9f),
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        Text(
            text = nasihah.adviceText,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextPrimary.copy(alpha = 0.92f),
                fontSize = 12.5.sp,
                lineHeight = 18.sp
            )
        )

        if (nasihah.actionPoint.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MintGlow.copy(alpha = 0.08f))
                    .border(0.5.dp, MintGlow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = uiText("বাস্তব জীবনের আমল:", "Practical Action:"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MintGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = nasihah.actionPoint,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary.copy(alpha = 0.88f),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        if (nasihah.reference.isNotBlank()) {
            Text(
                text = "${uiText("রেফারেন্স:", "Reference:")} ${nasihah.reference}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextTertiary,
                    fontSize = 10.5.sp
                )
            )
        }

        HorizontalDivider(
            color = HairlineBorder,
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 2.dp)
        )

        val copiedToastText = uiText("নসিহতটি কপি করা হয়েছে", "Nasihat copied to clipboard")
        val shareChooserTitle = uiText("নসিহত শেয়ার করুন", "Share Nasihat")
        val copyDesc = uiText("কপি করুন", "Copy")
        val shareDesc = uiText("শেয়ার করুন", "Share")
        val shareBody = remember(nasihah) { nasihah.buildShareableText() }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRefresh()
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = DarkEmeraldSurface,
                    contentColor = ChampagneGold
                ),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .testTag("ai_nasihah_new_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = uiText("নতুন নসিহত", "New Nasihah"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Believer Nasihah", shareBody))
                        Toast.makeText(context, copiedToastText, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("ai_nasihah_copy_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = copyDesc,
                        tint = TextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareBody)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, shareChooserTitle))
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("ai_nasihah_share_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = shareDesc,
                        tint = MintGlow,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}
