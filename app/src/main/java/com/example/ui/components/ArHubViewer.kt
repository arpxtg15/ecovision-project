package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.VirtualHubModel
import com.example.ui.theme.GeoForestPrimary
import com.example.ui.theme.GeoLimeAccent
import com.example.ui.theme.GeoOliveSecondary
import com.example.ui.theme.GeoSunAmber
import com.example.ui.theme.LightCreamGreen

@Composable
fun ArHubViewer(
    model: VirtualHubModel,
    roomEnvironment: String,
    simulationValue: Float,
    onSimulationValueChanged: (Float) -> Unit,
    onPlaceInSpace: () -> Unit,
    onOpenSpecs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(model.realisticScale) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E293B))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    scale = (scale * zoom).coerceIn(0.6f, 2.0f)
                    rotationAngle += rotation
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            .testTag("ar_hub_viewer_canvas_container")
    ) {
        // Backdrop Image
        Image(
            painter = painterResource(id = R.drawable.ecovision_hub_art_1787546880512),
            contentDescription = "Room Environment",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.65f)
                        )
                    )
                )
        )

        // 3D Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2 + offsetX, size.height / 2 + offsetY - 10.dp.toPx())
            val baseRadius = 75.dp.toPx() * scale

            // AR Ground Ring
            drawCircle(
                color = GeoLimeAccent.copy(alpha = 0.35f),
                radius = baseRadius * 1.3f,
                center = Offset(center.x, center.y + baseRadius * 0.6f),
                style = Stroke(width = 1.5.dp.toPx())
            )

            when (model.id) {
                "solar_panel_array" -> {
                    val panelW = baseRadius * 1.5f
                    val panelH = baseRadius * 0.9f
                    val tilt = simulationValue * 30f

                    val p1 = Offset(center.x - panelW / 2, center.y - panelH / 2 + tilt)
                    val p2 = Offset(center.x + panelW / 2, center.y - panelH / 2 - tilt)
                    val p3 = Offset(center.x + panelW / 2, center.y + panelH / 2 - tilt)
                    val p4 = Offset(center.x - panelW / 2, center.y + panelH / 2 + tilt)

                    val panelPath = Path().apply {
                        moveTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(p3.x, p3.y)
                        lineTo(p4.x, p4.y)
                        close()
                    }
                    drawPath(path = panelPath, color = Color(0xFF16321D).copy(alpha = 0.85f))
                    drawPath(path = panelPath, color = GeoLimeAccent, style = Stroke(width = 2.dp.toPx()))
                }
                "indoor_composter" -> {
                    val cylW = baseRadius * 1.0f
                    val cylH = baseRadius * 1.2f
                    drawRoundRect(
                        color = Color(0xFF2C3E2D).copy(alpha = 0.85f),
                        topLeft = Offset(center.x - cylW / 2, center.y - cylH / 2),
                        size = Size(cylW, cylH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                    )
                    drawRoundRect(
                        color = GeoLimeAccent,
                        topLeft = Offset(center.x - cylW / 2, center.y - cylH / 2),
                        size = Size(cylW, cylH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                "urban_micro_garden" -> {
                    for (i in 0..2) {
                        val tierY = center.y - (baseRadius * 0.4f) + (i * 28f)
                        val tierW = baseRadius * (1.1f - (i * 0.15f))
                        drawCircle(
                            color = GeoForestPrimary.copy(alpha = 0.8f),
                            radius = tierW / 2,
                            center = Offset(center.x, tierY)
                        )
                        drawCircle(
                            color = GeoLimeAccent,
                            radius = tierW / 2,
                            center = Offset(center.x, tierY),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
                else -> {
                    drawCircle(
                        color = GeoForestPrimary.copy(alpha = 0.8f),
                        radius = baseRadius * 0.8f,
                        center = center
                    )
                    drawCircle(
                        color = GeoLimeAccent,
                        radius = baseRadius * 0.8f,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        // Top Badge & Specs Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = "${model.iconEmoji} ${model.title}",
                    color = LightCreamGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                onClick = onOpenSpecs,
                modifier = Modifier.testTag("open_model_specs_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Specs",
                    tint = LightCreamGreen,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(18.dp)
                )
            }
        }

        // Bottom Placement Action Bar
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scale = model.realisticScale
                        rotationAngle = 0f
                        offsetX = 0f
                        offsetY = 0f
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset",
                        tint = LightCreamGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Button(
                    onClick = onPlaceInSpace,
                    colors = ButtonDefaults.buttonColors(containerColor = GeoForestPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("save_placement_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ViewInAr,
                        contentDescription = null,
                        tint = LightCreamGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Place in $roomEnvironment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = LightCreamGreen
                    )
                }
            }
        }
    }
}
