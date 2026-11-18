package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.audio.PlaybackState
import com.example.ui.theme.*

/**
 * Persistent glassmorphic audio mini-player bar shown across tabs when Quran, Du'a, Ruqyah,
 * or Arabic pronunciation audio is active in the background Media3 session.
 */
@Composable
fun GlobalMiniPlayerBar(
    playbackState: PlaybackState,
    visible: Boolean,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible && playbackState.isActive,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 4.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(DarkEmeraldElevated.copy(alpha = 0.96f))
                .border(0.75.dp, ChampagneGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                .testTag("global_mini_player_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ChampagneGold.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (playbackState.isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = ChampagneGold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.GraphicEq,
                            contentDescription = null,
                            tint = if (playbackState.isPlaying) MintGlow else ChampagneGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = playbackState.currentTitle,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val subtitleText = playbackState.error?.takeIf { it.isNotBlank() }
                        ?: playbackState.currentSubtitle.ifBlank {
                            if (playbackState.isBuffering) uiText("লোড হচ্ছে...", "Buffering...")
                            else if (playbackState.isPlaying) uiText("চলছে", "Playing")
                            else uiText("বিরতি", "Paused")
                        }
                    Text(
                        text = subtitleText,
                        color = if (playbackState.error != null) Color(0xFFFF8A80) else SlateGrayMuted,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (playbackState.playlistSize > 1) {
                    IconButton(
                        onClick = onSkipPrevious,
                        enabled = playbackState.hasPrevious,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("mini_player_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = uiText("পূর্ববর্তী", "Previous"),
                            tint = if (playbackState.hasPrevious) ChampagneGold else SlateGrayMuted.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mini_player_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) uiText("বিরতি", "Pause") else uiText("চালান", "Play"),
                        tint = MintGlow,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (playbackState.playlistSize > 1) {
                    IconButton(
                        onClick = onSkipNext,
                        enabled = playbackState.hasNext,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("mini_player_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = uiText("পরবর্তী", "Next"),
                            tint = if (playbackState.hasNext) ChampagneGold else SlateGrayMuted.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onStop,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("mini_player_stop_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = uiText("বন্ধ করুন", "Close player"),
                        tint = SlateGrayMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (playbackState.durationMs > 0) {
                val progress = (playbackState.currentPositionMs.toFloat() / playbackState.durationMs.toFloat())
                    .coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = MintGlow,
                    trackColor = HairlineBorder.copy(alpha = 0.4f)
                )
            } else if (playbackState.isBuffering) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = ChampagneGold,
                    trackColor = HairlineBorder.copy(alpha = 0.4f)
                )
            }
        }
    }
}
