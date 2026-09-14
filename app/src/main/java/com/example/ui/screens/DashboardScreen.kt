package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ScanHistoryEntity
import com.example.data.local.UserEcoProfileEntity
import com.example.data.model.DayActivity
import com.example.data.model.EcoBadge
import com.example.data.model.EcoTip
import com.example.data.model.QuickEcoAction
import com.example.data.model.SustainabilityFact
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DarkText
import com.example.ui.theme.DeepForestGreen
import com.example.ui.theme.EcoCoralAlert
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoGreenPrimaryDark
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.EcoMintBorder
import com.example.ui.theme.EcoMintContainer
import com.example.ui.theme.EcoMintContainerLow
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.LightGreenBackground
import com.example.ui.theme.NatureGreen
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SoftPaleGreen
import com.example.ui.viewmodel.HomeQuizUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    userProfile: UserEcoProfileEntity,
    recentScans: List<ScanHistoryEntity>,
    dailyTip: EcoTip?,
    dailyFact: SustainabilityFact?,
    quizState: HomeQuizUiState,
    badges: List<EcoBadge>,
    weeklyActivity: List<DayActivity>,
    quickActions: List<QuickEcoAction>,
    homeFeedback: String?,
    onCycleFact: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToSolutions: () -> Unit,
    onNavigateToTips: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onQuickScanItem: () -> Unit,
    onCompleteTip: (EcoTip) -> Unit,
    onLogQuickAction: (QuickEcoAction) -> Unit,
    onSubmitQuizAnswer: (Int) -> Unit,
    onCycleNextQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showQuickActionDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen_scroll"),
        contentPadding = PaddingValues(bottom = 84.dp)
    ) {
        // 1. Toast Feedback Banner (if active)
        if (homeFeedback != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = homeFeedback,
                            color = EcoGreenPrimaryDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // 2. Header & Eco Journey Overview Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Greeting & Eco Date Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, ${userProfile.userName} 👋",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Your daily eco-footprint dashboard",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, EcoMintBorder),
                        shadowElevation = 2.dp,
                        modifier = Modifier.testTag("eco_hero_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = "🌱", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Eco Active",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Unified 3-Metric Impact Row
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, EcoMintBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 14.dp, horizontal = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CleanImpactMetric(
                                icon = Icons.Filled.Spa,
                                iconTint = EcoGreenPrimary,
                                value = "%.2f kg".format(userProfile.totalPlasticSavedKg),
                                label = "Plastic Diverted"
                            )
                            Box(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(1.dp)
                                    .background(EcoMintBorder)
                            )
                            CleanImpactMetric(
                                icon = Icons.Filled.WaterDrop,
                                iconTint = EcoGreenSecondary,
                                value = "%.0f L".format(userProfile.totalWaterSavedLiters),
                                label = "Water Saved"
                            )
                            Box(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(1.dp)
                                    .background(EcoMintBorder)
                            )
                            CleanImpactMetric(
                                icon = Icons.Filled.Nature,
                                iconTint = EcoGreenPrimaryDark,
                                value = "%.1f kg".format(userProfile.totalCo2OffsetKg),
                                label = "CO₂ Prevented"
                            )
                        }
                    }
                }
            }
        }

        // 3. Action Center: Hero Scanner Banner & Quick Log Buttons
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                // Hero Banner
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToScanner() }
                        .testTag("hero_scan_card"),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.5.dp, EcoMintBorder),
                    shadowElevation = 3.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        EcoGreenPrimaryDark,
                                        EcoGreenPrimary
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(AccentLime),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.QrCodeScanner,
                                        contentDescription = null,
                                        tint = EcoGreenPrimaryDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "AR Waste Scanner",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LightCreamGreen
                                    )
                                    Text(
                                        text = "Identify materials & 4R sorting rules",
                                        fontSize = 12.sp,
                                        color = EcoMintContainerLow
                                    )
                                }
                            }

                            Button(
                                onClick = onNavigateToScanner,
                                colors = ButtonDefaults.buttonColors(containerColor = AccentLime),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Scan",
                                    color = EcoGreenPrimaryDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        onClick = { showQuickActionDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.5f)),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("log_quick_action_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Quick Action Log",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )
                        }
                    }

                    Surface(
                        onClick = onQuickScanItem,
                        shape = RoundedCornerShape(14.dp),
                        color = EcoMintContainerLow.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, EcoMintBorder),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("simulate_scan_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = EcoGreenPrimaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Simulate Scan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimaryDark
                            )
                        }
                    }
                }
            }
        }

        // 4. Interactive Daily Climate IQ Mini-Quiz
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                CleanClimateQuizCard(
                    quizState = quizState,
                    onSelectOption = onSubmitQuizAnswer,
                    onCycleNext = onCycleNextQuiz
                )
            }
        }

        // 5. Weekly Eco Activity Breakdown
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, EcoMintBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(15.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Weekly Eco Activity",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Consistent habit progress",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            weeklyActivity.forEach { day ->
                                val maxCount = 100f
                                val barHeightFraction = (day.activityCount / maxCount).coerceIn(0.15f, 1f)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height((barHeightFraction * 50).dp)
                                            .width(22.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (day.isToday) EcoGreenPrimary
                                                else EcoMintContainerLow
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = day.dayLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (day.isToday) EcoGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Milestone Badges Showcase
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Eco Milestone Badges",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${badges.count { it.isUnlocked }}/${badges.size} Unlocked",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoGreenPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(badges) { badge ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else EcoMintContainerLow.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, EcoMintBorder),
                            shadowElevation = if (badge.isUnlocked) 2.dp else 0.dp,
                            modifier = Modifier
                                .width(120.dp)
                                .testTag("badge_${badge.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (badge.isUnlocked) EcoMintContainer
                                            else EcoMintContainerLow.copy(alpha = 0.6f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (badge.isUnlocked) {
                                        Text(text = badge.iconEmoji, fontSize = 20.sp)
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.Lock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = badge.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = badge.progressLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (badge.isUnlocked) EcoGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Daily Sustainability Fact Card
        if (dailyFact != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    CleanDailyFactCard(
                        fact = dailyFact,
                        onCycleNext = onCycleFact,
                        onShare = { factToShare ->
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "🌍 *EcoVision Daily Climate Fact*:\n\n" +
                                            "💡 ${factToShare.fact}\n\n" +
                                            "📊 *Impact*: ${factToShare.globalImpact}\n" +
                                            "⚡ *Action*: ${factToShare.takeawayAction}\n\n" +
                                            "— Source: ${factToShare.source} via EcoVision"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Daily Fact"))
                        }
                    )
                }
            }
        }

        // 8. Quick Daily Habit Spotlight (if available)
        if (dailyTip != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, EcoMintBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EcoMintContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = dailyTip.context.iconEmoji, fontSize = 18.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TODAY'S HABIT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = dailyTip.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = dailyTip.impactSnippet,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = { onCompleteTip(dailyTip) },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                border = BorderStroke(1.dp, EcoGreenPrimary)
                            ) {
                                Text(
                                    text = "Done",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 9. Explore Sustainable Pillars
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Explore & Act",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CleanFeatureTile(
                        icon = Icons.Filled.Public,
                        iconTint = EcoGreenPrimary,
                        title = "Global Solutions",
                        subtitle = "Climate & 4R Pillars",
                        onClick = onNavigateToSolutions,
                        modifier = Modifier.weight(1f)
                    )

                    CleanFeatureTile(
                        icon = Icons.Filled.Lightbulb,
                        iconTint = EcoGreenSecondary,
                        title = "Eco Habits & Tips",
                        subtitle = "Daily Sprints & Tips",
                        onClick = onNavigateToTips,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 10. Recent Scans Log
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Scans",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (recentScans.isNotEmpty()) {
                        Text(
                            text = "${recentScans.size} items",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (recentScans.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, EcoMintBorder),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "No scans yet",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap 'Scan' above to analyze your first item",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        items(recentScans.take(3)) { scan ->
            CleanScanRow(
                scan = scan,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 3.dp)
            )
        }
    }

    // Quick Action Dialog
    if (showQuickActionDialog) {
        AlertDialog(
            onDismissRequest = { showQuickActionDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ Log Quick Eco Action",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { showQuickActionDialog = false },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tap any action you completed today to boost your score & impact:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    quickActions.forEach { action ->
                        Surface(
                            onClick = {
                                onLogQuickAction(action)
                                showQuickActionDialog = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = EcoMintContainerLow.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, EcoMintBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_action_${action.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = action.iconEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = action.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = action.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EcoMintContainer,
                                    border = BorderStroke(1.dp, EcoMintBorder)
                                ) {
                                    Text(
                                        text = "Action",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreenPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun CleanImpactMetric(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(EcoMintContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CleanClimateQuizCard(
    quizState: HomeQuizUiState,
    onSelectOption: (Int) -> Unit,
    onCycleNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val q = quizState.currentQuestion
    val isSubmitted = quizState.selectedOptionIndex != null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("climate_quiz_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, EcoMintBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EcoMintContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Quiz,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DAILY CLIMATE QUIZ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = q.category,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(
                    onClick = onCycleNext,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("next_quiz_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Autorenew,
                        contentDescription = "Cycle Question",
                        tint = EcoGreenPrimaryDark.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = q.question,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Options List
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                q.options.forEachIndexed { index, option ->
                    val isSelected = quizState.selectedOptionIndex == index
                    val isCorrectAnswer = isSubmitted && index == q.correctIndex
                    val isIncorrectChoice = isSubmitted && isSelected && !isCorrectAnswer

                    val cardBackground = when {
                        isCorrectAnswer -> EcoMintContainer
                        isIncorrectChoice -> EcoCoralAlert.copy(alpha = 0.15f)
                        isSelected -> EcoMintContainer
                        else -> EcoMintContainerLow.copy(alpha = 0.45f)
                    }

                    val borderColor = when {
                        isCorrectAnswer -> EcoGreenPrimary
                        isIncorrectChoice -> EcoCoralAlert
                        isSelected -> EcoGreenPrimary
                        else -> EcoMintBorder
                    }

                    val textColor = when {
                        isCorrectAnswer -> EcoGreenPrimaryDark
                        isIncorrectChoice -> EcoCoralAlert
                        isSelected -> EcoGreenPrimaryDark
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Surface(
                        onClick = { if (!isSubmitted) onSelectOption(index) },
                        shape = RoundedCornerShape(12.dp),
                        color = cardBackground,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_option_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${('A' + index)}.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected || isCorrectAnswer) FontWeight.SemiBold else FontWeight.Normal,
                                    color = textColor
                                )
                            }

                            if (isCorrectAnswer) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Correct",
                                    tint = EcoGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else if (isIncorrectChoice) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Incorrect",
                                    tint = EcoCoralAlert,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Explanation & Result Callout
            if (quizState.showExplanation) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (quizState.isCorrect) EcoMintContainer else EcoMintContainerLow.copy(alpha = 0.5f),
                    border = BorderStroke(
                        1.dp,
                        if (quizState.isCorrect) EcoGreenPrimary.copy(alpha = 0.5f) else EcoMintBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (quizState.isCorrect) "🎉 Correct! Well done!" else "💡 Climate Insight:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (quizState.isCorrect) EcoGreenPrimary else EcoGreenPrimaryDark
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = q.explanation,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CleanDailyFactCard(
    fact: SustainabilityFact,
    onCycleNext: () -> Unit,
    onShare: (SustainabilityFact) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_sustainability_fact_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, EcoMintBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EcoMintContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = fact.iconEmoji, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DAILY FACT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = fact.topic,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onCycleNext,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("next_fact_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Autorenew,
                            contentDescription = "Next Fact",
                            tint = EcoGreenPrimaryDark.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { onShare(fact) },
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("share_fact_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = EcoGreenPrimaryDark.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = fact,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "fact_text"
            ) { targetFact ->
                Column {
                    Text(
                        text = targetFact.fact,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EcoMintContainer,
                        border = BorderStroke(1.dp, EcoMintBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = targetFact.takeawayAction,
                                fontSize = 12.sp,
                                color = EcoGreenPrimaryDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CleanFeatureTile(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, EcoMintBorder),
        shadowElevation = 2.dp,
        modifier = modifier.height(90.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EcoMintContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CleanScanRow(
    scan: ScanHistoryEntity,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(scan.timestamp))

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, EcoMintBorder),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(EcoMintContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = scan.itemName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "${scan.category} • $dateStr",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EcoMintContainer,
                border = BorderStroke(1.dp, EcoMintBorder)
            ) {
                Text(
                    text = scan.actionType,
                    color = EcoGreenPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
