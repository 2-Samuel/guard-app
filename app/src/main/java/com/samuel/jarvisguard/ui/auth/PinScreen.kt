package com.samuel.jarvisguard.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuel.jarvisguard.ui.components.CyberBorders
import com.samuel.jarvisguard.ui.theme.CardBackground
import com.samuel.jarvisguard.ui.theme.DeepGreen
import com.samuel.jarvisguard.ui.theme.ErrorRed
import com.samuel.jarvisguard.ui.theme.InterFontFamily
import com.samuel.jarvisguard.ui.theme.OledBlack
import com.samuel.jarvisguard.ui.theme.OrbitronFontFamily
import com.samuel.jarvisguard.ui.theme.PrimaryEmerald
import com.samuel.jarvisguard.ui.theme.PureWhite
import com.samuel.jarvisguard.ui.theme.SecondaryText
import kotlinx.coroutines.delay

@Composable
fun PinScreen(
    title: String = "SECURITY PIN",
    subtitle: String = "Enter 6-digit backup code",
    errorMessage: String? = null,
    lockoutSeconds: Int = 0,
    onPinComplete: (String) -> Unit,
    onSwitchToBiometric: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null
) {
    var pin by remember { mutableStateOf("") }
    var currentError by remember { mutableStateOf(errorMessage) }

    LaunchedEffect(errorMessage) {
        currentError = errorMessage
    }

    LaunchedEffect(pin) {
        if (pin.length == 6) {
            onPinComplete(pin)
            // Reset pin input after brief delay if still on screen
            delay(500)
            pin = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
    ) {
        CyberBorders(accentColor = if (currentError != null) ErrorRed else PrimaryEmerald)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Text(
                    text = "JARVIS GUARD",
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 3.sp,
                    color = PureWhite
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    letterSpacing = 1.5.sp,
                    color = PrimaryEmerald
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (lockoutSeconds > 0) {
                        "Locked due to failed attempts ($lockoutSeconds s)"
                    } else {
                        currentError ?: subtitle
                    },
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    color = if (currentError != null || lockoutSeconds > 0) ErrorRed else SecondaryText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6 Circular PIN Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 6) {
                        val isFilled = i < pin.length
                        val dotColor = if (currentError != null) ErrorRed else PrimaryEmerald

                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) dotColor else Color.Transparent)
                                .border(
                                    width = 1.8.dp,
                                    color = if (isFilled) dotColor else dotColor.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }

            // Numeric Keypad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("CLEAR", "0", "DEL")
                )

                for (row in keys) {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (key in row) {
                            KeypadButton(
                                text = key,
                                enabled = lockoutSeconds <= 0,
                                onClick = {
                                    currentError = null
                                    when (key) {
                                        "CLEAR" -> pin = ""
                                        "DEL" -> if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                        else -> if (pin.length < 6) pin += key
                                    }
                                }
                            )
                        }
                    }
                }

                // Switch to Fingerprint (if available)
                if (onSwitchToBiometric != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onSwitchToBiometric)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Use Fingerprint",
                            tint = PrimaryEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "USE FINGERPRINT SCANNER",
                            fontFamily = OrbitronFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = PrimaryEmerald
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(CardBackground)
            .border(
                width = 1.2.dp,
                color = PrimaryEmerald.copy(alpha = if (enabled) 0.5f else 0.15f),
                shape = CircleShape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = rememberRipple(color = PrimaryEmerald),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        when (text) {
            "DEL" -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = if (enabled) PureWhite else SecondaryText,
                    modifier = Modifier.size(22.dp)
                )
            }
            "CLEAR" -> {
                Text(
                    text = "CLR",
                    fontFamily = OrbitronFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) SecondaryText else SecondaryText.copy(alpha = 0.3f),
                    letterSpacing = 1.sp
                )
            }
            else -> {
                Text(
                    text = text,
                    fontFamily = OrbitronFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) PureWhite else SecondaryText.copy(alpha = 0.3f)
                )
            }
        }
    }
}

