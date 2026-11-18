package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun JumatSpecialHubCard(
    salawatCount: Int,
    onIncrementSalawat: () -> Unit,
    onOpenSurahKahf: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val isJumatActive = remember {
        val cal = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        // Active from Thursday 17:30 (Maghrib) through Friday 18:30 (Sunset)
        (dayOfWeek == Calendar.FRIDAY) || (dayOfWeek == Calendar.THURSDAY && hour >= 17)
    }

    var isExpanded by remember { mutableStateOf(isJumatActive) }

    // Persistent in-session checklist state
    var ghuslChecked by remember { mutableStateOf(false) }
    var cleanClothesChecked by remember { mutableStateOf(false) }
    var miswakChecked by remember { mutableStateOf(false) }
    var earlyMosqueChecked by remember { mutableStateOf(false) }
    var duaHourChecked by remember { mutableStateOf(false) }

    FrostedGlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = if (isJumatActive) DarkEmeraldElevated else DarkEmeraldSurface,
        borderColor = if (isJumatActive) ChampagneGold.copy(alpha = 0.6f) else HairlineBorder
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ChampagneGold.copy(alpha = 0.25f), MintGlow.copy(alpha = 0.15f))
                            )
                        )
                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mosque,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                val uiLang = LocalAppLanguage.current
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = uiText("জুমু'আ স্পেশাল হাব", "Jumu'ah Special Hub"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        if (isJumatActive) {
                            MintPillBadge(text = uiText("আজ জুমু'আ", "Today is Jumu'ah"), icon = Icons.Filled.CheckCircle)
                        }
                    }
                    Text(
                        text = uiText(
                            "সূরা আল-কাহাফ, দরূদ শরীফ ও বিশেষ সুন্নাহ আমল",
                            "Surah Al-Kahf, Salawat & Friday Sunnah deeds"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = ChampagneGold
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. SURAH AL-KAHF QUICK CARD
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldSurface)
                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
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
                                imageVector = Icons.Filled.AutoStories,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = uiText("সূরা আল-কাহাফ (Surah Al-Kahf)", "Surah Al-Kahf"),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        Text(
                            text = uiText("সূরা #১৮ • ১১০ আয়াত", "Surah #18 • 110 Ayahs"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = uiText(
                            "রাসূলুল্লাহ (ﷺ) বলেছেন: “যে ব্যক্তি জুমু'আর দিন সূরা আল-কাহাফ পাঠ করবে, তার জন্য এক জুমু'আ থেকে অপর জুমু'আ পর্যন্ত নূর আলোকিত থাকবে।” (বায়হাকী)",
                            "The Prophet (ﷺ) said: “Whoever recites Surah Al-Kahf on Friday, a light will shine for him between this Friday and the next.” (Bayhaqi)"
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onOpenSurahKahf,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold,
                            contentColor = ObsidianBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiText("সূরা আল-কাহাফ পড়ুন ও শুনুন", "Read & Listen to Surah Al-Kahf"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // 2. DAROOD / SALAWAT COUNTER WIDGET
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldSurface)
                        .border(0.5.dp, MintGlow.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val uiLang = LocalAppLanguage.current
                        Text(
                            text = uiText("জুমু'আ দরূদ শরীফ কাউন্টার", "Jumu'ah Salawat Counter"),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MintGlow,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = uiText(
                                "আজকের গণনা: ${formatLocalizedInt(salawatCount, uiLang)}",
                                "Today's count: ${formatLocalizedInt(salawatCount, uiLang)}"
                            ),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val contentLang = LocalContentLanguage.current
                    Text(
                        text = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = AmiriFontFamily,
                            color = ChampagneGold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val salawatTranslit = remember(contentLang) {
                        com.example.data.util.ArabicTransliterationHelper.transliterate(
                            arabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ",
                            langCode = contentLang
                        )
                    }
                    if (salawatTranslit.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = salawatTranslit,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MintGlow.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Big Tap-to-count button
                    Button(
                        onClick = {
                            onIncrementSalawat()
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkEmeraldElevated,
                            contentColor = MintGlow
                        ),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MintGlow.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uiText("দরূদ পাঠ গণনা করুন (+১)", "Count Salawat (+1)"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 3. SUNNAH CHECKLIST FOR JUMAT
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldSurface)
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = uiText("জুমু'আর দিনের বিশেষ সুন্নাতসমূহ:", "Special Sunnahs for Jumu'ah:"),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SunnahCheckItem(
                        uiText("১. জুমু'আর বিশেষ গোসল (গোসল করা)", "1. Take a special Friday Ghusl (bath)"),
                        ghuslChecked
                    ) { ghuslChecked = it }
                    SunnahCheckItem(
                        uiText("২. মেসওয়াক করা ও দাঁত পরিষ্কার করা", "2. Use Miswak and clean teeth"),
                        miswakChecked
                    ) { miswakChecked = it }
                    SunnahCheckItem(
                        uiText("৩. সুগন্ধি/আতর ব্যবহার ও উত্তম পোশাক পরা", "3. Apply fragrance/attar & wear clean clothes"),
                        cleanClothesChecked
                    ) { cleanClothesChecked = it }
                    SunnahCheckItem(
                        uiText("৪. খুতবার পূর্বেই আগেভাগে মসজিদে গমন", "4. Arrive at the mosque early before Khutbah"),
                        earlyMosqueChecked
                    ) { earlyMosqueChecked = it }
                    SunnahCheckItem(
                        uiText("৫. আসর থেকে মাগরিবের মধ্যে বিশেষ দু'আ করা", "5. Make special Du'a between Asr and Maghrib"),
                        duaHourChecked
                    ) { duaHourChecked = it }
                }
            }
        }
    }
}

@Composable
private fun SunnahCheckItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MintGlow,
                uncheckedColor = SlateGrayMuted,
                checkmarkColor = ObsidianBackground
            ),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (checked) MintGlow else TextPrimary,
                fontSize = 11.sp,
                fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}
