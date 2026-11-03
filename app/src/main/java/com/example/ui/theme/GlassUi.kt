package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Premium Frosted Glassmorphism Design System for EcoVision
 * Provides reusable glass color tokens, gradients, modifiers, and containers.
 */

// Glass Color Tokens
val GlassWhiteUltra = Color(0xF2FFFFFF)        // 95% translucent crisp white glass
val GlassWhiteMedium = Color(0xD9FFFFFF)       // 85% translucent white frosted glass
val GlassWhiteSubtle = Color(0xB8FFFFFF)       // 72% translucent glass
val GlassWhiteWhisper = Color(0x66FFFFFF)      // 40% translucent sheer glass

val GlassDarkUltra = Color(0xE60D2818)         // 90% frosted deep forest glass
val GlassDarkMedium = Color(0xB8091E13)        // 72% frosted dark emerald glass
val GlassDarkSubtle = Color(0x8005140C)        // 50% sheer dark glass

val GlassBorderWhite = Color(0x80FFFFFF)       // 50% white specular rim
val GlassBorderMint = Color(0x80D6EED6)        // Frosted pastel mint rim
val GlassBorderEmerald = Color(0x4D2D8A58)     // Frosted emerald rim
val GlassBorderLimeGlow = Color(0xBF8CE830)    // Glowing lime specular edge
val GlassBorderDark = Color(0x33FFFFFF)        // Subtle dark mode glass rim

// Standard Glass Brushes
val GlassBrushWhiteCard = Brush.verticalGradient(
    colors = listOf(
        Color(0xF5FFFFFF),
        Color(0xD9F7FCF6),
        Color(0xCCEDF7ED)
    )
)

val GlassBrushWhiteElevated = Brush.verticalGradient(
    colors = listOf(
        Color(0xFAFFFFFF),
        Color(0xEBFFFFFF),
        Color(0xD9F7FCF6)
    )
)

val GlassBrushDarkHud = Brush.verticalGradient(
    colors = listOf(
        Color(0xCC0E2E1D),
        Color(0x99071A10)
    )
)

val GlassBrushDarkBar = Brush.verticalGradient(
    colors = listOf(
        Color(0xDE081C11),
        Color(0xF2040E08)
    )
)

val GlassBrushScannerShutter = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF288B57),
        Color(0xFF1B6B44),
        Color(0xFF0F442A)
    )
)

val GlassBrushScanButtonGlow = Brush.radialGradient(
    colors = listOf(
        AccentLime.copy(alpha = 0.95f),
        PrimaryGreen,
        DeepForestGreen
    )
)

val GlassRimLight = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.90f),
        Color.White.copy(alpha = 0.40f),
        SoftPaleGreen.copy(alpha = 0.60f)
    )
)

val GlassRimDark = Brush.verticalGradient(
    colors = listOf(
        SoftPaleGreen.copy(alpha = 0.65f),
        Color.White.copy(alpha = 0.20f),
        NatureGreen.copy(alpha = 0.45f)
    )
)

val GlassRimLime = Brush.verticalGradient(
    colors = listOf(
        AccentLime.copy(alpha = 0.95f),
        AccentLime.copy(alpha = 0.40f)
    )
)

/**
 * Modifier extension to apply a frosted glass surface effect
 */
fun Modifier.glassEffect(
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = GlassWhiteMedium,
    borderColor: Color = GlassBorderWhite,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 4.dp
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = Color(0x140F261B),
        spotColor = Color(0x1F0F261B)
    )
    .clip(shape)
    .background(backgroundColor)
    .border(borderWidth, borderColor, shape)

/**
 * Reusable Frosted Glass Surface Container
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundBrush: Brush = GlassBrushWhiteCard,
    borderStroke: BorderStroke = BorderStroke(1.dp, GlassRimLight),
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x120F261B),
                spotColor = Color(0x1C0F261B)
            )
            .clip(shape)
            .background(brush = backgroundBrush)
            .border(borderStroke, shape),
        content = content
    )
}

/**
 * Interactive Frosted Glass Card with Ripple & Specular Sheen
 */
@Composable
fun GlassCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundBrush: Brush = GlassBrushWhiteCard,
    borderStroke: BorderStroke = BorderStroke(1.dp, GlassRimLight),
    elevation: Dp = 3.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color(0x140F261B),
                spotColor = Color(0x1F0F261B)
            )
            .clip(shape)
            .background(brush = backgroundBrush)
            .border(borderStroke, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = NatureGreen.copy(alpha = 0.2f)),
                onClick = onClick
            ),
        content = content
    )
}

/**
 * Frosted Glass Action Pill / Capsule
 */
@Composable
fun GlassPill(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassDarkMedium,
    borderColor: Color = SoftPaleGreen.copy(alpha = 0.4f),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * Frosted Glass Circular Icon Button
 */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    backgroundColor: Color = GlassDarkMedium,
    borderStroke: BorderStroke = BorderStroke(1.dp, SoftPaleGreen.copy(alpha = 0.4f)),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderStroke, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = AccentLime.copy(alpha = 0.3f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
