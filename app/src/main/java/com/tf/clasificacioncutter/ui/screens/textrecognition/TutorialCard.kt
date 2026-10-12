package com.tf.clasificacioncutter.ui.screens.textrecognition

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.theme.CutterAccent
import com.tf.clasificacioncutter.ui.theme.CutterTheme

data class TutorialStep(
    @DrawableRes val imageRes: Int,
    val title: String,
    val text: String
)

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TutorialCard(
    step: TutorialStep,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val isTablet = configuration.smallestScreenWidthDp >= 600

    val screenHeight = configuration.screenHeightDp.dp
    val showImage = isTablet || isPortrait

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showImage) {
                val imageHeight = if (isTablet) 320.dp else 220.dp

                Image(
                    painter = painterResource(id = step.imageRes),
                    contentDescription = step.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = screenHeight * 0.35f)
                        .height(imageHeight)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (step.title.isNotEmpty()) {
                    Text(
                        text = step.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CutterAccent,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                Text(
                    text = step.text,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF3B0554)
@Composable
fun TutorialCardPreview() {
    CutterTheme {
        TutorialCard(
            step = TutorialStep(
                imageRes = R.drawable.tutorial1,
                title = "Escanear Texto",
                text = "Enfoca el texto con la cámara y presiona el botón de escaneo para comenzar el proceso."
            )
        )
    }
}
