package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DarkText
import com.example.ui.theme.DeepForestGreen
import com.example.ui.theme.GlassGreen
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.LightGreenBackground
import com.example.ui.theme.NatureGreen
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftPaleGreen

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onLearnMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_hud_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hud_pulse"
    )

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LightGreenBackground,
                        SoftPaleGreen,
                        LightGreenBackground
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 1. Top Section: Solarpunk Eco-City Landscape & AR HUD Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
            ) {
                // Solarpunk Eco City Artwork
                Image(
                    painter = painterResource(id = R.drawable.ecovision_hero_welcome_1787628954787),
                    contentDescription = "Solarpunk Eco City Landscape",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            GenericShape { size, _ ->
                                moveTo(0f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width, size.height - 40f)
                                quadraticBezierTo(
                                    size.width * 0.75f, size.height + 10f,
                                    size.width * 0.5f, size.height - 15f
                                )
                                quadraticBezierTo(
                                    size.width * 0.25f, size.height - 45f,
                                    0f, size.height - 10f
                                )
                                close()
                            }
                        )
                )

                // Ambient Vignette for HUD Contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    DeepForestGreen.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    DeepForestGreen.copy(alpha = 0.25f)
                                )
                            )
                        )
                )

                // --- AR HUD Overlays ---

                // A. Central Glowing Viewfinder / Scanner Reticle
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.Center)
                        .offset(y = 15.dp)
                        .scale(pulseScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Glowing Reticle Frame
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .border(
                                width = 2.5.dp,
                                brush = Brush.sweepGradient(
                                    listOf(
                                        AccentLime,
                                        NatureGreen,
                                        AccentLime,
                                        NatureGreen,
                                        AccentLime
                                    )
                                ),
                                shape = RoundedCornerShape(26.dp)
                            )
                    )

                    // Inner Glowing Circle with Glowing Leaf Icon
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                ambientColor = AccentLime,
                                spotColor = NatureGreen
                            )
                            .border(
                                width = 2.dp,
                                color = PureWhite.copy(alpha = 0.85f),
                                shape = CircleShape
                            )
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        PrimaryGreen.copy(alpha = 0.65f),
                                        DeepForestGreen.copy(alpha = 0.85f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Eco,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                // B. Top Right Card: "Recycling • On Track"
                GlassmorphismHudCard(
                    icon = Icons.Filled.Autorenew,
                    title = "Recycling",
                    status = "On Track",
                    statusColor = AccentLime,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 110.dp, end = 16.dp)
                )

                // C. Bottom Left Card: "Air Quality • Good 🟢"
                GlassmorphismHudCard(
                    icon = Icons.Filled.Eco,
                    title = "Air Quality",
                    status = "Good ●",
                    statusColor = AccentLime,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 95.dp, start = 16.dp)
                )

                // D. Bottom Right Card: "Water Saved • 124 L Today"
                GlassmorphismHudCard(
                    icon = Icons.Filled.Opacity,
                    title = "Water Saved",
                    status = "124 L Today",
                    statusColor = SoftPaleGreen,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 75.dp, end = 16.dp)
                )
            }

            // 2. Bottom Section: Leaf Seal, Clean Typography & CTA Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-32).dp)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Floating Official EcoVision Logo Emblem resting on the wave
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.5.dp, LightCreamGreen),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ecovision_official_logo_1787984664845),
                        contentDescription = "EcoVision Official Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // "Welcome to"
                Text(
                    text = "Welcome to",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = DarkText,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // "EcoVision" Logo Title with PrimaryGreen & DarkText
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold,
                                    shadow = Shadow(
                                        color = PrimaryGreen.copy(alpha = 0.25f),
                                        offset = Offset(0f, 2f),
                                        blurRadius = 3f
                                    )
                                )
                            ) {
                                append("Eco")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = DarkText,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Vision")
                            }
                        },
                        fontSize = 44.sp,
                        letterSpacing = (-0.5).sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Decorative Botanical Center Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        NatureGreen.copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Eco,
                        contentDescription = null,
                        tint = NatureGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        NatureGreen.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle: "Get the Best Out of Nature."
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Get the Best Out of Nature.",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreen,
                        letterSpacing = 0.2.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.Eco,
                        contentDescription = null,
                        tint = AccentLime,
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(25f)
                    )
                }

                Spacer(modifier = Modifier.height(34.dp))

                // Pill "Get Started ->" Call-to-Action Button
                GetStartedButton(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("welcome_get_started_button")
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Subtle Student Credits
                Text(
                    text = "Crafted with 💚 by Arpit Gupta & Avasyu Bansal • Khaitan Public School",
                    fontSize = 11.sp,
                    color = DarkText.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable { onLearnMore() }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GlassmorphismHudCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    status: String,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = DeepForestGreen.copy(alpha = 0.5f),
                spotColor = DeepForestGreen.copy(alpha = 0.7f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = GlassGreen,
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    PureWhite.copy(alpha = 0.45f),
                    AccentLime.copy(alpha = 0.35f),
                    PureWhite.copy(alpha = 0.1f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(PureWhite.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite
                )
                Text(
                    text = status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
private fun GetStartedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(60.dp)
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                ambientColor = DeepForestGreen.copy(alpha = 0.4f),
                spotColor = PrimaryGreen.copy(alpha = 0.6f)
            ),
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(
            1.5.dp,
            Brush.verticalGradient(
                listOf(
                    PureWhite.copy(alpha = 0.6f),
                    AccentLime.copy(alpha = 0.4f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            AccentLime,
                            NatureGreen,
                            PrimaryGreen,
                            DeepForestGreen
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spacer for symmetric balance
                Spacer(modifier = Modifier.size(44.dp))

                // Centered "Get Started" text
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    letterSpacing = 0.4.sp
                )

                // Deep Forest Green circular badge with white forward arrow on right
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(DeepForestGreen.copy(alpha = 0.9f))
                        .border(
                            BorderStroke(1.dp, PureWhite.copy(alpha = 0.35f)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Arrow Forward",
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
