package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEcoProfileEntity
import com.example.ml.teachablemachine.TeachableMachineModelInfo
import com.example.ui.components.TeachableMachineModelSelectorDialog
import com.example.ui.theme.DarkText
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoGreenPrimaryDark
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.EcoMintBorder
import com.example.ui.theme.EcoMintContainer
import com.example.ui.theme.SoftPaleGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userProfile: UserEcoProfileEntity,
    activeModel: TeachableMachineModelInfo,
    isModelLoading: Boolean,
    onSelectModelPreset: (TeachableMachineModelInfo) -> Unit,
    onSetCustomModelUrl: (String) -> Unit,
    onUpdateUserName: (String) -> Unit,
    onClearScanHistory: () -> Unit,
    onResetDemoData: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showModelDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempNameInput by remember { mutableStateOf(userProfile.userName) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    // Toggle states
    var enableHapticFeedback by remember { mutableStateOf(true) }
    var enableArReticle by remember { mutableStateOf(true) }
    var enableRealtimePill by remember { mutableStateOf(true) }
    var enableDailyNotifications by remember { mutableStateOf(true) }
    var useMetricUnits by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF7))
            .testTag("settings_screen")
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF0F3B2C)
                    )
                    Text(
                        text = "AI vision, AR scanning & preferences",
                        fontSize = 11.5.sp,
                        color = Color(0xFF557D6D)
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0F3B2C)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFF7FAF7)
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. AI Vision Model (Google Teachable Machine)
            item {
                SettingsSectionCard(title = "AI Vision & Detection Engine") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFEDF7EE),
                        border = BorderStroke(1.dp, Color(0xFFCCE8D2)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFD6F0DC),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Filled.Memory,
                                                contentDescription = null,
                                                tint = EcoGreenPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = activeModel.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F3B2C)
                                        )
                                        Text(
                                            text = if (activeModel.isBuiltIn) "Google Pre-trained Model" else "Custom Model URL",
                                            fontSize = 11.5.sp,
                                            color = EcoGreenSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isModelLoading) Color(0xFFFFF3CD) else Color(0xFFD4EDDA)
                                ) {
                                    Text(
                                        text = if (isModelLoading) "Loading..." else "Active",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isModelLoading) Color(0xFF856404) else Color(0xFF155724),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = activeModel.description,
                                fontSize = 12.sp,
                                color = DarkText.copy(alpha = 0.75f),
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showModelDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1B6B44)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("change_ai_model_button")
                            ) {
                                Text(
                                    text = "Switch AI Vision Model or Enter URL",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // 2. AR Scanner Preferences
            item {
                SettingsSectionCard(title = "AR Scanner & Camera Settings") {
                    SettingsToggleRow(
                        icon = Icons.Filled.Visibility,
                        title = "Spatial AR Reticle",
                        subtitle = "Display animated circular targeting indicator",
                        checked = enableArReticle,
                        onCheckedChange = { enableArReticle = it }
                    )

                    SettingsToggleRow(
                        icon = Icons.Filled.CameraAlt,
                        title = "Real-time Detection Pills",
                        subtitle = "Show floating classification badge in viewfinder",
                        checked = enableRealtimePill,
                        onCheckedChange = { enableRealtimePill = it }
                    )

                    SettingsToggleRow(
                        icon = Icons.Filled.Vibration,
                        title = "Haptic Feedback",
                        subtitle = "Gentle vibration upon successful object classification",
                        checked = enableHapticFeedback,
                        onCheckedChange = { enableHapticFeedback = it }
                    )
                }
            }

            // 3. User Profile & Preferences
            item {
                SettingsSectionCard(title = "Profile & Sustainability Units") {
                    // Editable Name
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF9FCFA),
                        border = BorderStroke(1.dp, Color(0xFFE2EFE4)),
                        onClick = {
                            tempNameInput = userProfile.userName
                            showEditNameDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_user_name_row")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFD6F0DC),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = null,
                                            tint = EcoGreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "User Name",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkText
                                    )
                                    Text(
                                        text = userProfile.userName,
                                        fontSize = 12.sp,
                                        color = EcoGreenPrimaryDark
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Name",
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsToggleRow(
                        icon = Icons.Filled.Notifications,
                        title = "Daily Eco Notifications",
                        subtitle = "Receive actionable daily sustainability insights",
                        checked = enableDailyNotifications,
                        onCheckedChange = { enableDailyNotifications = it }
                    )

                    SettingsToggleRow(
                        icon = Icons.Filled.Settings,
                        title = "Metric System (kg / Liters)",
                        subtitle = if (useMetricUnits) "Display in kg & Liters" else "Display in lbs & Gallons",
                        checked = useMetricUnits,
                        onCheckedChange = { useMetricUnits = it }
                    )
                }
            }

            // 4. Data Management & Storage
            item {
                SettingsSectionCard(title = "Data Management") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showClearConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFDE8E8),
                                contentColor = Color(0xFF9B1C1C)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("clear_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Clear Scans",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showResetConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEBF5FF),
                                contentColor = Color(0xFF1E429F)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reset_demo_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RestartAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reset Demo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 5. About & Project Report Shortcut
            item {
                SettingsSectionCard(title = "About EcoVision") {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFEFF8F1),
                        border = BorderStroke(1.dp, Color(0xFFD6EED9)),
                        onClick = onNavigateToAbout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("about_team_shortcut_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                Column {
                                    Text(
                                        text = "Project Report & Team",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F3B2C)
                                    )
                                    Text(
                                        text = "Learn about the mission, creators & documentation",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF456B5A)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF1B6B44),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "EcoVision v2.4.0 • Built with TensorFlow.js & Jetpack Compose",
                        fontSize = 11.sp,
                        color = DarkText.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Teachable Machine Dialog
    if (showModelDialog) {
        TeachableMachineModelSelectorDialog(
            activeModel = activeModel,
            isLoading = isModelLoading,
            onSelectPreset = { model ->
                onSelectModelPreset(model)
                showModelDialog = false
            },
            onSetCustomUrl = { url ->
                onSetCustomModelUrl(url)
                showModelDialog = false
            },
            onDismiss = { showModelDialog = false }
        )
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            shape = RoundedCornerShape(22.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Edit User Name",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F3B2C)
                )
            },
            text = {
                OutlinedTextField(
                    value = tempNameInput,
                    onValueChange = { tempNameInput = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoGreenPrimary,
                        unfocusedBorderColor = SoftPaleGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempNameInput.isNotBlank()) {
                            onUpdateUserName(tempNameInput)
                        }
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel", color = DarkText)
                }
            }
        )
    }

    // Clear History Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            shape = RoundedCornerShape(22.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Clear Scan History?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF9B1C1C)
                )
            },
            text = {
                Text(
                    text = "This will permanently remove all past camera scans and recognition records from your device storage.",
                    fontSize = 13.sp,
                    color = DarkText.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearScanHistory()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE02424))
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = DarkText)
                }
            }
        )
    }

    // Reset Demo Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            shape = RoundedCornerShape(22.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Reset Environmental Stats?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F3B2C)
                )
            },
            text = {
                Text(
                    text = "This will reset your plastic savings, carbon offset metrics, and water tracking to default baseline demo numbers.",
                    fontSize = 13.sp,
                    color = DarkText.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDemoData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary)
                ) {
                    Text("Reset Stats")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel", color = DarkText)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2EFE4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F3B2C)
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFEFF8F1),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = DarkText.copy(alpha = 0.65f),
                    lineHeight = 15.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF1B6B44),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD0D7D2)
            )
        )
    }
}
