package com.tf.clasificacioncutter.ui.screens.textrecognition

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.tf.clasificacioncutter.ui.theme.CutterTheme
import com.tf.clasificacioncutter.utils.extensions.lockPortraitOnPhones

class TextRecognitionActivity : ComponentActivity() {
    private val viewModel: TextRecognitionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lockPortraitOnPhones()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        setContent {
            CutterTheme {
                TextRecognitionScreen(
                    viewModel = viewModel,
                    onBack = { finish() },
                    onUseText = { text ->
                        setResult(RESULT_OK, intent.putExtra("recognized_text", text))
                        finish()
                    }
                )
            }
        }
    }
}
