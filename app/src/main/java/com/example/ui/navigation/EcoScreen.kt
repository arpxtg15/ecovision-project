package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class EcoScreen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val showInBottomBar: Boolean = true
) {
    object Welcome : EcoScreen(
        route = "welcome",
        title = "EcoVision",
        selectedIcon = Icons.Filled.NaturePeople,
        unselectedIcon = Icons.Outlined.NaturePeople,
        showInBottomBar = false
    )

    object Dashboard : EcoScreen(
        route = "dashboard",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        showInBottomBar = true
    )

    object Solutions : EcoScreen(
        route = "solutions",
        title = "Solutions",
        selectedIcon = Icons.Filled.Public,
        unselectedIcon = Icons.Outlined.Public,
        showInBottomBar = true
    )

    object Scanner : EcoScreen(
        route = "scanner",
        title = "AR Scan",
        selectedIcon = Icons.Filled.CenterFocusStrong,
        unselectedIcon = Icons.Outlined.CenterFocusStrong,
        showInBottomBar = true
    )

    object Shop : EcoScreen(
        route = "shop",
        title = "Shop",
        selectedIcon = Icons.Filled.ShoppingBag,
        unselectedIcon = Icons.Outlined.ShoppingBag,
        showInBottomBar = true
    )

    object TipsAndHabits : EcoScreen(
        route = "tips_habits",
        title = "Eco Tips",
        selectedIcon = Icons.Filled.EmojiObjects,
        unselectedIcon = Icons.Outlined.EmojiObjects,
        showInBottomBar = true
    )

    object AboutTeam : EcoScreen(
        route = "about_team",
        title = "About",
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info,
        showInBottomBar = false
    )

    companion object {
        // Scanner is placed strictly in the center (index 2 of 5 items)
        val bottomNavItems = listOf(
            Dashboard,
            Solutions,
            Scanner,
            Shop,
            TipsAndHabits
        )
    }
}
