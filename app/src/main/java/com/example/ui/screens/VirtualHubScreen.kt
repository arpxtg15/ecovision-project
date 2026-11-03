package com.example.ui.screens

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
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.VirtualHubModel
import com.example.ui.theme.DarkText
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoGreenPrimaryDark
import com.example.ui.theme.LightCreamGreen
import com.example.ui.viewmodel.HubUiState
import com.example.ui.viewmodel.PlacedHubItem
import kotlin.math.roundToInt

@Composable
fun VirtualHubScreen(
    hubState: HubUiState,
    allModels: List<VirtualHubModel>,
    onAddModel: (VirtualHubModel, Float, Float) -> Unit,
    onMoveItem: (Int, Float, Float) -> Unit,
    onSelectItem: (Int?) -> Unit,
    onResizeItem: (Int, Float) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onClearAll: () -> Unit,
    onSaveSnapshot: () -> Unit,
    onOpenSpecs: (VirtualHubModel) -> Unit,
    onDismissSpecs: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var backgroundModeIndex by remember { mutableIntStateOf(0) } // 0: Practice Room Art, 1: Dark Studio Gradient
    val selectedItem = hubState.placedItems.find { it.id == hubState.selectedPlacedItemId }

    val infiniteTransition = rememberInfiniteTransition(label = "hub_anim")
    val dashedPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dash_phase"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A1412))
            .testTag("virtual_hub_screen")
    ) {
        val stageWidthPx = constraints.maxWidth.toFloat()
        val stageHeightPx = constraints.maxHeight.toFloat()

        // 1. Interactive Stage Backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onSelectItem(null) }
                .testTag("virtual_hub_stage_canvas")
        ) {
            if (backgroundModeIndex == 0) {
                Image(
                    painter = painterResource(id = R.drawable.ecovision_hub_art_1787546880512),
                    contentDescription = "Virtual Hub Space Backdrop",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Soft gradient vignette for enhanced contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )
            } else {
                // Studio Dark Gradient (Matches HTML linear-gradient(#35574f, #0b1f1c))
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF35574F),
                                    Color(0xFF132B25),
                                    Color(0xFF0B1F1C)
                                )
                            )
                        )
                )
                // Subtle floor grid lines to enhance AR depth
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val horizonY = size.height * 0.58f
                    val lineCount = 8
                    for (i in 0..lineCount) {
                        val y = horizonY + (size.height - horizonY) * (i.toFloat() / lineCount)
                        drawLine(
                            color = Color(0x22FFFFFF),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
            }

            // 2. Placed AR Eco Upgrades (Freeform drag-to-position)
            hubState.placedItems.forEach { item ->
                val isSelected = item.id == hubState.selectedPlacedItemId
                val sizeDp = item.sizeDp.dp

                Box(
                    modifier = Modifier
                        .offset {
                            // Center baseline + custom drag offsets
                            val baseCenterX = stageWidthPx / 2f - (item.sizeDp * 1.5f)
                            val baseCenterY = stageHeightPx / 2f - (item.sizeDp * 1.5f)
                            IntOffset(
                                (baseCenterX + item.offsetX).roundToInt(),
                                (baseCenterY + item.offsetY).roundToInt()
                            )
                        }
                        .size(sizeDp)
                        .pointerInput(item.id) {
                            detectDragGestures(
                                onDragStart = { onSelectItem(item.id) },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    onMoveItem(item.id, dragAmount.x, dragAmount.y)
                                }
                            )
                        }
                        .clickable { onSelectItem(item.id) }
                        .testTag("placed_hub_item_${item.id}")
                ) {
                    // Selection Glow & Dashed Border Ring (Matches HTML outline: 2px dashed #f2c94c)
                    if (isSelected) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRoundRect(
                                color = Color(0xFFF2C94C),
                                size = size,
                                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                                style = Stroke(
                                    width = 2.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(16f, 12f),
                                        dashedPhase
                                    )
                                )
                            )
                        }
                    }

                    // Vector Eco Upgrade Canvas Renderer
                    EcoUpgradeGraphicCanvas(
                        modelId = item.model.id,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // 3. Top Overlay Bar (Back button, Title, View Toggle, Save Snapshot)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pill Back Button (Matches HTML .back { background: var(--green); ... })
            Surface(
                shape = CircleShape,
                color = EcoGreenPrimary,
                onClick = onNavigateBack,
                shadowElevation = 4.dp,
                modifier = Modifier.testTag("virtual_hub_back_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Back",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }

            // Backdrop Switcher Pill
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                onClick = { backgroundModeIndex = (backgroundModeIndex + 1) % 2 },
                modifier = Modifier.testTag("virtual_hub_backdrop_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (backgroundModeIndex == 0) Icons.Filled.ViewInAr else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = Color(0xFFF2C94C),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (backgroundModeIndex == 0) "Room AR" else "Studio",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            // Save Snapshot Button (Matches HTML .snap { background: rgba(255,255,255,.92); ... })
            Surface(
                shape = CircleShape,
                color = Color(0xF2FFFFFF),
                shadowElevation = 4.dp,
                onClick = onSaveSnapshot,
                modifier = Modifier.testTag("virtual_hub_save_snap_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📸", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save",
                        color = Color(0xFF02231F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }

        // 4. Top Toast / Feedback Pill
        AnimatedVisibility(
            visible = hubState.toastFeedback != null,
            enter = fadeIn(tween(200)) + slideInVertically(tween(200)),
            exit = fadeOut(tween(200)) + slideOutVertically(tween(200)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 68.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xF002231F),
                border = BorderStroke(1.dp, Color(0xFFF2C94C).copy(alpha = 0.45f)),
                shadowElevation = 6.dp
            ) {
                Text(
                    text = hubState.toastFeedback ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
        }

        // 5. Instruction Hint Pill (Matches HTML #hubHint)
        if (hubState.placedItems.isEmpty()) {
            Surface(
                shape = CircleShape,
                color = Color(0xD902231F),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 74.dp)
            ) {
                Text(
                    text = "Tap an upgrade below, then drag it into place",
                    color = Color(0xFFE8DFC0),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // 6. Floating Upgrade Info & Size Control Bar (Matches HTML .hub-info)
        if (selectedItem != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xF7FFFFFF),
                shadowElevation = 10.dp,
                border = BorderStroke(1.dp, Color(0x334F9F2A)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 14.dp, end = 14.dp, bottom = 118.dp)
                    .fillMaxWidth()
                    .testTag("hub_item_info_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedItem.model.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedItem.model.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EcoGreenPrimaryDark,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = selectedItem.model.shortDescription,
                            fontSize = 11.5.sp,
                            color = DarkText.copy(alpha = 0.75f),
                            maxLines = 2,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Size controls (− / +), specs (ℹ️) and trash (🗑️)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEEF3E8),
                            onClick = { onResizeItem(selectedItem.id, -18f) },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("hub_resize_minus")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Smaller",
                                    tint = EcoGreenPrimaryDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEEF3E8),
                            onClick = { onResizeItem(selectedItem.id, 18f) },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("hub_resize_plus")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Bigger",
                                    tint = EcoGreenPrimaryDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEEF3E8),
                            onClick = { onOpenSpecs(selectedItem.model) },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("hub_item_specs_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Specs",
                                    tint = EcoGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFECEB),
                            onClick = { onRemoveItem(selectedItem.id) },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("hub_item_delete_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Remove",
                                    tint = Color(0xFFC0392B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Bottom Palette of Eco Upgrades (Matches HTML .palette)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.88f)
                        )
                    )
                )
                .padding(bottom = 12.dp, top = 8.dp)
                .testTag("virtual_hub_palette_bar")
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allModels) { model ->
                    val isCurrentModel = selectedItem?.model?.id == model.id

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isCurrentModel) Color.White else Color(0xF2FFFFFF),
                        border = if (isCurrentModel) BorderStroke(2.dp, EcoGreenPrimary) else null,
                        shadowElevation = 5.dp,
                        onClick = {
                            onAddModel(model, 0f, 0f)
                        },
                        modifier = Modifier
                            .width(88.dp)
                            .testTag("palette_item_${model.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE9F3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = model.iconEmoji,
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = model.title.split(" & ")[0].split(" ").take(2).joinToString(" "),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimaryDark,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 8. Specifications Dialog (When ℹ️ is tapped)
        if (hubState.showSpecsDialog && hubState.specsModel != null) {
            val specsModel = hubState.specsModel
            AlertDialog(
                onDismissRequest = onDismissSpecs,
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = specsModel.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = specsModel.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = EcoGreenPrimaryDark
                            )
                            Text(
                                text = specsModel.category,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EcoGreenPrimary
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = specsModel.shortDescription,
                            fontSize = 13.sp,
                            color = DarkText,
                            lineHeight = 17.sp
                        )

                        HorizontalDivider(color = Color(0xFFE3E0D0))

                        // Dimensions & Placement
                        Column {
                            Text(
                                text = "📏 Dimensions & Placement",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EcoGreenPrimaryDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${specsModel.specs.dimensions}\n• ${specsModel.specs.idealPlacement}",
                                fontSize = 12.sp,
                                color = DarkText.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }

                        // Annual Impact
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEAF5E4),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "🌱 Estimated Sustainability Impact",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = EcoGreenPrimaryDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• ${specsModel.specs.estimatedAnnualSavings}\n• ${specsModel.specs.carbonOffsetPerYear}",
                                    fontSize = 11.5.sp,
                                    color = EcoGreenPrimaryDark,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        // Installation Tip
                        if (specsModel.specs.installationTips.isNotEmpty()) {
                            Text(
                                text = "💡 Quick Tip: ${specsModel.specs.installationTips.first()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = DarkText.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = onDismissSpecs,
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            )
        }
    }
}

/**
 * High-fidelity Vector Eco Upgrade Canvas Renderer
 * Faithfully mirrors the visual icons from the HTML attachment (Solar, Compost, Plant, Barrel, LED, Aerator)
 */
@Composable
private fun EcoUpgradeGraphicCanvas(
    modelId: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        when {
            modelId.contains("solar") -> {
                // Solar Panel Array: Angled Photovoltaic Panel & Grid (Matches HTML <path d="M18 24h70l-10 44H8z" ...>)
                val p1 = Offset(w * 0.18f, h * 0.22f)
                val p2 = Offset(w * 0.88f, h * 0.22f)
                val p3 = Offset(w * 0.78f, h * 0.68f)
                val p4 = Offset(w * 0.08f, h * 0.68f)

                val panelPath = Path().apply {
                    moveTo(p1.x, p1.y)
                    lineTo(p2.x, p2.y)
                    lineTo(p3.x, p3.y)
                    lineTo(p4.x, p4.y)
                    close()
                }
                // Deep blue silicon cell fill
                drawPath(path = panelPath, color = Color(0xFF17427A))
                // Grid border
                drawPath(
                    path = panelPath,
                    color = Color(0xFFD9E2EC),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Solar grid lines
                drawLine(
                    color = Color(0xFFD9E2EC),
                    start = Offset(w * 0.31f, h * 0.22f),
                    end = Offset(w * 0.21f, h * 0.68f),
                    strokeWidth = 1.6.dp.toPx()
                )
                drawLine(
                    color = Color(0xFFD9E2EC),
                    start = Offset(w * 0.54f, h * 0.22f),
                    end = Offset(w * 0.46f, h * 0.68f),
                    strokeWidth = 1.6.dp.toPx()
                )
                drawLine(
                    color = Color(0xFFD9E2EC),
                    start = Offset(w * 0.72f, h * 0.22f),
                    end = Offset(w * 0.66f, h * 0.68f),
                    strokeWidth = 1.6.dp.toPx()
                )
                drawLine(
                    color = Color(0xFFD9E2EC),
                    start = Offset(w * 0.13f, h * 0.44f),
                    end = Offset(w * 0.83f, h * 0.44f),
                    strokeWidth = 1.6.dp.toPx()
                )
                drawLine(
                    color = Color(0xFFD9E2EC),
                    start = Offset(w * 0.10f, h * 0.56f),
                    end = Offset(w * 0.80f, h * 0.56f),
                    strokeWidth = 1.6.dp.toPx()
                )

                // Stand Legs
                drawLine(
                    color = Color(0xFF8B949B),
                    start = Offset(w * 0.43f, h * 0.68f),
                    end = Offset(w * 0.43f, h * 0.86f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color(0xFF8B949B),
                    start = Offset(w * 0.30f, h * 0.86f),
                    end = Offset(w * 0.60f, h * 0.86f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            modelId.contains("compost") -> {
                // Compost Bin: Earthen body + green organic sprout logo (Matches HTML <path d="M24 32h52l-5 52..." ...>)
                val binPath = Path().apply {
                    moveTo(w * 0.24f, h * 0.32f)
                    lineTo(w * 0.76f, h * 0.32f)
                    lineTo(w * 0.71f, h * 0.84f)
                    lineTo(w * 0.29f, h * 0.84f)
                    close()
                }
                drawPath(path = binPath, color = Color(0xFF6B4A2F))

                // Lid & handle
                drawRoundRect(
                    color = Color(0xFF4A3321),
                    topLeft = Offset(w * 0.19f, h * 0.22f),
                    size = Size(w * 0.62f, h * 0.12f),
                    cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFF4A3321),
                    topLeft = Offset(w * 0.42f, h * 0.16f),
                    size = Size(w * 0.16f, h * 0.07f),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )

                // Bio green sprout leaf
                val sproutPath = Path().apply {
                    moveTo(w * 0.50f, h * 0.72f)
                    cubicTo(w * 0.39f, h * 0.70f, w * 0.35f, h * 0.59f, w * 0.38f, h * 0.50f)
                    cubicTo(w * 0.49f, h * 0.50f, w * 0.58f, h * 0.57f, w * 0.58f, h * 0.66f)
                    close()
                }
                drawPath(path = sproutPath, color = Color(0xFF7BC043))
            }
            modelId.contains("plant") || modelId.contains("garden") -> {
                // Indoor Plant: Terracotta pot & two lush green leaves (Matches HTML <path d="M30 66h40l-5 22H35z" ...>)
                val potPath = Path().apply {
                    moveTo(w * 0.30f, h * 0.66f)
                    lineTo(w * 0.70f, h * 0.66f)
                    lineTo(w * 0.65f, h * 0.88f)
                    lineTo(w * 0.35f, h * 0.88f)
                    close()
                }
                drawPath(path = potPath, color = Color(0xFFC4683A))

                // Pot rim
                drawRoundRect(
                    color = Color(0xFFD9794A),
                    topLeft = Offset(w * 0.27f, h * 0.60f),
                    size = Size(w * 0.46f, h * 0.09f),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                )

                // Main stem
                drawLine(
                    color = Color(0xFF2F7D2A),
                    start = Offset(w * 0.50f, h * 0.60f),
                    end = Offset(w * 0.50f, h * 0.34f),
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Left Leaf
                val leftLeaf = Path().apply {
                    moveTo(w * 0.50f, h * 0.46f)
                    cubicTo(w * 0.36f, h * 0.46f, w * 0.28f, h * 0.36f, w * 0.28f, h * 0.24f)
                    cubicTo(w * 0.42f, h * 0.24f, w * 0.50f, h * 0.32f, w * 0.50f, h * 0.46f)
                    close()
                }
                drawPath(path = leftLeaf, color = Color(0xFF4FAE3A))

                // Right Leaf
                val rightLeaf = Path().apply {
                    moveTo(w * 0.50f, h * 0.40f)
                    cubicTo(w * 0.62f, h * 0.40f, w * 0.72f, h * 0.32f, w * 0.72f, h * 0.18f)
                    cubicTo(w * 0.58f, h * 0.18f, w * 0.50f, h * 0.28f, w * 0.50f, h * 0.40f)
                    close()
                }
                drawPath(path = rightLeaf, color = Color(0xFF6CC24A))
            }
            modelId.contains("barrel") || modelId.contains("rain") -> {
                // Rain Barrel: Cyan body with dark hoops and raindrop emblem (Matches HTML <rect x="24" y="26" width="52" height="60" ...>)
                drawRoundRect(
                    color = Color(0xFF2E86C1),
                    topLeft = Offset(w * 0.24f, h * 0.26f),
                    size = Size(w * 0.52f, h * 0.60f),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
                // Reinforcement hoops
                drawRect(
                    color = Color(0xFF1B5F8C),
                    topLeft = Offset(w * 0.22f, h * 0.38f),
                    size = Size(w * 0.56f, h * 0.05f)
                )
                drawRect(
                    color = Color(0xFF1B5F8C),
                    topLeft = Offset(w * 0.22f, h * 0.68f),
                    size = Size(w * 0.56f, h * 0.05f)
                )
                // Top cap
                drawRoundRect(
                    color = Color(0xFF1B5F8C),
                    topLeft = Offset(w * 0.28f, h * 0.20f),
                    size = Size(w * 0.44f, h * 0.09f),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
                // Spigot
                drawRoundRect(
                    color = Color(0xFF8B949B),
                    topLeft = Offset(w * 0.72f, h * 0.72f),
                    size = Size(w * 0.12f, h * 0.06f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                // Center raindrop
                val dropPath = Path().apply {
                    moveTo(w * 0.50f, h * 0.46f)
                    cubicTo(w * 0.44f, h * 0.53f, w * 0.41f, h * 0.57f, w * 0.41f, h * 0.61f)
                    cubicTo(w * 0.41f, h * 0.66f, w * 0.45f, h * 0.70f, w * 0.50f, h * 0.70f)
                    cubicTo(w * 0.55f, h * 0.70f, w * 0.59f, h * 0.66f, w * 0.59f, h * 0.61f)
                    close()
                }
                drawPath(path = dropPath, color = Color(0xFFBFE6FF))
            }
            modelId.contains("led") || modelId.contains("bulb") -> {
                // LED Bulb: Bright glowing yellow lamp dome with filament & base (Matches HTML <path d="M50 12a24 24 0 0 0-14 43..." ...>)
                val bulbDome = Path().apply {
                    moveTo(w * 0.50f, h * 0.12f)
                    cubicTo(w * 0.36f, h * 0.12f, w * 0.28f, h * 0.24f, w * 0.28f, h * 0.38f)
                    cubicTo(w * 0.28f, h * 0.48f, w * 0.34f, h * 0.56f, w * 0.38f, h * 0.66f)
                    lineTo(w * 0.62f, h * 0.66f)
                    cubicTo(w * 0.66f, h * 0.56f, w * 0.72f, h * 0.48f, w * 0.72f, h * 0.38f)
                    cubicTo(w * 0.72f, h * 0.24f, w * 0.64f, h * 0.12f, w * 0.50f, h * 0.12f)
                    close()
                }
                drawPath(path = bulbDome, color = Color(0xFFFFD54A))
                drawPath(
                    path = bulbDome,
                    color = Color(0xFFE0A800),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // Silver screw thread rings
                drawRoundRect(
                    color = Color(0xFF9AA3A8),
                    topLeft = Offset(w * 0.40f, h * 0.67f),
                    size = Size(w * 0.20f, h * 0.06f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0xFF7C868C),
                    topLeft = Offset(w * 0.42f, h * 0.75f),
                    size = Size(w * 0.16f, h * 0.06f),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
                // Bottom contact
                drawLine(
                    color = Color(0xFF555555),
                    start = Offset(w * 0.45f, h * 0.84f),
                    end = Offset(w * 0.55f, h * 0.84f),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Filament zig-zag
                val filament = Path().apply {
                    moveTo(w * 0.44f, h * 0.36f)
                    lineTo(w * 0.50f, h * 0.44f)
                    lineTo(w * 0.56f, h * 0.36f)
                }
                drawPath(
                    path = filament,
                    color = Color(0xFFE0A800),
                    style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            else -> {
                // Default Circular Eco Badge
                drawCircle(
                    color = EcoGreenPrimary.copy(alpha = 0.85f),
                    radius = w * 0.40f,
                    center = Offset(w / 2, h / 2)
                )
                drawCircle(
                    color = Color.White,
                    radius = w * 0.40f,
                    center = Offset(w / 2, h / 2),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}
