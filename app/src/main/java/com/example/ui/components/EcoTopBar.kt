package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoTopBar(
    title: String,
    onAboutClick: () -> Unit,
    onSettingsClick: () -> Unit = onAboutClick,
    userName: String = "Eco User",
    modifier: Modifier = Modifier
) {
    var showProfileMenu by remember { mutableStateOf(false) }

    Surface(
        color = Color(0xFFF7FAF7),
        modifier = modifier.fillMaxWidth()
    ) {
        TopAppBar(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .testTag("eco_top_bar"),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = DarkText
            ),
            title = {
                if (title == "EcoVision") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // EcoVision Double-Leaf Logo Icon
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("ecovision_topbar_logo"),
                            contentAlignment = Alignment.Center
                        ) {
                            // Angled small leaf
                            Icon(
                                imageVector = Icons.Filled.Spa,
                                contentDescription = null,
                                tint = Color(0xFF2E9A60),
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(-28f)
                                    .align(Alignment.BottomStart)
                            )
                            // Main large leaf
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = "EcoVision Logo",
                                tint = Color(0xFF1B6B44),
                                modifier = Modifier
                                    .size(34.dp)
                                    .align(Alignment.TopEnd)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "EcoVision",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F3B2C),
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                text = "Augmented Reality for a Sustainable Future",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF557D6D),
                                maxLines = 1
                            )
                        }
                    }
                } else {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF0F3B2C)
                    )
                }
            },
            actions = {
                // Circular User Avatar Icon matching mockup
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE2F3E7),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { showProfileMenu = true }
                        .testTag("about_info_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "User profile and settings",
                            tint = Color(0xFF1B6B44),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        )
    }

    // Profile Dialog showing Settings and About options
    if (showProfileMenu) {
        AlertDialog(
            onDismissRequest = { showProfileMenu = false },
            shape = RoundedCornerShape(26.dp),
            containerColor = Color(0xFFF9FCFA),
            modifier = Modifier.testTag("profile_settings_dialog"),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDCF2E1),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF1B6B44),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = userName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F3B2C)
                            )
                            Text(
                                text = "Eco Pioneer • Sustainability Hub",
                                fontSize = 11.5.sp,
                                color = Color(0xFF456B5A)
                            )
                        }
                    }

                    IconButton(
                        onClick = { showProfileMenu = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF5A7266),
                            modifier = Modifier.size(20.dp)
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
                    // Option 1: Settings
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFEFF8F1),
                        border = BorderStroke(1.dp, Color(0xFFD6EED9)),
                        onClick = {
                            showProfileMenu = false
                            onSettingsClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_option_settings")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFD3EED8),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = null,
                                        tint = Color(0xFF1B6B44),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Settings",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F3B2C)
                                )
                                Text(
                                    text = "AI models, scanning preferences & haptics",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF456B5A)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF1B6B44),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Option 2: About
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFEFF8F1),
                        border = BorderStroke(1.dp, Color(0xFFD6EED9)),
                        onClick = {
                            showProfileMenu = false
                            onAboutClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_option_about")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFD3EED8),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF1B6B44),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "About",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F3B2C)
                                )
                                Text(
                                    text = "Team members, mission, documentation & credits",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF456B5A)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF1B6B44),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
