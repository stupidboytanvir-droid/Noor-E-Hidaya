package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.audio.OfflineAudioManager
import com.example.ui.theme.*

@Composable
fun SmartOfflineAudioManagerCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiLang = LocalAppLanguage.current
    var storageInfo by remember {
        mutableStateOf(OfflineAudioManager.getStorageInfo(context))
    }
    var showConfirmDialog by remember { mutableStateOf(false) }

    FrostedGlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = DarkEmeraldElevated,
        borderColor = HairlineBorder
    ) {
        // Header
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
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkEmeraldSurface)
                        .border(0.5.dp, ChampagneGold.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudDownload,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = uiText("স্মার্ট অফলাইন অডিও ম্যানেজার", "Smart Offline Audio Manager"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = uiText("ক্যাশ ও ডাউনলোড করা সূরা এবং অডিও পরিচালনা", "Manage cached & downloaded Surahs and audio"),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateGrayMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MintGlow.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = storageInfo.formattedAudioCacheSize,
                    color = MintGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Storage Distribution Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkEmeraldSurface)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = uiText("অ্যাপ অডিও ক্যাশ ব্যবহার:", "App Audio Cache Used:"),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                )
                Text(
                    text = uiText("${storageInfo.formattedAudioCacheSize} ব্যবহৃত", "${storageInfo.formattedAudioCacheSize} used"),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = ChampagneGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = uiText("ডিভাইসের ফাঁকা স্থান:", "Device Free Space:"),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 11.sp)
                )
                Text(
                    text = uiText("${storageInfo.formattedDeviceAvailableSpace} উপলব্ধ", "${storageInfo.formattedDeviceAvailableSpace} free"),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MintGlow,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Breakdown items
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StorageCategoryRow(
                title = uiText("কুরআন ক্বারী তিলাওয়াত ক্যাশ", "Quran Qari Recitation Cache"),
                count = uiText("${formatLocalizedInt(storageInfo.quranCacheCount, uiLang)}টি ফাইল", "${storageInfo.quranCacheCount} files"),
                icon = Icons.Filled.Headphones
            )
            StorageCategoryRow(
                title = uiText("রুকইয়াহ শারঈয়াহ অডিও ক্যাশ", "Ruqyah Shariah Audio Cache"),
                count = uiText("${formatLocalizedInt(storageInfo.ruqyahCacheCount, uiLang)}টি ফাইল", "${storageInfo.ruqyahCacheCount} files"),
                icon = Icons.Filled.Shield
            )
            StorageCategoryRow(
                title = uiText("হিসনুল মুসলিম ও দু'আ অডিও", "Hisnul Muslim & Du'a Audio"),
                count = uiText("${formatLocalizedInt(storageInfo.azkarCacheCount, uiLang)}টি ফাইল", "${storageInfo.azkarCacheCount} files"),
                icon = Icons.AutoMirrored.Filled.VolumeUp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Button: Clear Cache / Delete Downloads
        Button(
            onClick = { showConfirmDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2C1114),
                contentColor = Color(0xFFFFB4BA)
            ),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF5A1E24)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.DeleteSweep,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = uiText("ক্যাশ ও ডাউনলোড মুছে ফেলুন (স্পেস খালি করুন)", "Clear Cache & Downloads (Free Up Space)"),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showConfirmDialog) {
        val clearedMsg = uiText("অডিও ক্যাশ সফলভাবে খালি করা হয়েছে", "Audio cache cleared successfully")
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = DarkEmeraldElevated,
            titleContentColor = ChampagneGold,
            textContentColor = TextPrimary,
            title = {
                Text(
                    text = uiText("অডিও ক্যাশ খালি করবেন?", "Clear Audio Cache?"),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = uiText(
                        "এটি আপনার ডিভাইসে সংরক্ষিত সমস্ত অফলাইন অডিও ক্যাশ মুছে দেবে এবং স্টোরেজ খালি করবে। পরবর্তী প্লেব্যাকে পুনরায় অডিও স্ট্রিম হবে।",
                        "This will remove all offline audio cache saved on your device and free up storage. Audio will re-stream on next playback."
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(color = SlateGrayMuted, fontSize = 12.sp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        OfflineAudioManager.clearAllAudioCache(context)
                        storageInfo = OfflineAudioManager.getStorageInfo(context).copy(
                            totalAudioCacheBytes = 0L,
                            formattedAudioCacheSize = "0 MB",
                            quranCacheCount = 0,
                            ruqyahCacheCount = 0,
                            azkarCacheCount = 0
                        )
                        Toast.makeText(context, clearedMsg, Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB3261E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(uiText("মুছে ফেলুন", "Clear"), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = SlateGrayMuted)
                ) {
                    Text(uiText("বাতিল", "Cancel"))
                }
            }
        )
    }
}

@Composable
private fun StorageCategoryRow(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkEmeraldSurface)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MintGlow,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, fontSize = 11.sp)
            )
        }
        Text(
            text = count,
            style = MaterialTheme.typography.labelSmall.copy(color = SlateGrayMuted, fontSize = 10.sp)
        )
    }
}
