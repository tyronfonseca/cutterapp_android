package com.tf.clasificacioncutter.ui.screens.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.tf.clasificacioncutter.ui.screens.about.AboutActivity
import com.tf.clasificacioncutter.ui.screens.history.HistoryActivity
import com.tf.clasificacioncutter.ui.screens.textrecognition.TextRecognitionActivity
import com.tf.clasificacioncutter.ui.theme.CutterTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val textRecognitionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val recognizedText = result.data?.getStringExtra("recognized_text")
            recognizedText?.let {

                viewModel.searchReset()
                // We assume the last "space" is the delimiter of the name and lastname
                val possibleName = it.substringBeforeLast(" ").uppercase()
                val possibleLastName = it.substringAfterLast(" ").uppercase()

                viewModel.onLastNameChange(possibleLastName)

                if(possibleLastName.length != possibleName.length) {
                    viewModel.onNameChange(possibleName)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CutterTheme {
                MainScreen(
                    viewModel = viewModel,
                    onNavigateToCredits = {
                        startActivity(Intent(this, AboutActivity::class.java))
                    },
                    onNavigateToHistory = {
                        startActivity(Intent(this, HistoryActivity::class.java))
                    },
                    onNavigateToTextRecognition = {
                        textRecognitionLauncher.launch(
                            Intent(
                                this,
                                TextRecognitionActivity::class.java
                            )
                        )
                    }
                )
            }
        }
    }
}