package com.samuel.jarvisguard.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuel.jarvisguard.data.SecurityEvent
import com.samuel.jarvisguard.data.SecurityRepository
import com.samuel.jarvisguard.security.BiometricAuthManager
import com.samuel.jarvisguard.ui.auth.PinScreen
import com.samuel.jarvisguard.ui.components.CyberBorders
import com.samuel.jarvisguard.ui.components.FingerprintScanner
import com.samuel.jarvisguard.ui.components.ScannerState
import com.samuel.jarvisguard.ui.theme.BrightScannerGreen
import com.samuel.jarvisguard.ui.theme.CardBackground
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
fun SetupScreen(
    repository: SecurityRepository,
    onSetupCompleted: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) }

    // PIN configuration state
    var firstPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var isConfirmingPin by remember { mutableStateOf(false) }

    // Permission state
    var isAccessibilityGranted by remember {
        mutableStateOf(AccessibilityUtils.isServiceEnabled(context))
    }

    // Polling accessibility state when on step 4
    LaunchedEffect(currentStep) {
        if (currentStep == 4) {
            while (!isAccessibilityGranted) {
                isAccessibilityGranted = AccessibilityUtils.isServiceEnabled(context)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        CyberBorders()

        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "SetupSteps"
        ) { step ->
            when (step) {
                // ==========================================
                // SCREEN 1: WELCOME
                // ==========================================
                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 40.dp)
                        ) {
                            Text(
                                text = "JARVIS GUARD",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                letterSpacing = 3.sp,
                                color = MetallicSilver
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Your privacy. Your identity. Your control.",
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                color = SecondaryText,
                                textAlign = TextAlign.Center
                            )
                        }

                        FingerprintScanner(
                            scannerState = ScannerState.SCANNING
                        )

                        Button(
                            onClick = { currentStep = 2 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryEmerald,
                                contentColor = OledBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(
                                text = "INITIALIZE SYSTEM",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                // ==========================================
                // SCREEN 2: CONFIGURE SECURITY PIN
                // ==========================================
                2 -> {
                    if (!isConfirmingPin) {
                        PinScreen(
                            title = "CONFIGURE SECURITY PIN",
                            subtitle = "Register a 6-digit backup PIN",
                            errorMessage = pinError,
                            onPinComplete = { entered ->
                                firstPin = entered
                                pinError = null
                                isConfirmingPin = true
                            }
                        )
                    } else {
                        PinScreen(
                            title = "CONFIRM SECURITY PIN",
                            subtitle = "Re-enter the 6-digit backup PIN",
                            errorMessage = pinError,
                            onPinComplete = { confirmEntered ->
                                if (confirmEntered == firstPin) {
                                    repository.pinSecurityManager.savePin(confirmEntered)
                                    pinError = null
                                    currentStep = 3
                                } else {
                                    pinError = "PINs do not match. Try again."
                                    isConfirmingPin = false
                                    firstPin = ""
                                }
                            }
                        )
                    }
                }

                // ==========================================
                // SCREEN 3: BIOMETRIC CONFIGURATION
                // ==========================================
                3 -> {
                    val biometricStatus = repository.biometricAuthManager.checkBiometricStatus()
                    val isAvailable = biometricStatus == BiometricAuthManager.BiometricStatus.AVAILABLE

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 36.dp)
                        ) {
                            Text(
                                text = "BIOMETRIC CONFIGURATION",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 1.5.sp,
                                color = PrimaryEmerald,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "JARVIS GUARD integrates directly with your Android phone's biometric hardware.",
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                color = PureWhite,
                                textAlign = TextAlign.Center
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardBackground)
                                .border(1.2.dp, PrimaryEmerald.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = if (isAvailable) PrimaryEmerald else SecondaryText,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isAvailable) "BIOMETRICS READY" else "FINGERPRINT NOT ENROLLED",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isAvailable) PrimaryEmerald else SecondaryText
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isAvailable) {
                                        "Phone sensor detected and ready. No biometric templates are ever exported or saved outside your device's Secure Enclave."
                                    } else {
                                        "No enrolled fingerprint found. You can still use your 6-digit backup PIN."
                                    },
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = SecondaryText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Button(
                            onClick = {
                                repository.preferences.isBiometricEnabled = isAvailable
                                currentStep = 4
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryEmerald,
                                contentColor = OledBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(
                                text = "PROCEED TO ACCESSIBILITY",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // ==========================================
                // SCREEN 4: ACCESSIBILITY CONFIGURATION
                // ==========================================
                4 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 36.dp)
                        ) {
                            Text(
                                text = "ACCESSIBILITY CONFIGURATION",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                letterSpacing = 1.2.sp,
                                color = PrimaryEmerald,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Foreground Application Monitoring",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = PureWhite
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardBackground)
                                .border(1.2.dp, PrimaryEmerald.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = if (isAccessibilityGranted) Icons.Default.CheckCircle else Icons.Default.AccessibilityNew,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isAccessibilityGranted) "PERMISSION GRANTED" else "PERMISSION REQUIRED",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = PrimaryEmerald
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "To detect when WhatsApp opens and display the security gate before access, Android requires the Accessibility Service to be enabled.\n\nJARVIS GUARD will never read your chats, contacts, or screen contents.",
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = SecondaryText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (!isAccessibilityGranted) {
                                Button(
                                    onClick = {
                                        AccessibilityUtils.openAccessibilitySettings(context)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryEmerald,
                                        contentColor = OledBlack
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text(
                                        text = "GRANT ACCESSIBILITY",
                                        fontFamily = OrbitronFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            OutlinedButton(
                                onClick = { currentStep = 5 },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = if (isAccessibilityGranted) "CONTINUE" else "CONTINUE ANYWAY",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = PrimaryEmerald
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // SCREEN 5: WHATSAPP DETECTION
                // ==========================================
                5 -> {
                    val isWhatsAppInstalled = PackageUtils.isWhatsAppInstalled(context)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 36.dp)
                        ) {
                            Text(
                                text = "WHATSAPP DETECTION",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 1.5.sp,
                                color = PrimaryEmerald
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Target: com.whatsapp",
                                fontFamily = OrbitronFontFamily,
                                fontSize = 13.sp,
                                color = SecondaryText
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardBackground)
                                .border(1.2.dp, PrimaryEmerald, RoundedCornerShape(16.dp))
                                .padding(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = PrimaryEmerald,
                                    modifier = Modifier.size(52.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isWhatsAppInstalled) "WHATSAPP DETECTED" else "TARGET NOT INSTALLED",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = PrimaryEmerald
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isWhatsAppInstalled) "PROTECTION READY" else "WhatsApp package not found. JARVIS GUARD will activate as soon as it is installed.",
                                    fontFamily = OrbitronFontFamily,
                                    fontSize = 14.sp,
                                    color = PureWhite
                                )
                            }
                        }

                        Button(
                            onClick = { currentStep = 6 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryEmerald,
                                contentColor = OledBlack
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text(
                                text = "PROCEED TO ACTIVATION",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // ==========================================
                // SCREEN 6: ACTIVATE SECURITY
                // ==========================================
                6 -> {
                    var protectionEnabled by remember { mutableStateOf(true) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 36.dp)
                        ) {
                            Text(
                                text = "ACTIVATE SECURITY",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 1.5.sp,
                                color = PrimaryEmerald
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Enable biometric gate for WhatsApp",
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                color = SecondaryText
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CardBackground)
                                .border(1.2.dp, PrimaryEmerald, RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "WhatsApp Protection",
                                        fontFamily = OrbitronFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = PureWhite
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Require fingerprint or PIN before opening WhatsApp",
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        color = SecondaryText
                                    )
                                }
                                Switch(
                                    checked = protectionEnabled,
                                    onCheckedChange = { protectionEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = OledBlack,
                                        checkedTrackColor = PrimaryEmerald
                                    )
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "JARVIS SECURITY INITIALIZED",
                                fontFamily = OrbitronFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 2.sp,
                                color = BrightScannerGreen
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    repository.preferences.isProtectionEnabled = protectionEnabled
                                    repository.preferences.isSetupComplete = true
                                    repository.logEvent(
                                        SecurityEvent.Type.PROTECTION_ENABLED,
                                        "JARVIS GUARD initialized and armed."
                                    )
                                    onSetupCompleted()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryEmerald,
                                    contentColor = OledBlack
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                            ) {
                                Text(
                                    text = "ENTER COMMAND CENTER",
                                    fontFamily = OrbitronFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

