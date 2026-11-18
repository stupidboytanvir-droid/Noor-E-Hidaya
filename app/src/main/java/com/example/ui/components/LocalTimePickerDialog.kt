package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.util.TimeFormatUtils
import com.example.ui.theme.*

/**
 * Polished Material 3 Local Time Picker Dialog that strictly honors the device's local timezone
 * and 12h/24h format, and provides an explicit "OK • সংরক্ষণ" confirmation button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalTimePickerDialog(
    title: String,
    subtitle: String? = null,
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour24: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val is24Hour = remember(context) { TimeFormatUtils.is24HourFormat(context) }
    val localTzLabel = remember { TimeFormatUtils.getLocalTimeZoneDisplay() }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour.coerceIn(0, 23),
        initialMinute = initialMinute.coerceIn(0, 59),
        is24Hour = is24Hour
    )
    var useKeyboardInput by remember { mutableStateOf(false) }

    val previewFormatted = remember(timePickerState.hour, timePickerState.minute, is24Hour) {
        TimeFormatUtils.formatHourMinute(timePickerState.hour, timePickerState.minute, is24Hour)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 420.dp)
                .border(0.8.dp, ChampagneGold.copy(alpha = 0.45f), RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = DarkEmeraldElevated,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SlateGrayMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = { useKeyboardInput = !useKeyboardInput },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkEmeraldSurface)
                            .testTag("time_picker_mode_toggle")
                    ) {
                        Icon(
                            imageVector = if (useKeyboardInput) Icons.Filled.Schedule else Icons.Filled.Keyboard,
                            contentDescription = if (useKeyboardInput) uiText("ঘড়ি ভিউ", "Clock View") else uiText("কীবোর্ড ইনপুট", "Keyboard Input"),
                            tint = ChampagneGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Local Time Preview Pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkEmeraldSurface)
                        .border(0.5.dp, HairlineBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = MintGlow,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = localTzLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SlateGrayMuted,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Text(
                        text = previewFormatted,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MintGlow,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                // M3 TimePicker or TimeInput
                val pickerColors = TimePickerDefaults.colors(
                    clockDialColor = DarkEmeraldSurface,
                    clockDialSelectedContentColor = ObsidianBackground,
                    clockDialUnselectedContentColor = TextPrimary,
                    selectorColor = ChampagneGold,
                    containerColor = DarkEmeraldElevated,
                    periodSelectorBorderColor = ChampagneGold.copy(alpha = 0.5f),
                    periodSelectorSelectedContainerColor = ChampagneGold,
                    periodSelectorUnselectedContainerColor = DarkEmeraldSurface,
                    periodSelectorSelectedContentColor = ObsidianBackground,
                    periodSelectorUnselectedContentColor = TextPrimary,
                    timeSelectorSelectedContainerColor = ChampagneGold.copy(alpha = 0.22f),
                    timeSelectorUnselectedContainerColor = DarkEmeraldSurface,
                    timeSelectorSelectedContentColor = ChampagneGold,
                    timeSelectorUnselectedContentColor = TextPrimary
                )

                if (useKeyboardInput) {
                    TimeInput(
                        state = timePickerState,
                        colors = pickerColors
                    )
                } else {
                    TimePicker(
                        state = timePickerState,
                        colors = pickerColors
                    )
                }

                // Explicit Cancel & OK/Save Confirmation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("time_picker_cancel_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, HairlineBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateGrayMuted)
                    ) {
                        Text(uiText("বাতিল", "Cancel"), fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            onConfirm(timePickerState.hour, timePickerState.minute)
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("time_picker_ok_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold,
                            contentColor = ObsidianBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(uiText("OK • সংরক্ষণ করুন", "OK • Save"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
