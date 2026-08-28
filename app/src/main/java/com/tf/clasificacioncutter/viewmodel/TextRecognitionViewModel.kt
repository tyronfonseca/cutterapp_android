package com.tf.clasificacioncutter.viewmodel

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class DetectedTextInfo(
    val text: String,
    val boundingBox: Rect,
)

class TextRecognitionViewModel : ViewModel() {
    var recognizedText by mutableStateOf("")
        private set

    var detectedLines = mutableStateListOf<DetectedTextInfo>()
        private set

    var isCaptured by mutableStateOf(false)
        private set
        
    var capturedBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var imageSize by mutableStateOf<Pair<Int, Int>?>(null)
        private set

    fun onTextRecognized(text: String, lines: List<DetectedTextInfo>, width: Int, height: Int) {
        if (!isCaptured) {
            recognizedText = text
            detectedLines.clear()
            detectedLines.addAll(lines)
            imageSize = Pair(width, height)
        }
    }

    fun capture(bitmap: Bitmap) {
        capturedBitmap = bitmap
        isCaptured = true
    }

    fun reset() {
        isCaptured = false
        recognizedText = ""
        detectedLines.clear()
        capturedBitmap = null
    }
}
