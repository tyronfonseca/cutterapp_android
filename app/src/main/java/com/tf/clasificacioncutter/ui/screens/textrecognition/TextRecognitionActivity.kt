package com.tf.clasificacioncutter.ui.screens.textrecognition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.tf.clasificacioncutter.ui.theme.CutterTheme

class TextRecognitionActivity : ComponentActivity() {
    private val viewModel: TextRecognitionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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