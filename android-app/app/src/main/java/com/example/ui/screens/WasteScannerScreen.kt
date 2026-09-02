package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ar.ArHitMarker
import com.example.data.model.EcoActionType
import com.example.data.model.WasteCategory
import com.example.data.model.WasteItem
import com.example.data.remote.gemini.AiSustainabilityProfileResult
import com.example.data.remote.gemini.DetectedObjectAnalysis
import com.example.ui.components.ArOverlayView
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DeepForestGreen
import com.example.ui.theme.EcoAmberWarm
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoGreenPrimaryDark
import com.example.ui.theme.EcoMintBorder
import com.example.ui.theme.EcoMintContainer
import com.example.ui.theme.EcoMintContainerLow
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.SoftPaleGreen
import kotlin.math.roundToInt

@Composable
fun WasteScannerScreen(
    items: List<WasteItem>,
    isScanningActive: Boolean,
    isAnalyzing: Boolean,
    scanErrorMessage: String? = null,
    selectedItem: WasteItem?,
    currentAiProfile: AiSustainabilityProfileResult? = null,
    capturedFrameBitmap: Bitmap? = null,
    searchQuery: String,
    activeFilter: WasteCategory?,
    showDetailDialog: Boolean,
    onSearchChanged: (String) -> Unit,
    onFilterSelected: (WasteCategory?) -> Unit,
    onSelectItem: (WasteItem) -> Unit,
    onTriggerScan: (Bitmap?, ArHitMarker?) -> Unit,
    onRetryScan: () -> Unit = {},
    onDismissError: () -> Unit = {},
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isArViewfinderExpanded by remember { mutableStateOf(false) }

    if (isArViewfinderExpanded) {
        // Fullscreen Immersive CameraX AR Scanning Mode
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF06180E))
                .testTag("fullscreen_camerax_scanner_view")
        ) {
            ArOverlayView(
                isAnalyzing = isAnalyzing,
                selectedItem = selectedItem,
                onTriggerScan = onTriggerScan,
                isExpandedMode = true,
                onToggleExpand = { isArViewfinderExpanded = false },
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("waste_scanner_screen")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("scanner_ar_tab_content"),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                // 1. Live AR Viewfinder Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(290.dp)
                        ) {
                            ArOverlayView(
                                isAnalyzing = isAnalyzing,
                                selectedItem = selectedItem,
                                onTriggerScan = onTriggerScan,
                                isExpandedMode = false,
                                onToggleExpand = { isArViewfinderExpanded = true }
                            )
                        }
                    }
                }

                // 2. Loading State Card during Gemini Vision processing
                if (isAnalyzing) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DeepForestGreen.copy(alpha = 0.95f),
                            border = BorderStroke(1.dp, AccentLime.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .testTag("scanner_loading_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = AccentLime,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Multimodal Vision Analysis...",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = LightCreamGreen
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Identifying items and generating circular sustainability recommendations with Gemini AI.",
                                        fontSize = 11.sp,
                                        color = SoftPaleGreen,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Error State Card with Retry Option
                if (scanErrorMessage != null && !isAnalyzing) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFEBEE),
                            border = BorderStroke(1.dp, Color(0xFFEF5350)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .testTag("scanner_error_banner")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ErrorOutline,
                                            contentDescription = "Error",
                                            tint = Color(0xFFC62828),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Scan Notice",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFFB71C1C)
                                        )
                                    }
                                    IconButton(
                                        onClick = onDismissError,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Dismiss Error",
                                            tint = Color(0xFF757575),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = scanErrorMessage,
                                    fontSize = 12.sp,
                                    color = Color(0xFF37474F),
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = onRetryScan,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("retry_scan_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Refresh,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Retry Scan",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Detected Objects from Gemini Multimodal Vision (if available)
                val detectedObjects = currentAiProfile?.detectedObjects.orEmpty()
                if (detectedObjects.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = EcoGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI Detected Objects (${detectedObjects.size})",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EcoMintContainer
                                ) {
                                    Text(
                                        text = "Gemini Multimodal",
                                        color = EcoGreenPrimaryDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Multimodal identification with actionable circular sustainability guidance:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Display each detected object separately
                    items(detectedObjects) { detectedObj ->
                        DetectedObjectResultCard(
                            detected = detectedObj,
                            onClick = {
                                val syntheticItem = WasteItem(
                                    id = "detected_${detectedObj.id}",
                                    name = detectedObj.name,
                                    category = detectedObj.category,
                                    primaryAction = detectedObj.recommendedAction,
                                    materialType = detectedObj.material,
                                    decompositionTime = detectedObj.decompositionTime.ifBlank { "Varies" },
                                    carbonFootprint = detectedObj.carbonFootprint.ifBlank { "Lifecycle impact" },
                                    recyclingBinType = detectedObj.recyclingBinType.ifBlank { "Sorting stream" },
                                    stepByStepGuide = detectedObj.suggestions,
                                    upcyclingIdeas = if (detectedObj.upcyclingIdea != null) listOf(detectedObj.upcyclingIdea) else detectedObj.suggestions,
                                    ecoFriendlyAlternative = "Reusable alternative",
                                    funFact = detectedObj.reason,
                                    arVisualBadge = "♻️ ${detectedObj.actionTitle}",
                                    sustainabilityProfileDescription = detectedObj.suggestions.joinToString("\n• ", prefix = "• "),
                                    recyclabilityScore = (detectedObj.confidence * 100).toInt().coerceIn(40, 99),
                                    isAiAnalyzed = true,
                                    aiModelUsed = "Gemini 3.5 Flash"
                                )
                                onSelectItem(syntheticItem)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // 5. Search & Category Filters
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scanner_search_input"),
                            placeholder = { Text("Search waste item (plastic, glass, paper...)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EcoGreenPrimary,
                                unfocusedBorderColor = EcoMintBorder,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = activeFilter == null,
                                    onClick = { onFilterSelected(null) },
                                    label = { Text("All (${items.size})", fontSize = 12.sp) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = activeFilter == null,
                                        borderColor = EcoMintBorder,
                                        selectedBorderColor = EcoGreenPrimary
                                    ),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EcoMintContainer,
                                        selectedLabelColor = EcoGreenPrimaryDark,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            items(WasteCategory.values()) { category ->
                                FilterChip(
                                    selected = activeFilter == category,
                                    onClick = { onFilterSelected(if (activeFilter == category) null else category) },
                                    label = { Text(category.displayName, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = activeFilter == category,
                                        borderColor = EcoMintBorder,
                                        selectedBorderColor = EcoGreenPrimary
                                    ),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EcoMintContainer,
                                        selectedLabelColor = EcoGreenPrimaryDark,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }

                // 6. Catalog Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Standard Waste Items",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${items.size} items",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 7. Waste Items List
                items(items) { item ->
                    CleanWasteItemCard(
                        item = item,
                        onClick = { onSelectItem(item) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    // Standard Inspection Dialog
    if (showDetailDialog && selectedItem != null) {
        val displayBitmap = capturedFrameBitmap ?: currentAiProfile?.capturedBitmap
        val aiProfile = currentAiProfile
        val detectedObjects = aiProfile?.detectedObjects.orEmpty()
        var selectedDetectedIndex by remember { mutableIntStateOf(0) }

        val profileDescription = selectedItem.sustainabilityProfileDescription
            ?: aiProfile?.sustainabilityDescription
            ?: "Sustainability Profile for ${selectedItem.name}:\n\n• Material & Lifecycle Impact: Composed of ${selectedItem.materialType}. In natural open environments, this item takes approximately ${selectedItem.decompositionTime} to decompose.\n\n• Carbon Footprint & Energy: Manufacturing generates ${selectedItem.carbonFootprint}. Diverting it to certified circular sorting loops reduces lifecycle greenhouse gas emissions significantly.\n\n• Circular Recovery: High recycling value when placed in ${selectedItem.recyclingBinType}."

        val primaryBadgeColor = Color(selectedItem.primaryAction.badgeColorHex)

        AlertDialog(
            onDismissRequest = onDismissDialog,
            modifier = Modifier.testTag("scan_detail_dialog"),
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = primaryBadgeColor
                    ) {
                        Text(
                            text = selectedItem.primaryAction.title.uppercase(),
                            color = LightCreamGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = selectedItem.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            },
            text = {
                val dialogScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(dialogScrollState)
                ) {
                    if (displayBitmap != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, EcoMintBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    bitmap = displayBitmap.asImageBitmap(),
                                    contentDescription = "AR Captured Frame",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "📷 Multimodal Vision Frame",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Confidence and Recognition Warning if low confidence
                    if (aiProfile != null && (!aiProfile.isConfident || aiProfile.confidenceLevel < 0.65f)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EcoAmberWarm.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, EcoAmberWarm.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "⚠️", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = aiProfile.lowConfidenceReason ?: "Low optical match confidence. Ambiguous features detected — please verify product material markings before sorting.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Multi-object selector if multiple objects are detected in the image
                    if (detectedObjects.size > 1) {
                        Text(
                            text = "Detected Objects (${detectedObjects.size}):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(detectedObjects.indices.toList()) { idx ->
                                val obj = detectedObjects[idx]
                                val isSelected = idx == selectedDetectedIndex
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) EcoGreenPrimary else EcoMintContainerLow,
                                    border = BorderStroke(1.dp, if (isSelected) EcoGreenPrimary else EcoMintBorder),
                                    modifier = Modifier.clickable { selectedDetectedIndex = idx }
                                ) {
                                    Text(
                                        text = "${obj.name} (${obj.actionTitle})",
                                        color = if (isSelected) LightCreamGreen else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Active detected object card
                        val activeObj = detectedObjects.getOrNull(selectedDetectedIndex)
                        if (activeObj != null) {
                            val actionColor = getActionColor(activeObj.recommendedAction)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EcoMintContainerLow,
                                border = BorderStroke(1.dp, actionColor.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = activeObj.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = actionColor
                                        ) {
                                            Text(
                                                text = activeObj.actionTitle.uppercase(),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Material: ${activeObj.material} (${activeObj.category.displayName}) • ${(activeObj.confidence * 100).roundToInt()}% Match",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Why: ${activeObj.reason}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = EcoGreenPrimaryDark
                                    )
                                    if (activeObj.suggestions.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Practical Suggestions:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        activeObj.suggestions.forEach { suggestion ->
                                            Text(
                                                text = "• $suggestion",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    // 1. Detected Physical Components Breakdown
                    if (aiProfile != null && aiProfile.detectedComponents.isNotEmpty()) {
                        Text(
                            text = "Recognized Components & Separation:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        aiProfile.detectedComponents.forEach { component ->
                            val compColor = when (component.actionType.uppercase()) {
                                "REDUCE" -> Color(0xFFFFA726)
                                "REUSE" -> Color(0xFF0288D1)
                                "REPAIR" -> Color(0xFF7B1FA2)
                                "SPECIAL", "SPECIAL_DISPOSAL" -> Color(0xFFC62828)
                                else -> EcoGreenPrimary
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EcoMintContainerLow,
                                border = BorderStroke(1.dp, compColor.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = component.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = compColor.copy(alpha = 0.18f)
                                        ) {
                                            Text(
                                                text = component.actionLabel.ifBlank { component.actionType },
                                                color = compColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Material: ${component.material}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (component.separationNotes.isNotBlank()) {
                                        Text(
                                            text = "Separation: ${component.separationNotes}",
                                            fontSize = 11.sp,
                                            color = EcoGreenPrimaryDark,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 2. Structured 3R Method Guidance (Reduce, Reuse, Recycle, Repair, Special Disposal)
                    val guidance = aiProfile?.threeRGuidance
                    if (guidance != null) {
                        Text(
                            text = "3R Action Plan:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // REDUCE CARD
                        if (guidance.reduce.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFF8E1),
                                border = BorderStroke(1.dp, Color(0xFFFFB300)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "🔻 REDUCE Strategy",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFFE65100)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = guidance.reduce,
                                        fontSize = 11.sp,
                                        color = Color(0xFF3E2723),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        // REUSE CARD
                        if (guidance.reuse.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFE1F5FE),
                                border = BorderStroke(1.dp, Color(0xFF0288D1)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "🔄 REUSE & Upcycling",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF01579B)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = guidance.reuse,
                                        fontSize = 11.sp,
                                        color = Color(0xFF0D47A1),
                                        lineHeight = 15.sp
                                    )
                                    if (selectedItem.upcyclingIdeas.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        selectedItem.upcyclingIdeas.forEach { idea ->
                                            Text(
                                                text = "💡 $idea",
                                                fontSize = 10.sp,
                                                color = Color(0xFF0277BD)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // RECYCLE CARD
                        if (guidance.recycle.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EcoMintContainer,
                                border = BorderStroke(1.dp, EcoGreenPrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "♻️ RECYCLE Stream (${selectedItem.recyclingBinType})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = EcoGreenPrimaryDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = guidance.recycle,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                    if (selectedItem.stepByStepGuide.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        selectedItem.stepByStepGuide.forEach { prep ->
                                            Text(
                                                text = "• $prep",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // REPAIR CARD (If available)
                        if (!guidance.repair.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF3E5F5),
                                border = BorderStroke(1.dp, Color(0xFF7B1FA2)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "🔧 REPAIR & Maintenance",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF6A1B9A)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = guidance.repair,
                                        fontSize = 11.sp,
                                        color = Color(0xFF4A148C),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        // SPECIAL DISPOSAL CARD (If available or hazardous)
                        if (!guidance.specialDisposal.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFEBEE),
                                border = BorderStroke(1.dp, Color(0xFFE53935)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "⚠️ SPECIAL HAZARDOUS DISPOSAL",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFFB71C1C)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = guidance.specialDisposal,
                                        fontSize = 11.sp,
                                        color = Color(0xFFB71C1C),
                                        lineHeight = 15.sp
                                    )
                                    if (!guidance.safetyWarning.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Hazard Notice: ${guidance.safetyWarning}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC62828)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Gemini AI Sustainability Profile Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EcoMintContainerLow,
                        border = BorderStroke(1.dp, EcoMintBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = EcoGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Gemini Sustainability Profile",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EcoGreenPrimaryDark
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = profileDescription,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Precision Specs
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EcoMintContainer,
                        border = BorderStroke(1.dp, EcoMintBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔬 Match: ${((aiProfile?.confidenceLevel ?: 0.98f) * 100).toInt()}% Precision",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimaryDark
                                )
                                Text(
                                    text = "Score: ${selectedItem.recyclabilityScore}/100",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⏱️ Decay duration: ${selectedItem.decompositionTime}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "📦 Material: ${selectedItem.materialType}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "🌱 Carbon footprint: ${selectedItem.carbonFootprint}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "🗑️ Recommended stream: ${selectedItem.recyclingBinType}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (selectedItem.funFact.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EcoAmberWarm.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, EcoAmberWarm.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(text = "✨", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = selectedItem.funFact,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("dialog_done_button")
                ) {
                    Text(
                        text = "Done",
                        fontWeight = FontWeight.Bold,
                        color = LightCreamGreen,
                        fontSize = 13.sp
                    )
                }
            }
        )
    }
}

/**
 * Dedicated Card for displaying individual detected objects from Gemini Vision
 */
@Composable
private fun DetectedObjectResultCard(
    detected: DetectedObjectAnalysis,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actionColor = getActionColor(detected.recommendedAction)
    val isUncertain = !detected.isConfident || detected.confidence < 0.65f

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("detected_object_card_${detected.name.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, if (isUncertain) EcoAmberWarm else actionColor.copy(alpha = 0.6f)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Name, Confidence, and Recommended Action Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(actionColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getActionIcon(detected.recommendedAction),
                            contentDescription = null,
                            tint = actionColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = detected.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${detected.material} • ${detected.category.displayName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = actionColor
                ) {
                    Text(
                        text = detected.actionTitle.uppercase(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Confidence Level Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Match Confidence: ${(detected.confidence * 100).roundToInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUncertain) EcoAmberWarm else EcoGreenPrimaryDark
                )
                LinearProgressIndicator(
                    progress = { detected.confidence.coerceIn(0f, 1f) },
                    color = if (isUncertain) EcoAmberWarm else EcoGreenPrimary,
                    trackColor = EcoMintContainerLow,
                    modifier = Modifier
                        .width(90.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
            }

            // Low Confidence Ambiguity Warning Notice
            if (isUncertain) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoAmberWarm.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, EcoAmberWarm.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚠️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Uncertain identification. Ambiguous image features detected — please verify physical recycling marks on the item before disposal.",
                            fontSize = 10.sp,
                            color = Color(0xFF5D4037),
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Reason for Recommendation
            if (detected.reason.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoMintContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Why: ${detected.reason}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        lineHeight = 15.sp
                    )
                }
            }

            // Practical & Creative Suggestions
            if (detected.suggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Suggestions & Action Items:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                detected.suggestions.take(3).forEach { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• ",
                            fontSize = 11.sp,
                            color = actionColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = suggestion,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Footer Tags: Bin Type & Decomposition
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (detected.recyclingBinType.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EcoMintContainer
                    ) {
                        Text(
                            text = "🗑️ ${detected.recyclingBinType}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimaryDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                if (detected.decompositionTime.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EcoMintContainerLow
                    ) {
                        Text(
                            text = "⏱️ ${detected.decompositionTime}",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CleanWasteItemCard(
    item: WasteItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("waste_item_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, EcoMintBorder.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(item.primaryAction.badgeColorHex).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.primaryAction) {
                        EcoActionType.RECYCLE -> Icons.Filled.Recycling
                        EcoActionType.REUSE -> Icons.Filled.Loop
                        EcoActionType.REDUCE -> Icons.Filled.Spa
                        EcoActionType.COMPOST -> Icons.Filled.Spa
                        EcoActionType.DONATE -> Icons.Filled.Favorite
                        EcoActionType.REPAIR -> Icons.Filled.Build
                        else -> Icons.Filled.DeleteSweep
                    },
                    contentDescription = null,
                    tint = Color(item.primaryAction.badgeColorHex),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${item.category.displayName} • ${item.decompositionTime}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(item.primaryAction.badgeColorHex).copy(alpha = 0.15f)
            ) {
                Text(
                    text = item.primaryAction.title,
                    color = Color(item.primaryAction.badgeColorHex),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

private fun getActionColor(action: EcoActionType): Color {
    return when (action) {
        EcoActionType.REUSE -> Color(0xFF0288D1)
        EcoActionType.REPAIR -> Color(0xFF7B1FA2)
        EcoActionType.DONATE -> Color(0xFFE65100)
        EcoActionType.RECYCLE -> Color(0xFF2E7D32)
        EcoActionType.COMPOST -> Color(0xFF558B2F)
        EcoActionType.DISPOSE -> Color(0xFF757575)
        EcoActionType.SPECIAL_DISPOSAL -> Color(0xFFC62828)
        EcoActionType.REDUCE -> Color(0xFFFF8F00)
        EcoActionType.REPLACE -> Color(0xFF6A1B9A)
    }
}

private fun getActionIcon(action: EcoActionType) = when (action) {
    EcoActionType.REUSE -> Icons.Filled.Loop
    EcoActionType.REPAIR -> Icons.Filled.Build
    EcoActionType.DONATE -> Icons.Filled.Favorite
    EcoActionType.RECYCLE -> Icons.Filled.Recycling
    EcoActionType.COMPOST -> Icons.Filled.Spa
    EcoActionType.DISPOSE -> Icons.Filled.DeleteSweep
    else -> Icons.Filled.AutoAwesome
}
