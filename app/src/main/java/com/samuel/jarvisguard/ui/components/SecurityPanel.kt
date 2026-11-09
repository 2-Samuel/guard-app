package com.samuel.jarvisguard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samuel.jarvisguard.ui.theme.CardBackground
import com.samuel.jarvisguard.ui.theme.InterFontFamily
import com.samuel.jarvisguard.ui.theme.PrimaryEmerald
import com.samuel.jarvisguard.ui.theme.PureWhite
import com.samuel.jarvisguard.ui.theme.SecondaryText

@Composable
fun SecurityPanel(
    modifier: Modifier = Modifier,
    title: String = "Using your fingerprint",
    subtitle: String = "Your privacy. Our priority.",
    onClick: () -> Unit = {}
) {
    val cornerRadius = 16.dp
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(CardBackground)
            .border(
                width = 1.4.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PrimaryEmerald.copy(alpha = 0.85f),
                        PrimaryEmerald,
                        PrimaryEmerald.copy(alpha = 0.85f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = PrimaryEmerald),
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Glowing Shield with Padlock Icon
            ShieldLockIcon(
                modifier = Modifier.size(width = 42.dp, height = 48.dp),
                color = PrimaryEmerald
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
fun ShieldLockIcon(
    modifier: Modifier = Modifier,
    color: Color = PrimaryEmerald
) {
    Canvas(modifier = modifier) {
        val w = drawContext.size.width
        val h = drawContext.size.height

        // 1. Draw outer Shield contour
        val shieldPath = Path().apply {
            moveTo(w * 0.5f, h * 0.04f)
            // Top right curve
            cubicTo(
                w * 0.78f, h * 0.04f,
                w * 0.94f, h * 0.12f,
                w * 0.94f, h * 0.32f
            )
            // Down to bottom center tip
            cubicTo(
                w * 0.94f, h * 0.65f,
                w * 0.65f, h * 0.88f,
                w * 0.5f, h * 0.96f
            )
            // Up to bottom left
            cubicTo(
                w * 0.35f, h * 0.88f,
                w * 0.06f, h * 0.65f,
                w * 0.06f, h * 0.32f
            )
            // Top left curve
            cubicTo(
                w * 0.06f, h * 0.12f,
                w * 0.22f, h * 0.04f,
                w * 0.5f, h * 0.04f
            )
            close()
        }

        // Soft green fill inside shield
        drawPath(
            path = shieldPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.25f),
                    color.copy(alpha = 0.05f)
                ),
                center = Offset(w * 0.5f, h * 0.45f),
                radius = w * 0.6f
            )
        )

        // Shield border
        drawPath(
            path = shieldPath,
            color = color,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        )

        // 2. Padlock inside shield
        val lockCenterX = w * 0.5f
        val lockCenterY = h * 0.54f
        val bodyWidth = w * 0.36f
        val bodyHeight = h * 0.26f

        // Padlock body (rounded rectangle)
        drawRoundRect(
            color = color,
            topLeft = Offset(lockCenterX - bodyWidth / 2f, lockCenterY - bodyHeight * 0.15f),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )

        // Padlock shackle (arch above body)
        val shackleRadius = bodyWidth * 0.32f
        val shackleTop = lockCenterY - bodyHeight * 0.62f
        val shacklePath = Path().apply {
            moveTo(lockCenterX - shackleRadius, lockCenterY - bodyHeight * 0.12f)
            lineTo(lockCenterX - shackleRadius, shackleTop + shackleRadius)
            arcTo(
                rect = Rect(
                    lockCenterX - shackleRadius,
                    shackleTop,
                    lockCenterX + shackleRadius,
                    shackleTop + shackleRadius * 2
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(lockCenterX + shackleRadius, lockCenterY - bodyHeight * 0.12f)
        }

        drawPath(
            path = shacklePath,
            color = color,
            style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
        )

        // Padlock keyhole (dark dot and slit)
        drawCircle(
            color = CardBackground,
            radius = 1.8.dp.toPx(),
            center = Offset(lockCenterX, lockCenterY + bodyHeight * 0.22f)
        )
        drawLine(
            color = CardBackground,
            start = Offset(lockCenterX, lockCenterY + bodyHeight * 0.22f),
            end = Offset(lockCenterX, lockCenterY + bodyHeight * 0.52f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

