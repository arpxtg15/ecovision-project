package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ActionStep
import com.example.data.model.SustainabilitySolution
import com.example.ui.theme.DarkText
import com.example.ui.theme.EcoAmberWarm
import com.example.ui.theme.EcoCoralAlert
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoGreenPrimaryDark
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.EcoMintBorder
import com.example.ui.theme.EcoMintContainer
import com.example.ui.theme.EcoMintContainerLow
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.SoftPaleGreen
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SolutionsScreen(
    solutions: List<SustainabilitySolution>,
    selectedSolution: SustainabilitySolution?,
    showDetailDialog: Boolean,
    calculatorInputValue: Double = 3.0,
    calculatedAnnualSavings: Double = 0.0,
    onUpdateCalculatorInput: (Double) -> Unit = {},
    onSelectSolution: (SustainabilitySolution) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val filteredSolutions = remember(solutions, searchQuery, selectedCategoryFilter) {
        solutions.filter { solution ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                solution.title.contains(searchQuery, ignoreCase = true) ||
                solution.subtitle.contains(searchQuery, ignoreCase = true) ||
                solution.problemStatement.contains(searchQuery, ignoreCase = true) ||
                solution.keyActionSteps.any { it.title.contains(searchQuery, ignoreCase = true) }
            }
            val matchesCategory = if (selectedCategoryFilter == null) {
                true
            } else {
                solution.id.equals(selectedCategoryFilter, ignoreCase = true) ||
                solution.title.contains(selectedCategoryFilter ?: "", ignoreCase = true)
            }
            matchesSearch && matchesCategory
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("solutions_screen_scroll"),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        // 1. Clean Modern Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sustainability Solutions",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Actionable climate roadmaps, circular swaps, and impact calculations",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // 2. Search & Category Filters
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search solutions, plastics, water, food...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoGreenPrimary,
                        unfocusedBorderColor = EcoMintBorder,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("solutions_search_bar")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All Pillars (${solutions.size})", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EcoGreenPrimary,
                                selectedLabelColor = LightCreamGreen,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(1.dp, if (selectedCategoryFilter == null) EcoGreenPrimary else EcoMintBorder),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    items(solutions) { sol ->
                        val isSelected = selectedCategoryFilter == sol.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategoryFilter = if (isSelected) null else sol.id
                            },
                            label = { Text("${sol.categoryIcon} ${sol.title.split(" ").first()}", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EcoGreenPrimary,
                                selectedLabelColor = LightCreamGreen,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(1.dp, if (isSelected) EcoGreenPrimary else EcoMintBorder),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // 3. Section Title with Count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore Climate Pillars",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${filteredSolutions.size} available",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 4. Solutions Cards List
        if (filteredSolutions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, EcoMintBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🔍", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matching solutions found",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Try searching for a different environmental pillar or clear filters",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredSolutions) { solution ->
                CleanSolutionCard(
                    solution = solution,
                    onClick = { onSelectSolution(solution) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                )
            }
        }
    }

    // Comprehensive Solution Detail Dialog with Rich Information Architecture
    if (showDetailDialog && selectedSolution != null) {
        SolutionDetailDialog(
            solution = selectedSolution,
            calculatorInputValue = calculatorInputValue,
            calculatedAnnualSavings = calculatedAnnualSavings,
            onUpdateCalculatorInput = onUpdateCalculatorInput,
            onDismiss = onDismissDialog
        )
    }
}

@Composable
private fun CleanSolutionCard(
    solution: SustainabilitySolution,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("solution_card_${solution.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, EcoMintBorder.copy(alpha = 0.85f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category Icon Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(EcoMintContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = solution.categoryIcon,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = solution.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = solution.subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        lineHeight = 16.sp
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Solution Details",
                    tint = EcoGreenPrimary,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoMintContainerLow,
                    border = BorderStroke(1.dp, EcoMintBorder)
                ) {
                    Text(
                        text = "🛠️ ${solution.keyActionSteps.size} Action Steps",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoGreenPrimaryDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SoftPaleGreen.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, EcoMintBorder)
                ) {
                    Text(
                        text = "🔄 ${solution.practicalAlternatives.size} Swaps",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoGreenSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Tap for Roadmap",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SolutionDetailDialog(
    solution: SustainabilitySolution,
    calculatorInputValue: Double,
    calculatedAnnualSavings: Double,
    onUpdateCalculatorInput: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val dialogScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solution_detail_dialog"),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EcoMintContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = solution.categoryIcon, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = solution.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = solution.subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .verticalScroll(dialogScrollState)
            ) {
                // Section 1: The Challenge (Problem Overview)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EcoCoralAlert.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, EcoCoralAlert.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚠️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "The Environmental Challenge",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = EcoCoralAlert
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = solution.problemStatement,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📊", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = solution.alarmingStat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF991B1B),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Section 2: Global Target & SDG Alignment
                if (solution.globalTarget.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = EcoMintContainerLow,
                        border = BorderStroke(1.dp, EcoMintBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(EcoGreenPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.TrackChanges,
                                    contentDescription = null,
                                    tint = EcoGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Global Policy & SDG Target",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimaryDark
                                )
                                Text(
                                    text = solution.globalTarget,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Section 3: Interactive Annual Impact Calculator
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, EcoMintBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Calculate,
                                contentDescription = null,
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Personal Annual Impact Calculator",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = solution.calculatorMetric,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Stepper Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EcoMintContainerLow,
                                border = BorderStroke(1.dp, EcoMintBorder),
                                modifier = Modifier.clickable {
                                    if (calculatorInputValue > 1.0) {
                                        onUpdateCalculatorInput(calculatorInputValue - 1.0)
                                    }
                                }
                            ) {
                                Box(
                                    modifier = Modifier.size(36.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Remove,
                                        contentDescription = "Decrease",
                                        tint = EcoGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = "${calculatorInputValue.toInt()} ${solution.unitName}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimaryDark
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EcoMintContainerLow,
                                border = BorderStroke(1.dp, EcoMintBorder),
                                modifier = Modifier.clickable {
                                    if (calculatorInputValue < 50.0) {
                                        onUpdateCalculatorInput(calculatorInputValue + 1.0)
                                    }
                                }
                            ) {
                                Box(
                                    modifier = Modifier.size(36.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Increase",
                                        tint = EcoGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calculated Annual Result Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EcoGreenPrimary.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "✨", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Your Projected Annual Environmental Savings:",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = String.format(Locale.US, "%.1f %s saved / year", calculatedAnnualSavings, solution.unitName),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreenPrimaryDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 4: Practical Action Steps Roadmap
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Practical Action Roadmap (${solution.keyActionSteps.size} Steps)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                solution.keyActionSteps.forEachIndexed { index, step ->
                    ActionStepCard(step = step, stepNumber = index + 1)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Section 5: Swaps & Zero-Waste Replacements
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.SwapHoriz,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Smart Circular Swaps (Avoid → Alternative)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                solution.practicalAlternatives.forEach { (bad, good) ->
                    SwapAlternativeCard(bad = bad, good = good)
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Section 6: Community Ripple Effect
                if (solution.communityImpactTip.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EcoAmberWarm.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, EcoAmberWarm.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Groups,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Community Ripple Effect",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = solution.communityImpactTip,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("solution_done_button")
            ) {
                Text(
                    text = "Commit to Action",
                    fontWeight = FontWeight.Bold,
                    color = LightCreamGreen,
                    fontSize = 13.sp
                )
            }
        }
    )
}

@Composable
private fun ActionStepCard(
    step: ActionStep,
    stepNumber: Int
) {
    val difficultyColor = when (step.difficulty.lowercase(Locale.ROOT)) {
        "easy" -> EcoGreenPrimary
        "medium" -> EcoAmberWarm
        else -> Color(0xFF7C3AED)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = EcoMintContainerLow,
        border = BorderStroke(1.dp, EcoMintBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(11.dp)) {
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
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(EcoGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$stepNumber",
                            color = LightCreamGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = difficultyColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, difficultyColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = step.difficulty,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = difficultyColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = step.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            if (step.impactRating.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⚡ Impact: ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimaryDark
                    )
                    Text(
                        text = step.impactRating,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoGreenSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SwapAlternativeCard(
    bad: String,
    good: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, EcoMintBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Conventional Item to Avoid
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EcoCoralAlert.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, EcoCoralAlert.copy(alpha = 0.2f)),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "❌ $bad",
                    fontSize = 11.sp,
                    color = Color(0xFF991B1B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    lineHeight = 14.sp
                )
            }

            Box(
                modifier = Modifier.padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Swap To",
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Eco Alternative
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EcoMintContainerLow,
                border = BorderStroke(1.dp, EcoMintBorder),
                modifier = Modifier.weight(1.15f)
            ) {
                Text(
                    text = "✅ $good",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EcoGreenPrimaryDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    lineHeight = 14.sp
                )
            }
        }
    }
}
