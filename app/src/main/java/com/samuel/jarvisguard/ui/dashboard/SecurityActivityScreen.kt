package com.samuel.jarvisguard.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.samuel.jarvisguard.ui.theme.SecondaryEmerald
import com.samuel.jarvisguard.ui.theme.SecondaryText

@Composable
fun SecurityActivityScreen(
    repository: SecurityRepository,
    onBack: () -> Unit
) {
    val events by repository.eventsFlow.collectAsState()

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
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryEmerald
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "JARVIS GUARD",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 2.sp,
                        color = MetallicSilver
                    )
                    Text(
                        text = "SECURITY ACTIVITY LOG",
                        fontFamily = OrbitronFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = PrimaryEmerald
                    )
                }

                IconButton(onClick = { repository.clearEvents() }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Logs",
                        tint = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO SECURITY EVENTS RECORDED",
                        fontFamily = OrbitronFontFamily,
                        fontSize = 13.sp,
                        letterSpacing = 1.5.sp,
                        color = SecondaryText
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(events) { event ->
                        SecurityEventItem(event)
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityEventItem(event: SecurityEvent) {
    val (icon: ImageVector, iconColor) = when (event.type) {
        SecurityEvent.Type.AUTH_SUCCESS, SecurityEvent.Type.PIN_SUCCESS ->
            Pair(Icons.Default.CheckCircle, PrimaryEmerald)
        SecurityEvent.Type.AUTH_FAILURE, SecurityEvent.Type.PIN_FAILURE ->
            Pair(Icons.Default.Error, ErrorRed)
        SecurityEvent.Type.PROTECTION_ENABLED ->
            Pair(Icons.Default.Security, PrimaryEmerald)
        SecurityEvent.Type.PROTECTION_DISABLED ->
            Pair(Icons.Default.Warning, ErrorRed)
        SecurityEvent.Type.ACCESSIBILITY_REVOKED ->
            Pair(Icons.Default.Warning, ErrorRed)
        SecurityEvent.Type.SERVICE_RECOVERY ->
            Pair(Icons.Default.Security, SecondaryEmerald)
        SecurityEvent.Type.SYSTEM ->
            Pair(Icons.Default.Lock, PrimaryEmerald)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(1.dp, iconColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.displayTitle,
                    fontFamily = OrbitronFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = PureWhite
                )

                if (event.detail.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.detail,
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = event.formattedTime,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    color = iconColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

