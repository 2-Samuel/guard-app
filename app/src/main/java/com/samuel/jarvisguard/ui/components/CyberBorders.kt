package com.samuel.jarvisguard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.samuel.jarvisguard.ui.theme.CyberBorderDim
import com.samuel.jarvisguard.ui.theme.CyberBorderGreen
import com.samuel.jarvisguard.ui.theme.PrimaryEmerald

@Composable
fun CyberBorders(
    modifier: Modifier = Modifier,
    accentColor: Color = PrimaryEmerald
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val sideMargin = 18.dp.toPx()
        val jogOffset = 10.dp.toPx()
        val strokeWidth = 1.4.dp.toPx()

        val borderBrush = Brush.verticalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.2f),
                accentColor.copy(alpha = 0.85f),
                accentColor.copy(alpha = 0.95f),
                accentColor.copy(alpha = 0.85f),
                accentColor.copy(alpha = 0.3f)
            ),
            startY = h * 0.05f,
            endY = h * 0.95f
        )

        // 1. LEFT CYBERNETIC BORDER WITH ANGULAR NOTCHES
        val leftPath = Path().apply {
            // Start near top
            moveTo(sideMargin, h * 0.08f)
            lineTo(sideMargin, h * 0.22f)
            // Top jog inward
            lineTo(sideMargin - jogOffset * 0.5f, h * 0.23f)
            lineTo(sideMargin - jogOffset * 0.5f, h * 0.27f)
            lineTo(sideMargin, h * 0.28f)
            // Continue down to scanner area
            lineTo(sideMargin, h * 0.38f)
            // Scanner jog outward
            lineTo(sideMargin - jogOffset, h * 0.395f)
            lineTo(sideMargin - jogOffset, h * 0.52f)
            lineTo(sideMargin, h * 0.535f)
            // Down to lower panel area
            lineTo(sideMargin, h * 0.65f)
            // Lower jog inward
            lineTo(sideMargin + jogOffset * 0.6f, h * 0.665f)
            lineTo(sideMargin + jogOffset * 0.6f, h * 0.72f)
            lineTo(sideMargin, h * 0.735f)
            // Down to bottom
            lineTo(sideMargin, h * 0.92f)
        }

        drawPath(
            path = leftPath,
            brush = borderBrush,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // 2. RIGHT CYBERNETIC BORDER (Symmetric to left)
        val rightX = w - sideMargin
        val rightPath = Path().apply {
            moveTo(rightX, h * 0.08f)
            lineTo(rightX, h * 0.22f)
            // Top jog inward
            lineTo(rightX + jogOffset * 0.5f, h * 0.23f)
            lineTo(rightX + jogOffset * 0.5f, h * 0.27f)
            lineTo(rightX, h * 0.28f)
            // Continue down to scanner area
            lineTo(rightX, h * 0.38f)
            // Scanner jog outward
            lineTo(rightX + jogOffset, h * 0.395f)
            lineTo(rightX + jogOffset, h * 0.52f)
            lineTo(rightX, h * 0.535f)
            // Down to lower panel area
            lineTo(rightX, h * 0.65f)
            // Lower jog inward
            lineTo(rightX - jogOffset * 0.6f, h * 0.665f)
            lineTo(rightX - jogOffset * 0.6f, h * 0.72f)
            lineTo(rightX, h * 0.735f)
            // Down to bottom
            lineTo(rightX, h * 0.92f)
        }

        drawPath(
            path = rightPath,
            brush = borderBrush,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // 3. TOP CYBER ACCENTS (Subtle sci-fi notches above header)
        val topY = h * 0.07f
        val topRoofWidth = w * 0.28f
        val topCenterX = w / 2f
        val topPath = Path().apply {
            moveTo(topCenterX - topRoofWidth, topY)
            lineTo(topCenterX - topRoofWidth * 0.65f, topY - 5.dp.toPx())
            lineTo(topCenterX + topRoofWidth * 0.65f, topY - 5.dp.toPx())
            lineTo(topCenterX + topRoofWidth, topY)
        }
        drawPath(
            path = topPath,
            color = accentColor.copy(alpha = 0.5f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // 4. BOTTOM CYBER BRACKETS (Below bottom branding)
        val botY = h * 0.94f
        val botWidth = w * 0.42f
        val botPath = Path().apply {
            moveTo(topCenterX - botWidth, botY)
            lineTo(topCenterX - botWidth * 0.75f, botY)
            lineTo(topCenterX - botWidth * 0.62f, botY + 8.dp.toPx())
            lineTo(topCenterX + botWidth * 0.62f, botY + 8.dp.toPx())
            lineTo(topCenterX + botWidth * 0.75f, botY)
            lineTo(topCenterX + botWidth, botY)
        }
        drawPath(
            path = botPath,
            color = accentColor.copy(alpha = 0.7f),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Ambient center radial glow (Soft luminous green pulse in background)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.12f),
                    accentColor.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(w / 2f, h * 0.40f),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = Offset(w / 2f, h * 0.40f)
        )
    }
}

