package com.tf.clasificacioncutter.utils

import java.text.Normalizer
import java.util.Locale

/**
 * Helper with utilities for text processing and Spanish alphabet rules (Cutter).
 */
class CutterHelper {
    /**
     * Normalizes a text by removing accents/diacritics and converting to uppercase.
     */
    fun removeAccents(str: String): String {
        val upper = str.uppercase(Locale.ROOT)
        return Normalizer.normalize(upper, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
}