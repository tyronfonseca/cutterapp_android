package com.tf.clasificacioncutter.ui.screens.textrecognition

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import com.tf.clasificacioncutter.utils.extensions.extractISBNs

data class DetectedTextInfo(
    val text: String,
    val boundingBox: Rect,
    val isIsbn: Boolean = false,
    val extractedIsbn: String? = null,
)

class TextRecognitionViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPref = application.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    private val keyShowTutorial = "show_text_recognition_tutorial"

    var recognizedText by mutableStateOf("")
        private set

    var detectedLines = mutableStateListOf<DetectedTextInfo>()
        private set

    var detectedIsbn by mutableStateOf<String?>(null)
        private set

    var isCaptured by mutableStateOf(false)
        private set
        
    var capturedBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var imageSize by mutableStateOf<Pair<Int, Int>?>(null)
        private set

    var showTutorial by mutableStateOf(false)
        private set

    var shouldCapture by mutableStateOf(false)
        private set

    init {
        // Show tutorial only the first time
        showTutorial = sharedPref.getBoolean(keyShowTutorial, true)
    }

    fun onTutorialFinished() {
        showTutorial = false
        sharedPref.edit {
            putBoolean(keyShowTutorial, false)
        }
    }

    fun onTutorialOpen() {
        showTutorial = true
    }

    fun onTextRecognized(text: String, lines: List<DetectedTextInfo>, width: Int, height: Int, bitmap: Bitmap? = null) {
        if (!isCaptured) {
            recognizedText = text
            detectedLines.clear()
            detectedLines.addAll(lines)
            imageSize = Pair(width, height)

            val isbnFromLines = lines.firstOrNull { it.isIsbn }?.extractedIsbn
            val isbnFromText = text.extractISBNs().firstOrNull()
            detectedIsbn = isbnFromLines ?: isbnFromText

            if (shouldCapture && bitmap != null) {
                capturedBitmap = bitmap
                isCaptured = true
                shouldCapture = false
            }
        }
    }

    fun requestCapture() {
        shouldCapture = true
    }

    fun reset() {
        isCaptured = false
        shouldCapture = false
        recognizedText = ""
        detectedLines.clear()
        detectedIsbn = null
        capturedBitmap = null
    }
}
