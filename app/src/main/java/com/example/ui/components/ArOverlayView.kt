package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ar.ArHitMarker
import com.example.data.model.WasteItem
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DeepForestGreen
import com.example.ui.theme.EcoAmberWarm
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.SoftPaleGreen

@Composable
fun ArOverlayView(
    isAnalyzing: Boolean,
    selectedItem: WasteItem?,
    onTriggerScan: (Bitmap?, ArHitMarker?) -> Unit,
    isExpandedMode: Boolean = false,
    onToggleExpand: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var lastCapturedFrame by remember { mutableStateOf<Bitmap?>(null) }
    var lastTargetedMarker by remember { mutableStateOf<ArHitMarker?>(null) }
    var frameBitmapProvider by remember { mutableStateOf<(() -> Bitmap?)?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    onTriggerScan(bitmap, null)
                }
            } catch (e: Exception) {
                Log.e("ArOverlayView", "Failed to decode picked image: ${e.message}", e)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(if (isExpandedMode) 0.dp else 24.dp))
            .background(Color(0xFF091F13))
            .testTag("ar_viewfinder_container")
    ) {
        // Live ARCore / CameraX Preview Surface with 3D Precision Hit Testing & Plane Tracking
        ArCameraPreviewSurface(
            isScanningActive = isAnalyzing,
            onObjectTargeted = { marker, frameBitmap ->
                lastCapturedFrame = frameBitmap
                lastTargetedMarker = marker
                onTriggerScan(frameBitmap, marker)
            },
            onFrameCaptureProvider = { provider ->
                frameBitmapProvider = provider
            },
            isExpandedMode = isExpandedMode,
            onToggleExpand = onToggleExpand,
            modifier = Modifier.fillMaxSize()
        )

        // Selected Target Tag Indicator with Frosted Glass Styling
        if (selectedItem != null) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xCC1B6B44),
                border = BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.8f), AccentLime.copy(alpha = 0.6f)))),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (isExpandedMode) 64.dp else 54.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CenterFocusStrong,
                        contentDescription = null,
                        tint = AccentLime,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${selectedItem.name} • ${selectedItem.primaryAction.title}",
                        color = LightCreamGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }

        // Bottom Scrim Gradient for glass contrast
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(125.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x73030A05),
                            Color(0xD9040D07)
                        )
                    )
                )
        )

        // Frosted Glass Bottom Action Dock
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    bottom = if (isExpandedMode) 36.dp else 14.dp,
                    start = 14.dp,
                    end = 14.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isAnalyzing && selectedItem == null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x990A1F13),
                    border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f)),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Point camera at item & tap scan",
                        color = LightCreamGreen.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Glass Action Dock Bar
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xB8091E13),
                border = BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.55f), SoftPaleGreen.copy(alpha = 0.15f)))),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    // Frosted Glass Photo Upload Button (Android Photo Picker)
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.6f), SoftPaleGreen.copy(alpha = 0.25f)))),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0x990D2818)
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("upload_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddPhotoAlternate,
                            contentDescription = "Upload Image",
                            tint = AccentLime,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Upload",
                            color = LightCreamGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }

                    // Frosted Glass Primary Scan Shutter Button
                    Button(
                        onClick = {
                            val capturedFrame = frameBitmapProvider?.invoke() ?: lastCapturedFrame
                            onTriggerScan(capturedFrame, lastTargetedMarker)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAnalyzing) Color(0xD9F59E0B) else Color(0xDD1B6B44)
                        ),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isAnalyzing) Brush.verticalGradient(listOf(Color.White, Color(0xFFF59E0B)))
                            else Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.85f), AccentLime.copy(alpha = 0.7f)))
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("ar_scan_action_button")
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                color = DeepForestGreen,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Analyzing with AI...",
                                color = DeepForestGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = AccentLime,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Scan Item",
                                color = LightCreamGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

