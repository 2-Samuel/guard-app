package com.samuel.jarvisguard.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuel.jarvisguard.data.SecurityEvent
import com.samuel.jarvisguard.data.SecurityRepository
import com.samuel.jarvisguard.ui.components.CyberBorders
import com.samuel.jarvisguard.ui.theme.CardBackground
import com.samuel.jarvisguard.ui.theme.ErrorRed
import com.samuel.jarvisguard.ui.theme.InterFontFamily
import com.samuel.jarvisguard.ui.theme.MetallicSilver
import com.samuel.jarvisguard.ui.theme.OledBlack
import com.samuel.jarvisguard.ui.theme.OrbitronFontFamily
import com.samuel.jarvisguard.ui.theme.PrimaryEmerald
import com.samuel.jarvisguard.ui.theme.PureWhite
import com.samuel.jarvisguard.ui.theme.SecondaryText
import com.samuel.jarvisguard.utils.AccessibilityUtils

@Composable
fun SettingsScreen(
    repository: SecurityRepository,
    onBack: () -> Unit,
    onChangePin: () -> Unit
) {
    val context = LocalContext.current
    var isProtectionEnabled by remember { mutableStateOf(repository.preferences.isProtectionEnabled) }
    var isBiometricEnabled by remember { mutableStateOf(repository.preferences.isBiometricEnabled) }
    var relockGraceMs by remember { mutableLongStateOf(repository.preferences.relockGraceMs) }
    var showRelockDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        CyberBorders()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryEmerald
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "JARVIS GUARD",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 2.sp,
                        color = MetallicSilver
                    )
                    Text(
                        text = "SYSTEM SETTINGS",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = PrimaryEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section: Protection Controls
                Text(
                    text = "SECURITY CONTROLS",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    color = PrimaryEmerald,
                    modifier = Modifier.padding(start = 4.dp)
                )

                SettingsToggleCard(
                    icon = Icons.Default.Security,
                    title = "WhatsApp Protection",
                    subtitle = if (isProtectionEnabled) "Biometric gate active" else "Protection disabled",
                    checked = isProtectionEnabled,
                    onCheckedChange = { checked ->
                        isProtectionEnabled = checked
                        repository.preferences.isProtectionEnabled = checked
                        repository.logEvent(
                            if (checked) SecurityEvent.Type.PROTECTION_ENABLED else SecurityEvent.Type.PROTECTION_DISABLED,
                            if (checked) "Protection enabled by user" else "Protection disabled by user"
                        )
                    }
                )

                SettingsToggleCard(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Authentication",
                    subtitle = if (isBiometricEnabled) "Sensor verification enabled" else "PIN only verification",
                    checked = isBiometricEnabled,
                    onCheckedChange = { checked ->
                        isBiometricEnabled = checked
                        repository.preferences.isBiometricEnabled = checked
                    }
                )

                // Section: Access & Relock
                Text(
                    text = "ACCESS & RELOCK",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    color = PrimaryEmerald,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                val graceLabel = when (relockGraceMs) {
                    0L -> "Immediately on exit"
                    30_000L -> "30 seconds grace"
                    60_000L -> "1 minute grace"
                    300_000L -> "5 minutes grace"
                    else -> "Immediately on exit"
                }

                SettingsActionCard(
                    icon = Icons.Default.Timer,
                    title = "Relock Grace Period",
                    subtitle = graceLabel,
                    onClick = { showRelockDialog = true }
                )

                SettingsActionCard(
                    icon = Icons.Default.Lock,
                    title = "Change Backup PIN",
                    subtitle = "Update your 6-digit backup security code",
                    onClick = onChangePin
                )

                SettingsActionCard(
                    icon = Icons.Default.AccessibilityNew,
                    title = "Accessibility Permission",
                    subtitle = if (AccessibilityUtils.isServiceEnabled(context)) "Monitoring active" else "Permission required",
                    isWarning = !AccessibilityUtils.isServiceEnabled(context),
                    onClick = { AccessibilityUtils.openAccessibilitySettings(context) }
                )

                // Section: Information
                Text(
                    text = "APPLICATION",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    color = PrimaryEmerald,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                SettingsActionCard(
                    icon = Icons.Default.Info,
                    title = "About & Privacy",
                    subtitle = "Version 1.0.0 • Local Keystore Security",
                    onClick = { showAboutDialog = true }
                )
            }
        }

        // Relock Grace Period Dialog
        if (showRelockDialog) {
            AlertDialog(
                onDismissRequest = { showRelockDialog = false },
                containerColor = CardBackground,
                titleContentColor = PrimaryEmerald,
                textContentColor = PureWhite,
                title = {
                    Text(
                        text = "RELOCK TIMING",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column {
                        val options = listOf(
                            Pair(0L, "Immediately (Recommended)"),
                            Pair(30_000L, "30 Seconds"),
                            Pair(60_000L, "1 Minute"),
                            Pair(300_000L, "5 Minutes")
                        )
                        options.forEach { (ms, label) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        relockGraceMs = ms
                                        repository.preferences.relockGraceMs = ms
                                        showRelockDialog = false
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                RadioButton(
                                    selected = relockGraceMs == ms,
                                    onClick = {
                                        relockGraceMs = ms
                                        repository.preferences.relockGraceMs = ms
                                        showRelockDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PrimaryEmerald,
                                        unselectedColor = SecondaryText
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = label,
                                    fontFamily = InterFontFamily,
                                    fontSize = 14.sp,
                                    color = PureWhite
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showRelockDialog = false }) {
                        Text(text = "CLOSE", color = PrimaryEmerald)
                    }
                }
            )
        }

        // About Dialog
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                containerColor = CardBackground,
                titleContentColor = PrimaryEmerald,
                textContentColor = PureWhite,
                title = {
                    Text(
                        text = "JARVIS GUARD",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Text(
                        text = "JARVIS GUARD operates entirely locally on your device.\n\n" +
                            "• Zero cloud servers or tracking.\n" +
                            "• Fingerprints are processed solely inside Android's Hardware-backed Keystore.\n" +
                            "• Backup PINs are encrypted using AES-256-GCM.\n" +
                            "• No WhatsApp messages or contacts are accessed.",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = SecondaryText
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text(text = "OK", color = PrimaryEmerald)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsToggleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(1.dp, PrimaryEmerald.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryEmerald,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OledBlack,
                    checkedTrackColor = PrimaryEmerald
                )
            )
        }
    }
}

@Composable
fun SettingsActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isWarning: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(
                1.dp,
                if (isWarning) ErrorRed.copy(alpha = 0.5f) else PrimaryEmerald.copy(alpha = 0.4f),
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isWarning) ErrorRed else PrimaryEmerald,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = if (isWarning) ErrorRed else SecondaryText
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

