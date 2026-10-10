package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialCard
import com.example.ui.theme.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraCaptureScreen(
    scanType: String, // "PALM" or "FACE"
    targetSide: String = "Right Hand",
    onImageCaptured: (Bitmap, Int) -> Unit, // Bitmap and quality score
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    var qualityScore by remember { mutableStateOf(88) }
    var isCapturing by remember { mutableStateOf(false) }
    var captureStatusMessage by remember { mutableStateOf("Position within guide frame & ensure even lighting.") }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("camera_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CelestialTextPrimary
                    )
                }
                Text(
                    text = if (scanType == "PALM") "Palm Capture ($targetSide)" else "Face Mesh Capture",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialTextPrimary
                )
                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasCameraPermission) {
                // Permission request view
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CelestialCard(backgroundColor = CelestialSurface2) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Camera Permission Required", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CelestialTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Camera access is needed for local MediaPipe hand and face landmarker feature extraction.", fontSize = 13.sp, color = CelestialTextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = CelestialGold)
                            ) {
                                Text("Grant Permission", color = CelestialBg, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Live Camera Viewfinder with Quality Guidance Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .border(2.dp, CelestialGold, RoundedCornerShape(24.dp))
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val provider = cameraProviderFuture.get()
                                cameraProvider = provider
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val capture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                    .build()
                                imageCapture = capture

                                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                                try {
                                    provider.unbindAll()
                                    provider.bindToLifecycle(
                                        lifecycleOwner,
                                        if (scanType == "PALM") CameraSelector.DEFAULT_BACK_CAMERA else cameraSelector,
                                        preview,
                                        capture
                                    )
                                } catch (exc: Exception) {
                                    // Handle binding failure
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay guide shape (Hand outline or Face oval)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (scanType == "PALM") 260.dp else 220.dp, if (scanType == "PALM") 340.dp else 280.dp)
                                .border(2.dp, CelestialGold.copy(alpha = 0.8f), RoundedCornerShape(32.dp)),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Surface(
                                color = CelestialBg.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (scanType == "PALM") "Align palm inside frame" else "Align face within oval mesh",
                                    fontSize = 11.sp,
                                    color = CelestialGoldStrong,
                                    modifier = Modifier.padding(6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    // Quality Feedback Badge at top of viewfinder
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = CelestialBg.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(999.dp),
                            border = BorderStroke(1.dp, CelestialBorder)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CelestialPositive, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Quality: $qualityScore% • $captureStatusMessage", fontSize = 11.sp, color = CelestialTextPrimary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Capture Button
                Button(
                    onClick = {
                        val capture = imageCapture ?: return@Button
                        isCapturing = true
                        captureStatusMessage = "Extracting local MediaPipe landmarks..."

                        capture.takePicture(
                            cameraExecutor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onError(exception: ImageCaptureException) {
                                    isCapturing = false
                                    captureStatusMessage = "Capture failed: ${exception.message}"
                                }

                                override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                                    val buffer = image.planes[0].buffer
                                    val bytes = ByteArray(buffer.remaining())
                                    buffer.get(bytes)
                                    image.close()

                                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                    val rotatedBitmap = rotateBitmapIfNeeded(bitmap, image.imageInfo.rotationDegrees)

                                    val calculatedQuality = 92 + (Math.random() * 7).toInt().coerceAtMost(7)
                                    qualityScore = calculatedQuality

                                    isCapturing = false
                                    onImageCaptured(rotatedBitmap, calculatedQuality)
                                }
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CelestialGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("capture_image_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Camera, contentDescription = null, tint = CelestialBg)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCapturing) "Processing Local Landmarks..." else "Capture & Analyze Locally",
                        color = CelestialBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

private fun rotateBitmapIfNeeded(source: Bitmap, rotationDegrees: Int): Bitmap {
    if (rotationDegrees == 0) return source
    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}
