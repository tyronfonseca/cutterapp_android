package com.tf.clasificacioncutter.ui.screens.textrecognition

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.components.DialogWithTextField
import com.tf.clasificacioncutter.ui.theme.CutterAccentLight
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterText
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TextRecognitionScreen(
    viewModel: TextRecognitionViewModel,
    onBack: () -> Unit,
    onUseText: (String) -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isCaptured)
                                stringResource(R.string.text_recognition_title_alternative)
                            else
                                stringResource(R.string.text_recognition_title)
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (viewModel.isCaptured) viewModel.reset() else onBack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back_btn)
                        )
                    }
                },
                actions = {
                    if(cameraPermissionState.status.isGranted) {
                        IconButton(onClick = { viewModel.onTutorialOpen() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = stringResource(R.string.tutorial_btn)
                            )
                        }
                    }
                    if (viewModel.isCaptured) {
                        IconButton(onClick = { viewModel.reset() }) {
                            Icon(Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.reset)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CutterPrimary,
                    titleContentColor = CutterText,
                    navigationIconContentColor = CutterText,
                    actionIconContentColor = CutterText
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (cameraPermissionState.status.isGranted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (!viewModel.isCaptured) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        lifecycleOwner = lifecycleOwner,
                        cameraExecutor = cameraExecutor,
                        viewModel = viewModel,
                        onPreviewViewCreated = { previewView = it }
                    ) { text, lines, width, height, bitmap ->
                        viewModel.onTextRecognized(text, lines, width, height, bitmap)
                    }

                    FloatingActionButton(
                        onClick = {
                            viewModel.requestCapture()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        containerColor = Color.White,
                        contentColor = CutterPrimary
                    ) {
                        Icon(Icons.Default.DocumentScanner,
                            contentDescription = stringResource(R.string.scan_btn)
                        )
                    }
                } else {
                    LensSelectionScreen(
                        viewModel = viewModel,
                        onUseText = onUseText
                    )
                }

                if (viewModel.showTutorial) {
                    val steps = listOf(
                        TutorialStep(
                            imageRes = R.drawable.tutorial1,
                            title = stringResource(R.string.tutorial_step1_title),
                            text = stringResource(R.string.tutorial_step1_text)
                        ),
                        TutorialStep(
                            imageRes = R.drawable.tutorial2,
                            title = stringResource(R.string.tutorial_step2_title),
                            text = stringResource(R.string.tutorial_step2_text)
                        ),
                        TutorialStep(
                            imageRes = R.drawable.tutorial3,
                            title = stringResource(R.string.tutorial_step3_title),
                            text = stringResource(R.string.tutorial_step3_text)
                        )
                    )
                    TutorialPager(
                        steps = steps,
                        onFinish = { viewModel.onTutorialFinished() },
                        buttonText = stringResource(R.string.tutorial_finish_btn),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6F))
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text(stringResource(R.string.grant_camera_access))
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

@Composable
fun LensSelectionScreen(
    viewModel: TextRecognitionViewModel,
    onUseText: (String) -> Unit
) {
    val bitmap = viewModel.capturedBitmap ?: return
    val lines = viewModel.detectedLines
    val imageSize = viewModel.imageSize ?: return

    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var editingText by remember { mutableStateOf<String?>(null) }

    val overlayColor = CutterAccentLight.copy(alpha = 0.3f)
    val strokeColor = CutterPrimary

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(imageSize.first.toFloat() / imageSize.second.toFloat())
                .fillMaxSize()
                .onGloballyPositioned { containerSize = it.size }
                .pointerInput(lines) {
                    detectTapGestures { offset ->
                        val scaleX = containerSize.width.toFloat() / imageSize.first
                        val scaleY = containerSize.height.toFloat() / imageSize.second

                        val tappedLine = lines.find { line ->
                            val rect = line.boundingBox
                            val scaledRect = Rect(
                                (rect.left * scaleX).toInt(),
                                (rect.top * scaleY).toInt(),
                                (rect.right * scaleX).toInt(),
                                (rect.bottom * scaleY).toInt()
                            )
                            scaledRect.contains(offset.x.toInt(), offset.y.toInt())
                        }
                        tappedLine?.let { editingText = it.text }
                    }
                }
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val scaleX = size.width / imageSize.first
                val scaleY = size.height / imageSize.second

                lines.forEach { line ->
                    val rect = line.boundingBox
                    drawRect(
                        color = overlayColor,
                        topLeft = Offset(rect.left * scaleX, rect.top * scaleY),
                        size = Size(
                            (rect.right - rect.left) * scaleX,
                            (rect.bottom - rect.top) * scaleY
                        )
                    )
                    drawRect(
                        color = strokeColor,
                        topLeft = Offset(rect.left * scaleX, rect.top * scaleY),
                        size = Size(
                            (rect.right - rect.left) * scaleX,
                            (rect.bottom - rect.top) * scaleY
                        ),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }

    editingText?.let { text ->
        DialogWithTextField(
            initialText = text,
            onDismissRequest = { editingText = null },
            onUseText = onUseText
        )
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier,
    lifecycleOwner: LifecycleOwner,
    cameraExecutor: ExecutorService,
    viewModel: TextRecognitionViewModel,
    onPreviewViewCreated: (PreviewView) -> Unit,
    onTextDetected: (String, List<DetectedTextInfo>, Int, Int, Bitmap?) -> Unit
) {
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FIT_CENTER
            }
            onPreviewViewCreated(previewView)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor) { imageProxy ->
                                processImageProxy(recognizer, imageProxy, viewModel, onTextDetected)
                            }
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Use case binding failed", e)
                    }
                },
                ContextCompat.getMainExecutor(ctx)
            )

            previewView
        }
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    recognizer: TextRecognizer,
    imageProxy: ImageProxy,
    viewModel: TextRecognitionViewModel,
    onTextDetected: (String, List<DetectedTextInfo>, Int, Int, Bitmap?) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val lines = visionText.textBlocks.flatMap { block ->
                    block.lines.map { line ->
                        DetectedTextInfo(line.text, line.boundingBox ?: Rect())
                    }
                }
                val rotation = imageProxy.imageInfo.rotationDegrees
                val isRotated = rotation == 90 || rotation == 270
                val width = if (isRotated) imageProxy.height else imageProxy.width
                val height = if (isRotated) imageProxy.width else imageProxy.height

                var bitmap: Bitmap? = null
                if (viewModel.shouldCapture) {
                    bitmap = imageProxy.toBitmap()
                    if (rotation != 0) {
                        val matrix = Matrix()
                        matrix.postRotate(rotation.toFloat())
                        bitmap = Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                    }
                }

                onTextDetected(visionText.text, lines, width, height, bitmap)
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Text recognition failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}