package com.samuel.jarvisguard.ui.dashboard

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
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuel.jarvisguard.data.SecurityEvent
import com.samuel.jarvisguard.data.SecurityRepository
import com.samuel.jarvisguard.ui.components.CyberBorders
import com.samuel.jarvisguard.ui.theme.BrightScannerGreen
import com.samuel.jarvisguard.ui.theme.CardBackground
import com.samuel.jarvisguard.ui.theme.DeepGreen
import com.samuel.jarvisguard.ui.theme.ErrorRed
import com.samuel.jarvisguard.ui.theme.InterFontFamily
import com.samuel.jarvisguard.ui.theme.MetallicSilver
import com.samuel.jarvisguard.ui.theme.OledBlack
import com.samuel.jarvisguard.ui.theme.OrbitronFontFamily
import com.samuel.jarvisguard.ui.theme.PrimaryEmerald
import com.samuel.jarvisguard.ui.theme.PureWhite
import com.samuel.jarvisguard.ui.theme.SecondaryText
import com.samuel.jarvisguard.utils.AccessibilityUtils
import com.samuel.jarvisguard.utils.PackageUtils

@Composable
fun DashboardScreen(
    repository: SecurityRepository,
    onNavigateToSettings: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onTestLockScreen: () -> Unit
) {
    val context = LocalContext.current
    var isProtectionActive by remember { mutableStateOf(repository.preferences.isProtectionEnabled) }
    val isAccessibilityActive = AccessibilityUtils.isServiceEnabled(context)
    val isWhatsAppInstalled = PackageUtils.isWhatsAppInstalled(context)

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "JARVIS GUARD",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        letterSpacing = 2.5.sp,
                        color = MetallicSilver
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "SECURITY COMMAND CENTER",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                        color = PrimaryEmerald
                    )
                }

                Row {
                    IconButton(onClick = onNavigateToActivity) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Logs",
                            tint = PrimaryEmerald
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = PrimaryEmerald
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // PROTECTION STATUS Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBackground)
                        .border(
                            1.4.dp,
                            if (isProtectionActive) PrimaryEmerald else ErrorRed.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (isProtectionActive) PrimaryEmerald else ErrorRed,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "PROTECTION STATUS",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp,
                                    color = SecondaryText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isProtectionActive) "ACTIVE" else "INACTIVE",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = if (isProtectionActive) BrightScannerGreen else ErrorRed
                                )
                            }
                        }

                        Switch(
                            checked = isProtectionActive,
                            onCheckedChange = { checked ->
                                isProtectionActive = checked
                                repository.preferences.isProtectionEnabled = checked
                                repository.logEvent(
                                    if (checked) SecurityEvent.Type.PROTECTION_ENABLED else SecurityEvent.Type.PROTECTION_DISABLED,
                                    if (checked) "Protection enabled" else "Protection disabled"
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = OledBlack,
                                checkedTrackColor = PrimaryEmerald
                            )
                        )
                    }
                }

                // Grid of 4 Status Cards (2x2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Protected Application
                    DashboardMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Security,
                        title = "PROTECTED APP",
                        value = "WhatsApp",
                        statusText = if (isWhatsAppInstalled) "Detected" else "Not installed",
                        statusColor = if (isWhatsAppInstalled) PrimaryEmerald else ErrorRed
                    )

                    // Authentication
                    DashboardMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Fingerprint,
                        title = "AUTHENTICATION",
                        value = "Biometric",
                        statusText = if (repository.preferences.isBiometricEnabled) "Enabled" else "Disabled",
                        statusColor = if (repository.preferences.isBiometricEnabled) PrimaryEmerald else SecondaryText
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Backup Security
                    DashboardMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Lock,
                        title = "BACKUP SECURITY",
                        value = "PIN",
                        statusText = if (repository.preferences.hasPin()) "Configured" else "Not Set",
                        statusColor = PrimaryEmerald
                    )

                    // System Status
                    DashboardMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccessibilityNew,
                        title = "SYSTEM STATUS",
                        value = if (isAccessibilityActive) "Monitoring" else "Alert",
                        statusText = if (isAccessibilityActive) "Active" else "Permission Req.",
                        statusColor = if (isAccessibilityActive) PrimaryEmerald else ErrorRed
                    )
                }

                if (!isAccessibilityActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBackground)
                            .border(1.dp, ErrorRed, RoundedCornerShape(12.dp))
                            .clickable {
                                AccessibilityUtils.openAccessibilitySettings(context)
                            }
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Accessibility Permission Required",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ErrorRed
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tap here to enable JARVIS GUARD monitoring in Android Settings",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = ErrorRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Test Security Gate Action Button
                Button(
                    onClick = onTestLockScreen,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryEmerald,
                        contentColor = OledBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "TEST SECURITY GATE",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    statusText: String,
    statusColor: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(1.dp, PrimaryEmerald.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryEmerald,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = SecondaryText
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusText,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = statusColor
            )
        }
    }
}

