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
            .clip(RoundedCornerShape(if (isExpandedMode) 0.dp else 20.dp))
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
            isExpandedMode = isExpandedMode,
            onToggleExpand = onToggleExpand,
            modifier = Modifier.fillMaxSize()
        )

        // Selected Target Tag Indicator
        if (selectedItem != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EcoGreenPrimary.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, SoftPaleGreen.copy(alpha = 0.6f)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (isExpandedMode) 52.dp else 76.dp)
            ) {
                Text(
                    text = "${selectedItem.name} • ${selectedItem.primaryAction.title}",
                    color = LightCreamGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Bottom Action Buttons for triggering high-precision object scanning & image upload with Gemini AI
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = if (isExpandedMode) 56.dp else 34.dp, start = 12.dp, end = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onTriggerScan(lastCapturedFrame, lastTargetedMarker)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAnalyzing) EcoAmberWarm else EcoGreenPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("ar_scan_action_button")
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            color = DeepForestGreen,
                            modifier = Modifier.size(15.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyzing with Gemini...",
                            color = DeepForestGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = LightCreamGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scan with Gemini AI",
                            color = LightCreamGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Photo Upload Button (Android Photo Picker)
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, AccentLime.copy(alpha = 0.8f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DeepForestGreen.copy(alpha = 0.88f)
                    ),
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("upload_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddPhotoAlternate,
                        contentDescription = "Upload Image",
                        tint = AccentLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Upload",
                        color = LightCreamGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

