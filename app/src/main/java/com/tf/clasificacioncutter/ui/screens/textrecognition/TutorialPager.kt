package com.tf.clasificacioncutter.ui.screens.textrecognition

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.components.CutterButton
import com.tf.clasificacioncutter.ui.theme.CutterAccent
import com.tf.clasificacioncutter.ui.theme.CutterTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun TutorialPager(
    steps: List<TutorialStep>,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    onFinish: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { steps.size })
    val coroutineScope = rememberCoroutineScope()
    val scope = rememberCoroutineScope()
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val screenWidth = with(density) { windowInfo.containerSize.width.toDp() }
    val isTablet = screenWidth > 600.dp
    
    // Adaptive
    val pagerWidth = if (isTablet) 550.dp else screenWidth * 0.9f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .width(pagerWidth),
                contentPadding = PaddingValues(horizontal = 0.dp),
                pageSpacing = 0.dp,
                verticalAlignment = Alignment.CenterVertically
            ) { page ->
                TutorialCard(
                    step = steps[page],
                    modifier = Modifier
                        .wrapContentHeight()
                        .graphicsLayer {
                            val pageOffset = (
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            ).absoluteValue
                            
                            val fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = fraction
                            )
                            scaleX = lerp(
                                start = 0.9f,
                                stop = 1f,
                                fraction = fraction
                            )
                            scaleY = lerp(
                                start = 0.9f,
                                stop = 1f,
                                fraction = fraction
                            )
                            
                            rotationY = lerp(
                                start = 15f,
                                stop = 0f,
                                fraction = fraction
                            ) * (if (page < pagerState.currentPage) 1f else -1f)
                        }
                )
            }

            val arrowPadding = if (isTablet) 16.dp else 4.dp
            val arrowSize = if (isTablet) 64.dp else 48.dp
            val iconSize = if (isTablet) 40.dp else 32.dp
            
            // Left arrow
            if (pagerState.currentPage > 0) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = pagerState.currentPage - 1,
                                animationSpec = tween(durationMillis = 500)
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = arrowPadding)
                        .size(arrowSize)
                        .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Anterior",
                        tint = Color.White,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }

            // Right arrow
            if (pagerState.currentPage < steps.size - 1) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = pagerState.currentPage + 1,
                                animationSpec = tween(durationMillis = 500)
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = arrowPadding)
                        .size(arrowSize)
                        .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Siguiente",
                        tint = Color.White,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        // Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(steps.size) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) CutterAccent else Color.LightGray)
                )
            }
        }

        // Final button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isTablet) 100.dp else 80.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (!buttonText.isNullOrEmpty() && pagerState.currentPage == steps.size - 1) {
                CutterButton(onClick = {
                    onFinish()
                    coroutineScope.launch {
                        pagerState.scrollToPage(0)
                    }
                }, text = buttonText)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF3B0554)
@Composable
fun TutorialPagerPreview() {
    val steps = listOf(
        TutorialStep(R.drawable.inicio_1, "Paso 1:", text="Enfoca el texto con la cámara y presiona el botón de escaneo."),
        TutorialStep(R.drawable.inicio_1, "Paso 2:", text="Toca los cuadros de texto resaltados para seleccionar lo que necesites."),
        TutorialStep(R.drawable.inicio_1, "Paso 3:", text="Puedes modificar el texto reconocido antes de utilizarlo.")
    )
    CutterTheme {
        TutorialPager(steps = steps, buttonText = "¡Entendido!")
    }
}
