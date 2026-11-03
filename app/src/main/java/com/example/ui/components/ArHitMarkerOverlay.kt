package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ar.ArDetectedPlane
import com.example.ar.ArHitMarker
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DarkText
import com.example.ui.theme.DeepForestGreen
import com.example.ui.theme.EcoAmberWarm
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoMintBorder
import com.example.ui.theme.EcoMintContainer
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.NatureGreen
import com.example.ui.theme.SoftPaleGreen
import kotlin.math.roundToInt

@Composable
fun ArHitMarkerOverlay(
    markers: List<ArHitMarker>,
    detectedPlanes: List<ArDetectedPlane>,
    activeMarker: ArHitMarker?,
    onMarkerClick: (ArHitMarker) -> Unit,
    onTapToHitTest: (Float, Float) -> Unit,
    onMarkerDismiss: () -> Unit,
    onScanMarkerItem: (ArHitMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "marker_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val normX = (offset.x / size.width).coerceIn(0.1f, 0.9f)
                    val normY = (offset.y / size.height).coerceIn(0.15f, 0.85f)
                    onTapToHitTest(normX, normY)
                }
            }
            .testTag("ar_hit_marker_overlay")
    ) {
        // 1. Draw 3D Plane Mesh, Sub-mm Caliper Lines & Precision Hit Markers
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Render Detected Physical Plane Outlines
            detectedPlanes.forEach { plane ->
                drawDetectedPlaneMesh(plane, width, height, waveOffset)
            }

            // Render 3D Precision Hit Markers, Bounding Brackets & Component Badges
            markers.forEach { marker ->
                val isFocused = marker.id == activeMarker?.id
                val markerPxX = marker.screenNormX * width
                val markerPxY = marker.screenNormY * height

                val markerActionColor = when (marker.threeRClassification.uppercase()) {
                    "REDUCE" -> Color(0xFFFFA726) // Amber
                    "REUSE" -> Color(0xFF29B6F6) // Sky Blue
                    "REPAIR" -> Color(0xFFAB47BC) // Violet
                    "SPECIAL", "SPECIAL_DISPOSAL" -> Color(0xFFFF5252) // Coral Red
                    else -> AccentLime // Lime Green for RECYCLE
                }

                // Draw AR component target brackets
                drawComponentTargetBrackets(
                    centerX = markerPxX,
                    centerY = markerPxY,
                    isFocused = isFocused,
                    color = markerActionColor,
                    pulse = if (isFocused) pulseScale else 1.0f
                )

                draw3DPrecisionHitMarker(
                    x = markerPxX,
                    y = markerPxY,
                    isFocused = isFocused,
                    pulse = if (isFocused) pulseScale else 1.0f,
                    marker = marker,
                    actionColor = markerActionColor
                )
            }
        }

        // 2. Focused Precision Telemetry Hologram Card with Object Intelligence
        AnimatedVisibility(
            visible = activeMarker != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 12.dp, vertical = 52.dp)
        ) {
            if (activeMarker != null) {
                val classificationBadge = when (activeMarker.threeRClassification.uppercase()) {
                    "REDUCE" -> "REDUCE"
                    "REUSE" -> "REUSE"
                    "RECYCLE" -> "RECYCLABLE"
                    "COMPOST" -> "COMPOSTABLE"
                    else -> "TARGET"
                }
                val badgeColor = when (activeMarker.threeRClassification.uppercase()) {
                    "REDUCE" -> Color(0xFFEF5350)
                    "REUSE" -> Color(0xFF29B6F6)
                    "RECYCLE" -> AccentLime
                    "COMPOST" -> Color(0xFF81C784)
                    else -> AccentLime
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xE60D2818),
                    border = BorderStroke(
                        1.2.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.8f),
                                badgeColor,
                                Color.White.copy(alpha = 0.2f)
                            )
                        )
                    ),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .clickable { onMarkerClick(activeMarker) }
                        .testTag("ar_focused_marker_card")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        // Top Header: Name, Badge, Inspect Action, Close
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(badgeColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Place,
                                        contentDescription = null,
                                        tint = badgeColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = activeMarker.label,
                                            color = LightCreamGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = badgeColor.copy(alpha = 0.25f),
                                            border = BorderStroke(0.5.dp, badgeColor)
                                        ) {
                                            Text(
                                                text = classificationBadge,
                                                color = badgeColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${activeMarker.resinCode} • ${(activeMarker.confidence * 100).roundToInt()}% match",
                                        color = SoftPaleGreen,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Inspect / Scan button
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = badgeColor,
                                    modifier = Modifier
                                        .clickable { onScanMarkerItem(activeMarker) }
                                        .testTag("ar_scan_marker_action")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoAwesome,
                                            contentDescription = "Inspect",
                                            tint = DeepForestGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Inspect",
                                            color = DeepForestGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                IconButton(
                                    onClick = onMarkerDismiss,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Dismiss",
                                        tint = SoftPaleGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Subtle Single-line Tip
                        val tip = activeMarker.recycleTip.ifBlank { activeMarker.reuseTip.ifBlank { activeMarker.reduceTip } }
                        if (tip.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "💡 $tip",
                                color = LightCreamGreen.copy(alpha = 0.9f),
                                fontSize = 10.sp,
                                maxLines = 1,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Draws a subtle AR grid mesh outlining detected physical planes.
 */
private fun DrawScope.drawDetectedPlaneMesh(
    plane: ArDetectedPlane,
    width: Float,
    height: Float,
    waveAnim: Float
) {
    val centerX = plane.centerScreenX * width
    val centerY = plane.centerScreenY * height
    val planeRadiusX = width * 0.38f
    val planeRadiusY = height * 0.22f

    // Draw Plane Boundary Ellipse
    drawOval(
        color = AccentLime.copy(alpha = 0.22f),
        topLeft = Offset(centerX - planeRadiusX, centerY - planeRadiusY),
        size = Size(planeRadiusX * 2, planeRadiusY * 2),
        style = Stroke(
            width = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), waveAnim * 20f)
        )
    )

    // Draw Subtle Cross Plane Grid Lines
    drawLine(
        color = SoftPaleGreen.copy(alpha = 0.2f),
        start = Offset(centerX - planeRadiusX * 0.7f, centerY),
        end = Offset(centerX + planeRadiusX * 0.7f, centerY),
        strokeWidth = 1.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
    )
    drawLine(
        color = SoftPaleGreen.copy(alpha = 0.2f),
        start = Offset(centerX, centerY - planeRadiusY * 0.7f),
        end = Offset(centerX, centerY + planeRadiusY * 0.7f),
        strokeWidth = 1.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
    )
}

/**
 * Draws high-tech corner brackets and targeting reticle for recognized 3R product components.
 */
private fun DrawScope.drawComponentTargetBrackets(
    centerX: Float,
    centerY: Float,
    isFocused: Boolean,
    color: Color,
    pulse: Float
) {
    val boxHalfW = if (isFocused) 52.dp.toPx() * pulse else 38.dp.toPx()
    val boxHalfH = if (isFocused) 32.dp.toPx() * pulse else 24.dp.toPx()
    val cornerLen = if (isFocused) 14.dp.toPx() else 9.dp.toPx()
    val strokeWidth = if (isFocused) 2.2.dp.toPx() else 1.4.dp.toPx()

    val left = centerX - boxHalfW
    val top = centerY - boxHalfH
    val right = centerX + boxHalfW
    val bottom = centerY + boxHalfH

    // Top-Left Corner
    drawLine(color, Offset(left, top), Offset(left + cornerLen, top), strokeWidth)
    drawLine(color, Offset(left, top), Offset(left, top + cornerLen), strokeWidth)

    // Top-Right Corner
    drawLine(color, Offset(right, top), Offset(right - cornerLen, top), strokeWidth)
    drawLine(color, Offset(right, top), Offset(right, top + cornerLen), strokeWidth)

    // Bottom-Left Corner
    drawLine(color, Offset(left, bottom), Offset(left + cornerLen, bottom), strokeWidth)
    drawLine(color, Offset(left, bottom), Offset(left, bottom - cornerLen), strokeWidth)

    // Bottom-Right Corner
    drawLine(color, Offset(right, bottom), Offset(right - cornerLen, bottom), strokeWidth)
    drawLine(color, Offset(right, bottom), Offset(right, bottom - cornerLen), strokeWidth)

    if (isFocused) {
        // Glowing target box fill
        drawRect(
            color = color.copy(alpha = 0.08f),
            topLeft = Offset(left, top),
            size = Size(boxHalfW * 2, boxHalfH * 2)
        )
    }
}

/**
 * Draws the high-precision 3D isometric ground plane anchor ring, sub-mm crosshairs, and holographic caliper pin.
 */
private fun DrawScope.draw3DPrecisionHitMarker(
    x: Float,
    y: Float,
    isFocused: Boolean,
    pulse: Float,
    marker: ArHitMarker,
    actionColor: Color = AccentLime
) {
    val ringRadiusX = (22.dp.toPx() * pulse)
    val ringRadiusY = (9.dp.toPx() * pulse)
    val markerColor = if (isFocused) actionColor else SoftPaleGreen
    val glowColor = if (isFocused) actionColor.copy(alpha = 0.35f) else NatureGreen.copy(alpha = 0.2f)

    // 1. Ground Plane Isometric Anchor Ellipse
    drawOval(
        color = glowColor,
        topLeft = Offset(x - ringRadiusX * 1.3f, y - ringRadiusY * 1.3f),
        size = Size(ringRadiusX * 2.6f, ringRadiusY * 2.6f)
    )
    drawOval(
        color = markerColor,
        topLeft = Offset(x - ringRadiusX, y - ringRadiusY),
        size = Size(ringRadiusX * 2, ringRadiusY * 2),
        style = Stroke(width = if (isFocused) 2.5.dp.toPx() else 1.5.dp.toPx())
    )

    // 2. Ground Anchor Center Dot & Sub-millimeter Crosshairs
    drawCircle(
        color = markerColor,
        radius = 3.5.dp.toPx(),
        center = Offset(x, y)
    )

    if (isFocused) {
        val crossLen = 14.dp.toPx()
        drawLine(
            color = actionColor.copy(alpha = 0.8f),
            start = Offset(x - crossLen, y),
            end = Offset(x + crossLen, y),
            strokeWidth = 1.2.dp.toPx()
        )
        drawLine(
            color = actionColor.copy(alpha = 0.8f),
            start = Offset(x, y - crossLen / 2),
            end = Offset(x, y + crossLen / 2),
            strokeWidth = 1.2.dp.toPx()
        )
    }

    // 3. Vertical 3D Hologram Elevation Stem
    val stemHeight = 34.dp.toPx()
    val topPinY = y - stemHeight

    drawLine(
        brush = Brush.verticalGradient(
            colors = listOf(markerColor, markerColor.copy(alpha = 0.3f)),
            startY = topPinY,
            endY = y
        ),
        start = Offset(x, topPinY),
        end = Offset(x, y),
        strokeWidth = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
    )

    // 4. Floating Hologram Pin Head
    drawCircle(
        color = if (isFocused) DeepForestGreen else EcoGreenPrimary,
        radius = 8.dp.toPx(),
        center = Offset(x, topPinY)
    )
    drawCircle(
        color = markerColor,
        radius = 8.dp.toPx(),
        center = Offset(x, topPinY),
        style = Stroke(width = 2.dp.toPx())
    )
    drawCircle(
        color = markerColor,
        radius = 3.5.dp.toPx(),
        center = Offset(x, topPinY)
    )
}

