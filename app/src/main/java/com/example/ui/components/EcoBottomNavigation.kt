package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.EcoScreen
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DarkText
import com.example.ui.theme.DeepForestGreen
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.LightGreenBackground
import com.example.ui.theme.NatureGreen
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftPaleGreen

@Composable
fun EcoBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("eco_bottom_navigation"),
        color = LightCreamGreen,
        border = BorderStroke(1.dp, SoftPaleGreen.copy(alpha = 0.85f)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EcoScreen.bottomNavItems.forEach { screen ->
                val isSelected = currentRoute == screen.route
                val isCenterScanner = screen == EcoScreen.Scanner

                if (isCenterScanner) {
                    // Highlighted Center AR Scanner Button with Circle Container
                    HighlightCenterScannerTab(
                        screen = screen,
                        isSelected = isSelected,
                        onClick = { onNavigate(screen.route) }
                    )
                } else {
                    // Standard Navigation Tab Item
                    StandardNavTabItem(
                        screen = screen,
                        isSelected = isSelected,
                        onClick = { onNavigate(screen.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HighlightCenterScannerTab(
    screen: EcoScreen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.06f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scanner_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .offset(y = (-8).dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("nav_item_${screen.route}")
            .padding(horizontal = 6.dp)
    ) {
        // Prominent Floating Circular Highlight
        Box(
            modifier = Modifier
                .size(54.dp)
                .shadow(
                    elevation = if (isSelected) 8.dp else 4.dp,
                    shape = CircleShape,
                    ambientColor = PrimaryGreen.copy(alpha = 0.4f),
                    spotColor = PrimaryGreen.copy(alpha = 0.6f)
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isSelected) listOf(
                            AccentLime,
                            PrimaryGreen,
                            DeepForestGreen
                        ) else listOf(
                            PrimaryGreen,
                            DeepForestGreen
                        )
                    )
                )
                .border(
                    BorderStroke(
                        width = if (isSelected) 3.dp else 2.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                LightCreamGreen,
                                NatureGreen
                            )
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                contentDescription = screen.title,
                tint = LightCreamGreen,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Center Tab Label
        Text(
            text = screen.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isSelected) DeepForestGreen else PrimaryGreen
        )
    }
}

@Composable
private fun StandardNavTabItem(
    screen: EcoScreen,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen else DarkText.copy(alpha = 0.65f),
        label = "nav_text_color"
    )
    val animatedIconColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen else DarkText.copy(alpha = 0.65f),
        label = "nav_icon_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("nav_item_${screen.route}")
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) LightGreenBackground else Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                contentDescription = screen.title,
                tint = animatedIconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = screen.title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = animatedTextColor,
            maxLines = 1
        )
    }
}
