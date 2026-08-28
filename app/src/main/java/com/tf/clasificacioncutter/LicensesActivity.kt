package com.tf.clasificacioncutter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tf.clasificacioncutter.ui.screens.AboutScreen
import com.tf.clasificacioncutter.ui.theme.CutterTheme

class LicensesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CutterTheme {
                AboutScreen(onBack = { finish() })
            }
        }
    }
}


