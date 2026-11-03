package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HomeWork
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
    onNavigateToVirtualHub: () -> Unit,
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
                    color = Color(0xF2FFFFFF),
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.95f),
                                EcoGreenPrimary.copy(alpha = 0.4f)
                            )
                        )
                    ),
                    shadowElevation = 3.dp,
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

        // 2. Hero Scenic AR Banner Card (Matches Mockup)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToScanner() }
                    .testTag("hero_scan_card"),
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(195.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_nature_ar_hero_1791177539002),
                        contentDescription = "Get the Best Out of Nature",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Soft semi-transparent left scrim for high readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xF5FFFFFF),
                                        Color(0xCCFFFFFF),
                                        Color(0x66FFFFFF),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, top = 20.dp, bottom = 18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Get the Best\nOut of Nature",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F3B2C),
                                lineHeight = 28.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Scan. Learn. Take Action.\nFor a Greener Tomorrow.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1D4A3B),
                                lineHeight = 16.sp
                            )
                        }

                        // Small leaf icon + line
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = null,
                                tint = Color(0xFF288B57),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .width(42.dp)
                                    .height(2.5.dp)
                                    .background(Color(0xFF288B57), RoundedCornerShape(1.5.dp))
                            )
                        }
                    }
                }
            }
        }

        // 3. Two Feature Cards Side-by-Side: AR Scanner & Sustainability Solutions (Matches Mockup)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: AR Scanner (Pale Mint)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFDDF3E4),
                    modifier = Modifier
                        .weight(1f)
                        .height(210.dp)
                        .clickable { onNavigateToScanner() }
                        .testTag("ar_scanner_feature_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // AR Reticle with leaf
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .border(2.dp, Color(0xFF135133), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = null,
                                tint = Color(0xFF135133),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AR Scanner",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F3B2A),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Scan everyday objects and see their real impact.",
                                fontSize = 11.5.sp,
                                color = Color(0xFF386050),
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }

                        // Circular dark green action button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF135133)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open AR Scanner",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Card 2: Sustainability Solutions (Pale Sky Teal)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFD7EEF2),
                    modifier = Modifier
                        .weight(1f)
                        .height(210.dp)
                        .clickable { onNavigateToSolutions() }
                        .testTag("sustainability_solutions_feature_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // House icon with leaf container
                        Box(
                            modifier = Modifier.size(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.HomeWork,
                                contentDescription = null,
                                tint = Color(0xFF134C58),
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Sustainability\nSolutions",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0E3842),
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Explore tips and solutions for a greener planet.",
                                fontSize = 11.5.sp,
                                color = Color(0xFF3B646F),
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }

                        // Circular dark teal action button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF134C58)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open Solutions",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3b. Featured Virtual Hub AR Card (Matches HTML Virtual Hub entry)
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF1F8EE),
                border = BorderStroke(1.dp, Color(0xFFCBE8C5)),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToVirtualHub() }
                    .testTag("virtual_hub_feature_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE0F2D8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏡", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Virtual Hub",
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F3B2A)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = EcoGreenPrimary
                            ) {
                                Text(
                                    text = "AR SPACE",
                                    color = Color.White,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Place solar panels, compost bins and plants in your space.",
                            fontSize = 12.sp,
                            color = Color(0xFF386050),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF135133)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Virtual Hub",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 4. Horizontal Card: Today's Eco Tip (Matches Mockup)
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFEFF7F0),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToTips() }
                    .testTag("today_eco_tip_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular glowing lightbulb container
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDDF4E3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF238350),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Today's Eco Tip",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B6B44)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dailyTip?.title ?: "Use a reusable water bottle and reduce single-use plastic.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF385E4D),
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Decorative fresh leaves illustration
                    Image(
                        painter = painterResource(id = R.drawable.img_eco_leaves_tip_1791177554294),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(46.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "View tips",
                        tint = Color(0xFF1B6B44),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 5. Eco Journey Impact Overview & 3-Metric Row
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Your daily eco-footprint dashboard",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFE2F3E7),
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

                Spacer(modifier = Modifier.height(10.dp))

                // Unified 3-Metric Impact Row
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xF2FFFFFF),
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.95f),
                                SoftPaleGreen.copy(alpha = 0.5f)
                            )
                        )
                    ),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp)) {
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

        // 6. Action Center: Quick Log & Simulate Buttons
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        onClick = { showQuickActionDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xF2FFFFFF),
                        border = BorderStroke(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    EcoGreenPrimary.copy(alpha = 0.45f)
                                )
                            )
                        ),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
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
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xD9FFFFFF),
                        border = BorderStroke(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    SoftPaleGreen.copy(alpha = 0.5f)
                                )
                            )
                        ),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
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

        // 5. Weekly Eco Activity Breakdown with Frosted Glass Styling
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xF2FFFFFF),
                    border = BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.95f),
                                SoftPaleGreen.copy(alpha = 0.5f)
                            )
                        )
                    ),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Weekly Eco Activity",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "Consistent habit progress",
                                fontSize = 11.sp,
                                color = DarkText.copy(alpha = 0.65f)
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
                                                if (day.isToday) Brush.verticalGradient(listOf(AccentLime, EcoGreenPrimary))
                                                else Brush.verticalGradient(listOf(EcoMintContainerLow, SoftPaleGreen.copy(alpha = 0.5f)))
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

        // 6. Milestone Badges Showcase with Frosted Glass Styling
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
                            color = if (badge.isUnlocked) Color(0xF2FFFFFF) else Color(0xB3FFFFFF),
                            border = BorderStroke(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.95f),
                                        if (badge.isUnlocked) EcoGreenPrimary.copy(alpha = 0.4f) else SoftPaleGreen.copy(alpha = 0.35f)
                                    )
                                )
                            ),
                            shadowElevation = if (badge.isUnlocked) 3.dp else 1.dp,
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
                                            if (badge.isUnlocked) Color(0xE6EDF7ED)
                                            else Color(0x66D6EED6)
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
                                    color = DarkText,
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
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xF2FFFFFF)),
                        border = BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.95f), SoftPaleGreen.copy(alpha = 0.5f)))),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xF2FFFFFF),
                        border = BorderStroke(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.95f),
                                    SoftPaleGreen.copy(alpha = 0.5f)
                                )
                            )
                        ),
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

    // Quick Action Dialog with Frosted Glass Styling
    if (showQuickActionDialog) {
        AlertDialog(
            onDismissRequest = { showQuickActionDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color(0xF7FFFFFF),
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
                        color = DarkText
                    )
                    IconButton(
                        onClick = { showQuickActionDialog = false },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = DarkText.copy(alpha = 0.7f)
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
                        color = DarkText.copy(alpha = 0.75f)
                    )

                    quickActions.forEach { action ->
                        Surface(
                            onClick = {
                                onLogQuickAction(action)
                                showQuickActionDialog = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xE6FFFFFF),
                            border = BorderStroke(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.9f),
                                        SoftPaleGreen.copy(alpha = 0.5f)
                                    )
                                )
                            ),
                            shadowElevation = 1.dp,
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
                                        color = DarkText
                                    )
                                    Text(
                                        text = action.description,
                                        fontSize = 11.sp,
                                        color = DarkText.copy(alpha = 0.65f)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EcoMintContainer,
                                    border = BorderStroke(1.dp, SoftPaleGreen)
                                ) {
                                    Text(
                                        text = "Action",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreenPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF2FFFFFF)),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.95f),
                    SoftPaleGreen.copy(alpha = 0.5f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                            color = DarkText
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
                color = DarkText,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Options List with Frosted Glass Styling
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                q.options.forEachIndexed { index, option ->
                    val isSelected = quizState.selectedOptionIndex == index
                    val isCorrectAnswer = isSubmitted && index == q.correctIndex
                    val isIncorrectChoice = isSubmitted && isSelected && !isCorrectAnswer

                    val cardBackground = when {
                        isCorrectAnswer -> Color(0xE6EDF7ED)
                        isIncorrectChoice -> EcoCoralAlert.copy(alpha = 0.15f)
                        isSelected -> Color(0xE6EDF7ED)
                        else -> Color(0xD9FFFFFF)
                    }

                    val borderColor = when {
                        isCorrectAnswer -> EcoGreenPrimary
                        isIncorrectChoice -> EcoCoralAlert
                        isSelected -> EcoGreenPrimary
                        else -> SoftPaleGreen.copy(alpha = 0.8f)
                    }

                    val textColor = when {
                        isCorrectAnswer -> EcoGreenPrimaryDark
                        isIncorrectChoice -> EcoCoralAlert
                        isSelected -> EcoGreenPrimaryDark
                        else -> DarkText
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF2FFFFFF)),
        border = BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.95f), SoftPaleGreen.copy(alpha = 0.5f)))),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
        shape = RoundedCornerShape(18.dp),
        color = Color(0xF2FFFFFF),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.95f),
                    SoftPaleGreen.copy(alpha = 0.5f)
                )
            )
        ),
        shadowElevation = 3.dp,
        modifier = modifier.height(92.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xE6EDF7ED))
                    .border(1.dp, SoftPaleGreen.copy(alpha = 0.8f), RoundedCornerShape(10.dp)),
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
                    color = DarkText,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = DarkText.copy(alpha = 0.65f),
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
        shape = RoundedCornerShape(16.dp),
        color = Color(0xF2FFFFFF),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.95f),
                    SoftPaleGreen.copy(alpha = 0.5f)
                )
            )
        ),
        shadowElevation = 3.dp,
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
                        .background(Color(0xE6EDF7ED))
                        .border(1.dp, SoftPaleGreen.copy(alpha = 0.8f), CircleShape),
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
                        color = DarkText,
                        maxLines = 1
                    )
                    Text(
                        text = "${scan.category} • $dateStr",
                        fontSize = 11.sp,
                        color = DarkText.copy(alpha = 0.65f)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xE6EDF7ED),
                border = BorderStroke(1.dp, SoftPaleGreen.copy(alpha = 0.8f))
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
