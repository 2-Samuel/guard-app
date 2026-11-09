package com.samuel.jarvisguard.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuel.jarvisguard.ui.components.CyberBorders
import com.samuel.jarvisguard.ui.components.FingerprintScanner
import com.samuel.jarvisguard.ui.components.ScannerState
import com.samuel.jarvisguard.ui.components.SecurityPanel
import com.samuel.jarvisguard.ui.theme.BrightScannerGreen
import com.samuel.jarvisguard.ui.theme.ErrorRed
import com.samuel.jarvisguard.ui.theme.InterFontFamily
import com.samuel.jarvisguard.ui.theme.MetallicSilver
import com.samuel.jarvisguard.ui.theme.OledBlack
import com.samuel.jarvisguard.ui.theme.OrbitronFontFamily
import com.samuel.jarvisguard.ui.theme.PrimaryEmerald
import com.samuel.jarvisguard.ui.theme.PureWhite
import com.samuel.jarvisguard.ui.theme.SecondaryText

@Composable
fun LockScreen(
    scannerState: ScannerState = ScannerState.SCANNING,
    statusHeading: String? = null,
    statusSubtitle: String? = null,
    onScannerClick: () -> Unit = {},
    onSecurityPanelClick: () -> Unit = {},
    onPinRequested: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val headingText = statusHeading ?: when (scannerState) {
        ScannerState.SUCCESS -> "IDENTITY VERIFIED"
        ScannerState.FAILED -> "ACCESS DENIED"
        else -> "IDENTITY VERIFICATION"
    }

    val subtitleText = statusSubtitle ?: when (scannerState) {
        ScannerState.SUCCESS -> "Opening WhatsApp..."
        ScannerState.FAILED -> "Authentication failed. Tap to retry or use PIN"
        else -> "Authenticate to access WhatsApp"
    }

    val headingColor = when (scannerState) {
        ScannerState.FAILED -> ErrorRed
        ScannerState.SUCCESS -> BrightScannerGreen
        else -> BrightScannerGreen
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        // Futuristic Cybernetic HUD Frame Lines
        CyberBorders(accentColor = if (scannerState == ScannerState.FAILED) ErrorRed else PrimaryEmerald)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // TOP BRANDING
            // ==========================================
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                // JARVIS GUARD Title
                Text(
                    text = "JARVIS GUARD",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    letterSpacing = 3.5.sp,
                    color = MetallicSilver
                )

                Spacer(modifier = Modifier.height(6.dp))

                // SECURITY PROTOCOL ACTIVE with futuristic angled flanking lines
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Left angled cyber line
                    Canvas(modifier = Modifier.width(44.dp).height(12.dp)) {
                        val w = drawContext.size.width
                        val h = drawContext.size.height
                        val path = Path().apply {
                            moveTo(0f, h * 0.7f)
                            lineTo(w * 0.75f, h * 0.7f)
                            lineTo(w, h * 0.2f)
                        }
                        drawPath(
                            path = path,
                            color = PrimaryEmerald.copy(alpha = 0.8f),
                            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "SECURITY PROTOCOL ACTIVE",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 2.2.sp,
                        color = PrimaryEmerald
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Right angled cyber line
                    Canvas(modifier = Modifier.width(44.dp).height(12.dp)) {
                        val w = drawContext.size.width
                        val h = drawContext.size.height
                        val path = Path().apply {
                            moveTo(0f, h * 0.2f)
                            lineTo(w * 0.25f, h * 0.7f)
                            lineTo(w, h * 0.7f)
                        }
                        drawPath(
                            path = path,
                            color = PrimaryEmerald.copy(alpha = 0.8f),
                            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }

            // ==========================================
            // CENTER SCANNER
            // ==========================================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                FingerprintScanner(
                    scannerState = scannerState,
                    onClick = onScannerClick
                )
            }

            // ==========================================
            // MAIN HEADING & SUPPORTING TEXT
            // ==========================================
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = headingText,
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    letterSpacing = 1.8.sp,
                    color = headingColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitleText,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = PureWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // SECURITY INFORMATION PANEL
                // ==========================================
                SecurityPanel(
                    title = "Using your fingerprint",
                    subtitle = "Your privacy. Our priority.",
                    onClick = {
                        onSecurityPanelClick()
                    }
                )

                Spacer(modifier = Modifier.height(26.dp))

                // ==========================================
                // BOTTOM BRANDING
                // ==========================================
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    // Left accent line
                    Canvas(modifier = Modifier.width(32.dp).height(2.dp)) {
                        val w = drawContext.size.width
                        val h = drawContext.size.height
                        drawLine(
                            color = SecondaryText.copy(alpha = 0.45f),
                            start = Offset(0f, h / 2f),
                            end = Offset(w, h / 2f),
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "JARVIS GUARD",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        letterSpacing = 3.sp,
                        color = SecondaryText.copy(alpha = 0.65f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right accent line
                    Canvas(modifier = Modifier.width(32.dp).height(2.dp)) {
                        val w = drawContext.size.width
                        val h = drawContext.size.height
                        drawLine(
                            color = SecondaryText.copy(alpha = 0.45f),
                            start = Offset(0f, h / 2f),
                            end = Offset(w, h / 2f),
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }
                }
            }
        }
    }
}

