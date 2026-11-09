package com.samuel.jarvisguard.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.samuel.jarvisguard.ui.theme.BrightScannerGreen
import com.samuel.jarvisguard.ui.theme.ErrorRed
import com.samuel.jarvisguard.ui.theme.PrimaryEmerald
import com.samuel.jarvisguard.ui.theme.PureWhite
import kotlin.math.cos
import kotlin.math.sin

enum class ScannerState {
    IDLE,
    SCANNING,
    SUCCESS,
    FAILED
}

@Composable
fun FingerprintScanner(
    modifier: Modifier = Modifier,
    scannerState: ScannerState = ScannerState.SCANNING,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scannerAnimations")

    // Outer ring rotation (slow, smooth)
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerRotation"
    )

    // Middle ring counter-rotation
    val middleRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "middleRotation"
    )

    // Vertical scanning beam position (-1f to 1f)
    val beamOffsetNormalized by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beamOffset"
    )

    // Core glow pulse
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val baseColor = when (scannerState) {
        ScannerState.FAILED -> ErrorRed
        ScannerState.SUCCESS -> BrightScannerGreen
        else -> PrimaryEmerald
    }

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = drawContext.size.width
            val canvasHeight = drawContext.size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)
            val outerRadius = canvasWidth * 0.44f
            val middleRadius = canvasWidth * 0.38f
            val innerRadius = canvasWidth * 0.31f

            // 1. Soft radial ambient glow behind the scanner
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        baseColor.copy(alpha = 0.28f * glowPulse),
                        baseColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = outerRadius * 1.3f
                ),
                radius = outerRadius * 1.3f,
                center = center
            )

            // 2. Outer segmented HUD Ring
            rotate(degrees = if (scannerState == ScannerState.SCANNING) outerRotation else 0f, pivot = center) {
                // Segment 1
                drawArc(
                    color = baseColor,
                    startAngle = 10f,
                    sweepAngle = 70f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                // Segment 2
                drawArc(
                    color = baseColor,
                    startAngle = 100f,
                    sweepAngle = 70f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                // Segment 3
                drawArc(
                    color = baseColor,
                    startAngle = 190f,
                    sweepAngle = 70f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                // Segment 4
                drawArc(
                    color = baseColor,
                    startAngle = 280f,
                    sweepAngle = 70f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Outer accent tick marks
                val tickCount = 24
                for (i in 0 until tickCount) {
                    val angleRad = Math.toRadians((i * (360.0 / tickCount)))
                    val innerTickR = outerRadius + 5.dp.toPx()
                    val outerTickR = outerRadius + (if (i % 6 == 0) 10.dp.toPx() else 7.dp.toPx())
                    val p1 = Offset(
                        (center.x + innerTickR * cos(angleRad)).toFloat(),
                        (center.y + innerTickR * sin(angleRad)).toFloat()
                    )
                    val p2 = Offset(
                        (center.x + outerTickR * cos(angleRad)).toFloat(),
                        (center.y + outerTickR * sin(angleRad)).toFloat()
                    )
                    drawLine(
                        color = baseColor.copy(alpha = if (i % 6 == 0) 0.8f else 0.35f),
                        start = p1,
                        end = p2,
                        strokeWidth = if (i % 6 == 0) 2.dp.toPx() else 1.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // 3. Middle HUD Ring with dashed segments
            rotate(degrees = if (scannerState == ScannerState.SCANNING) middleRotation else 0f, pivot = center) {
                // Arcs
                drawArc(
                    color = baseColor.copy(alpha = 0.55f),
                    startAngle = 30f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(center.x - middleRadius, center.y - middleRadius),
                    size = Size(middleRadius * 2, middleRadius * 2),
                    style = Stroke(width = 1.4.dp.toPx())
                )
                drawArc(
                    color = baseColor.copy(alpha = 0.55f),
                    startAngle = 210f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(center.x - middleRadius, center.y - middleRadius),
                    size = Size(middleRadius * 2, middleRadius * 2),
                    style = Stroke(width = 1.4.dp.toPx())
                )
            }

            // 4. Inner Ring (Framing the fingerprint)
            drawCircle(
                color = baseColor.copy(alpha = 0.9f),
                radius = innerRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Inner Ring Accent notch brackets
            drawArc(
                color = PureWhite.copy(alpha = 0.7f),
                startAngle = 80f,
                sweepAngle = 20f,
                useCenter = false,
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = PureWhite.copy(alpha = 0.7f),
                startAngle = 260f,
                sweepAngle = 20f,
                useCenter = false,
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // 5. High-fidelity Fingerprint Vector Ridges
            drawFingerprint(
                center = center,
                color = if (scannerState == ScannerState.SUCCESS) PureWhite else baseColor,
                coreGlowAlpha = glowPulse
            )

            // 6. Horizontal Scanning Laser Beam
            if (scannerState == ScannerState.SCANNING) {
                val scanRange = innerRadius * 0.78f
                val beamY = center.y + (beamOffsetNormalized * scanRange)
                val beamHalfWidth = canvasWidth * 0.46f

                // Wide soft horizontal laser flare
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            baseColor.copy(alpha = 0.3f),
                            BrightScannerGreen.copy(alpha = 0.95f),
                            PureWhite,
                            BrightScannerGreen.copy(alpha = 0.95f),
                            baseColor.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        startX = center.x - beamHalfWidth,
                        endX = center.x + beamHalfWidth
                    ),
                    start = Offset(center.x - beamHalfWidth, beamY),
                    end = Offset(center.x + beamHalfWidth, beamY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Intense center horizontal laser core
                val coreHalfWidth = innerRadius * 0.95f
                drawLine(
                    color = PureWhite,
                    start = Offset(center.x - coreHalfWidth, beamY),
                    end = Offset(center.x + coreHalfWidth, beamY),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Laser glow bloom (elliptical vertical flare)
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BrightScannerGreen.copy(alpha = 0.5f),
                            baseColor.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(center.x, beamY),
                        radius = coreHalfWidth * 0.8f
                    ),
                    topLeft = Offset(center.x - coreHalfWidth * 0.8f, beamY - 14.dp.toPx()),
                    size = Size(coreHalfWidth * 1.6f, 28.dp.toPx())
                )
            } else if (scannerState == ScannerState.SUCCESS) {
                // Fixed luminous center beam on success
                drawLine(
                    color = PureWhite,
                    start = Offset(center.x - innerRadius * 1.1f, center.y),
                    end = Offset(center.x + innerRadius * 1.1f, center.y),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Draws detailed, concentric arching ridges replicating real fingerprint loops and whorls.
 */
private fun DrawScope.drawFingerprint(
    center: Offset,
    color: Color,
    coreGlowAlpha: Float
) {
    val strokeWidth = 2.0.dp.toPx()
    val ridgeColor = color.copy(alpha = 0.92f)

    // Soft luminous radial center in the core
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.45f * coreGlowAlpha),
                color.copy(alpha = 0.12f),
                Color.Transparent
            ),
            center = center,
            radius = 35.dp.toPx()
        ),
        radius = 35.dp.toPx(),
        center = center
    )

    // Defined concentric fingerprint loops and whorls (arch patterns)
    val loopRadii = listOf(
        Pair(8.dp.toPx(), 12.dp.toPx()),
        Pair(14.dp.toPx(), 20.dp.toPx()),
        Pair(20.dp.toPx(), 28.dp.toPx()),
        Pair(26.dp.toPx(), 36.dp.toPx()),
        Pair(32.dp.toPx(), 44.dp.toPx()),
        Pair(38.dp.toPx(), 52.dp.toPx()),
        Pair(44.dp.toPx(), 60.dp.toPx()),
        Pair(50.dp.toPx(), 67.dp.toPx())
    )

    for ((rx, ry) in loopRadii) {
        val path = Path().apply {
            // Upper arch of the fingerprint (inverted U-turn)
            val rect = Rect(
                center.x - rx,
                center.y - ry + 4.dp.toPx(),
                center.x + rx,
                center.y + ry * 0.4f
            )
            arcTo(rect, 180f, 180f, false)

            // Left tail descending downward
            lineTo(center.x - rx * 0.95f, center.y + ry * 0.85f)

            // Right tail descending downward
            moveTo(center.x + rx, center.y + ry * 0.4f - (ry * 0.3f))
            lineTo(center.x + rx * 0.95f, center.y + ry * 0.85f)
        }

        drawPath(
            path = path,
            color = ridgeColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }

    // Core innermost swirl
    val innerCore = Path().apply {
        moveTo(center.x - 3.dp.toPx(), center.y + 4.dp.toPx())
        cubicTo(
            center.x - 4.dp.toPx(), center.y - 5.dp.toPx(),
            center.x + 4.dp.toPx(), center.y - 5.dp.toPx(),
            center.x + 3.dp.toPx(), center.y + 4.dp.toPx()
        )
    }
    drawPath(
        path = innerCore,
        color = ridgeColor,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Bottom lateral ridges / delta creases
    val bottomCreases = listOf(
        Pair(-35.dp.toPx(), 45.dp.toPx()),
        Pair(-25.dp.toPx(), 52.dp.toPx()),
        Pair(25.dp.toPx(), 52.dp.toPx()),
        Pair(35.dp.toPx(), 45.dp.toPx())
    )

    for (p in bottomCreases) {
        drawLine(
            color = ridgeColor,
            start = Offset(center.x + p.first * 0.6f, center.y + p.second * 0.7f),
            end = Offset(center.x + p.first, center.y + p.second),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

