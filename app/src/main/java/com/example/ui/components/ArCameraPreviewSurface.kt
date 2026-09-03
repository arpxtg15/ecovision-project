package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ar.ArCoreHelper
import com.example.ar.ArHitMarker
import com.example.ar.ArObjectDetectionService
import com.example.ui.theme.AccentLime
import com.example.ui.theme.DeepForestGreen
import com.example.ui.theme.EcoAmberWarm
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.LightCreamGreen
import com.example.ui.theme.SoftPaleGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

private const val TAG = "ArCameraPreviewSurface"

/**
 * High-precision CameraX Preview surface engineered for physical object scanning,
 * real-time AR plane detection, tap-to-focus physical object targeting, flashlight/torch control,
 * and multi-level optical macro zoom for Gemini Vision multimodal analysis.
 */
@Composable
fun ArCameraPreviewSurface(
    isScanningActive: Boolean,
    onObjectTargeted: ((ArHitMarker?, Bitmap?) -> Unit)? = null,
    isExpandedMode: Boolean = false,
    onToggleExpand: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val arService = remember { ArObjectDetectionService.getInstance() }
    val arState by arService.detectionState.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var arAvailability by remember {
        mutableStateOf(ArCoreHelper.ArAvailability.UNKNOWN)
    }

    var isCameraBound by remember { mutableStateOf(false) }
    var currentCamera by remember { mutableStateOf<Camera?>(null) }
    var currentPreviewView by remember { mutableStateOf<PreviewView?>(null) }
    var cameraProviderInstance by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var isBackCamera by remember { mutableStateOf(true) }
    var isTorchEnabled by remember { mutableStateOf(false) }
    var currentZoomRatio by remember { mutableFloatStateOf(1.0f) }

    // Tap-to-focus visual feedback state
    var focusTapPosition by remember { mutableStateOf<Offset?>(null) }
    var showFocusRing by remember { mutableStateOf(false) }

    // Shutter flash effect when physical object is captured
    var isShutterFlashing by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
        ArCoreHelper.checkArCoreAvailability(context) { availability ->
            arAvailability = availability
            Log.d(TAG, "ARCore status: $availability")
        }
    }

    // Function to bind or re-bind CameraX preview with selected lens
    fun bindCamera(provider: ProcessCameraProvider, previewView: PreviewView) {
        try {
            provider.unbindAll()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = if (isBackCamera) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            val boundCam = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
            currentCamera = boundCam
            isCameraBound = true
            boundCam.cameraControl.setZoomRatio(currentZoomRatio)
            boundCam.cameraControl.enableTorch(isTorchEnabled && isBackCamera)
        } catch (exc: Exception) {
            Log.e(TAG, "Failed to bind CameraX preview: ${exc.message}", exc)
        }
    }

    // Re-bind when camera lens selection changes
    LaunchedEffect(isBackCamera) {
        cameraProviderInstance?.let { provider ->
            currentPreviewView?.let { pView ->
                bindCamera(provider, pView)
            }
        }
    }

    // Start AR Object Detection service when camera preview is active
    DisposableEffect(Unit) {
        (context as? Activity)?.let { activity ->
            arService.startService(activity)
        }
        onDispose {
            arService.stopService()
        }
    }

    // Scanning laser animation
    val infiniteTransition = rememberInfiniteTransition(label = "ar_scanner_transition")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_laser_anim"
    )

    val reticlePulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "reticle_pulse"
    )

    val shutterAlpha by animateFloatAsState(
        targetValue = if (isShutterFlashing) 0.8f else 0.0f,
        animationSpec = tween(durationMillis = 120),
        label = "shutter_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(if (isExpandedMode) 0.dp else 20.dp))
            .background(Color(0xFF07180E))
            .testTag("ar_camera_preview_surface")
    ) {
        if (hasCameraPermission) {
            // 1. Live CameraX Preview Surface
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                    currentPreviewView = previewView
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val provider = cameraProviderFuture.get()
                            cameraProviderInstance = provider
                            bindCamera(provider, previewView)
                        } catch (exc: Exception) {
                            Log.e(TAG, "Failed to get ProcessCameraProvider: ${exc.message}", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            focusTapPosition = tapOffset
                            showFocusRing = true

                            // Physical Tap-to-Focus Metering
                            currentCamera?.cameraControl?.let { camCtrl ->
                                val factory = SurfaceOrientedMeteringPointFactory(
                                    size.width.toFloat(),
                                    size.height.toFloat()
                                )
                                val point = factory.createPoint(tapOffset.x, tapOffset.y)
                                val action = FocusMeteringAction.Builder(
                                    point,
                                    FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
                                )
                                    .setAutoCancelDuration(3, TimeUnit.SECONDS)
                                    .build()
                                camCtrl.startFocusAndMetering(action)
                            }

                            // Perform AR Hit Test on the tapped physical surface
                            val normX = (tapOffset.x / size.width).coerceIn(0.1f, 0.9f)
                            val normY = (tapOffset.y / size.height).coerceIn(0.1f, 0.9f)
                            arService.performHitTestAtScreenPosition(
                                screenNormX = normX,
                                screenNormY = normY,
                                viewportWidth = size.width.toFloat(),
                                viewportHeight = size.height.toFloat()
                            )

                            scope.launch {
                                delay(1800)
                                showFocusRing = false
                            }
                        }
                    }
            )

            // 2. 3D Plane Mesh & Hit Markers Overlay
            ArHitMarkerOverlay(
                markers = arState.hitMarkers,
                detectedPlanes = arState.detectedPlanes,
                activeMarker = arState.activeFocusedMarker,
                onMarkerClick = { marker ->
                    arService.selectMarker(marker.id)
                },
                onTapToHitTest = { normX, normY ->
                    arService.performHitTestAtScreenPosition(
                        screenNormX = normX,
                        screenNormY = normY,
                        viewportWidth = 800f,
                        viewportHeight = 600f
                    )
                },
                onMarkerDismiss = {
                    arService.clearMarkers()
                },
                onScanMarkerItem = { marker ->
                    val frameBitmap = currentPreviewView?.bitmap
                    isShutterFlashing = true
                    scope.launch {
                        delay(120)
                        isShutterFlashing = false
                        onObjectTargeted?.invoke(marker, frameBitmap)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // 3. AR Targeting Reticle and Precision Laser Surface Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val boxWidth = canvasWidth * 0.72f * reticlePulse
                val boxHeight = canvasHeight * 0.60f * reticlePulse
                val left = (canvasWidth - boxWidth) / 2f
                val top = (canvasHeight - boxHeight) / 2f
                val cornerLength = 34f

                // Draw AR Scanning Frame Corners
                val strokeColor = if (isScanningActive) AccentLime else SoftPaleGreen
                val cornerStroke = Stroke(width = 2.5.dp.toPx())

                // Top-Left Corner
                drawLine(strokeColor, Offset(left, top), Offset(left + cornerLength, top), strokeWidth = cornerStroke.width)
                drawLine(strokeColor, Offset(left, top), Offset(left, top + cornerLength), strokeWidth = cornerStroke.width)

                // Top-Right Corner
                drawLine(strokeColor, Offset(left + boxWidth, top), Offset(left + boxWidth - cornerLength, top), strokeWidth = cornerStroke.width)
                drawLine(strokeColor, Offset(left + boxWidth, top), Offset(left + boxWidth, top + cornerLength), strokeWidth = cornerStroke.width)

                // Bottom-Left Corner
                drawLine(strokeColor, Offset(left, top + boxHeight), Offset(left + cornerLength, top + boxHeight), strokeWidth = cornerStroke.width)
                drawLine(strokeColor, Offset(left, top + boxHeight), Offset(left, top + boxHeight - cornerLength), strokeWidth = cornerStroke.width)

                // Bottom-Right Corner
                drawLine(strokeColor, Offset(left + boxWidth, top + boxHeight), Offset(left + boxWidth - cornerLength, top + boxHeight), strokeWidth = cornerStroke.width)
                drawLine(strokeColor, Offset(left + boxWidth, top + boxHeight), Offset(left + boxWidth, top + boxHeight - cornerLength), strokeWidth = cornerStroke.width)

                // Center Aiming Crosshairs
                val centerX = canvasWidth / 2f
                val centerY = canvasHeight / 2f
                val crosshairSize = 18f
                drawLine(
                    color = AccentLime.copy(alpha = 0.85f),
                    start = Offset(centerX - crosshairSize, centerY),
                    end = Offset(centerX + crosshairSize, centerY),
                    strokeWidth = 1.8.dp.toPx()
                )
                drawLine(
                    color = AccentLime.copy(alpha = 0.85f),
                    start = Offset(centerX, centerY - crosshairSize),
                    end = Offset(centerX, centerY + crosshairSize),
                    strokeWidth = 1.8.dp.toPx()
                )

                // Animated AR Laser Scanning Line
                if (isScanningActive) {
                    val laserY = top + (boxHeight * scanLineOffset)
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                AccentLime.copy(alpha = 0.4f),
                                AccentLime,
                                Color.Transparent
                            ),
                            startY = laserY - 14f,
                            endY = laserY + 14f
                        ),
                        topLeft = Offset(left + 4f, laserY - 10f),
                        size = Size(boxWidth - 8f, 20f)
                    )
                }
            }

            // 4. Animated Tap-To-Focus Target Marker
            if (showFocusRing && focusTapPosition != null) {
                val pos = focusTapPosition!!
                Box(
                    modifier = Modifier
                        .offset { IntOffset(pos.x.roundToInt() - 28, pos.y.roundToInt() - 28) }
                        .size(56.dp)
                        .border(1.5.dp, AccentLime, RoundedCornerShape(8.dp))
                )
            }

            // 5. Shutter White Flash Animation
            if (shutterAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = shutterAlpha))
                )
            }

            // 6. Top Telemetry & Optical Controls Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Precision Lock Status Chip & Gemini Vision Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DeepForestGreen.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, SoftPaleGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isCameraBound) AccentLime else EcoAmberWarm)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (arAvailability == ArCoreHelper.ArAvailability.SUPPORTED_INSTALLED) "AR (99%)" else "AR Vision",
                                color = LightCreamGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Gemini Multimodal Vision AI Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DeepForestGreen.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, AccentLime.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Gemini Vision",
                                tint = AccentLime,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Gemini Multimodal Vision",
                                color = LightCreamGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Camera Action Controls: Torch, Lens Flip, Zoom, Fullscreen Expand
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Flashlight / Torch Toggle
                    if (isBackCamera) {
                        Surface(
                            shape = CircleShape,
                            color = if (isTorchEnabled) AccentLime else DeepForestGreen.copy(alpha = 0.92f),
                            border = BorderStroke(1.dp, if (isTorchEnabled) AccentLime else SoftPaleGreen.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    isTorchEnabled = !isTorchEnabled
                                    currentCamera?.cameraControl?.enableTorch(isTorchEnabled)
                                }
                                .testTag("camera_torch_toggle")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isTorchEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                                    contentDescription = "Torch",
                                    tint = if (isTorchEnabled) DeepForestGreen else LightCreamGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    // Flip Camera Lens (Back / Front)
                    Surface(
                        shape = CircleShape,
                        color = DeepForestGreen.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, SoftPaleGreen.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                isBackCamera = !isBackCamera
                                isTorchEnabled = false
                            }
                            .testTag("camera_flip_lens")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Cameraswitch,
                                contentDescription = "Switch Camera",
                                tint = LightCreamGreen,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Macro Zoom Toggle (1.0x -> 2.0x -> 3.0x)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DeepForestGreen.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, AccentLime.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .clickable {
                                currentZoomRatio = when (currentZoomRatio) {
                                    1.0f -> 2.0f
                                    2.0f -> 3.0f
                                    else -> 1.0f
                                }
                                currentCamera?.cameraControl?.setZoomRatio(currentZoomRatio)
                                arService.setZoomLevel(currentZoomRatio)
                            }
                            .testTag("camera_zoom_toggle")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ZoomIn,
                                contentDescription = "Zoom",
                                tint = AccentLime,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${currentZoomRatio.toInt()}x",
                                color = LightCreamGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Expand / Fullscreen Toggle
                    if (onToggleExpand != null) {
                        Surface(
                            shape = CircleShape,
                            color = DeepForestGreen.copy(alpha = 0.92f),
                            border = BorderStroke(1.dp, SoftPaleGreen.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { onToggleExpand() }
                                .testTag("camera_expand_toggle")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isExpandedMode) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                    contentDescription = "Expand Viewfinder",
                                    tint = LightCreamGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 7. Bottom Physical Plane Telemetry Bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DeepForestGreen.copy(alpha = 0.94f),
                    border = BorderStroke(1.dp, SoftPaleGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📐 Tap object to focus & target with Gemini Vision",
                            color = LightCreamGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(arState.currentFocalDistanceMeters * 100).roundToInt()}cm plane",
                            color = AccentLime,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

        } else {
            // Permission Required Prompt View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = EcoGreenPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Camera Permission",
                            tint = AccentLime,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Camera Access Required for AR Scanning",
                    color = LightCreamGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Grant camera permission to enable real-time CameraX preview, physical object detection, and sustainability material classification.",
                    color = SoftPaleGreen,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("grant_camera_permission_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Videocam,
                        contentDescription = null,
                        tint = LightCreamGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Enable Camera Preview",
                        color = LightCreamGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
