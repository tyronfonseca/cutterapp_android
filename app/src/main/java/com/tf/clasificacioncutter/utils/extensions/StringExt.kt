package com.tf.clasificacioncutter.utils.extensions

import java.text.Normalizer

// String Extensions

/**
 * Removes any accent / diacritics. E.g., Ñ to N, é to e, ç to c
 */
fun String.removeAccents(): String {
    val uppercaseString = this.uppercase()
    val normalized = Normalizer.normalize(uppercaseString, Normalizer.Form.NFD)
    // Matches diacritical mark characters (accents) and strips them out
    val regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
    val stripped = regex.replace(normalized, "")
    // Explicitly replace cedilla Ç with C for Portuguese/Spanish names containing ç (e.g. Gonçalves, França)
    return stripped.replace('Ç', 'C')
}

/**
 * Extracts raw numeric ISBN-10 or ISBN-13 strings from text.
 */
fun String.extractISBNs(): List<String> {
    val pattern = """(?i)(?:ISBN(?:-1[03])?:?\s*)?\b((?:97[89][-\s]?)?(?:\d[-\s]?){9}[\dX])\b""".toRegex()

    return pattern.findAll(this).mapNotNull { matchResult ->
        val rawMatch = matchResult.groups[1]?.value ?: return@mapNotNull null
        val numberOnly = rawMatch.filter { it.isDigit() || it.uppercaseChar() == 'X' }
        if (numberOnly.length == 10 || numberOnly.length == 13) {
            numberOnly
        } else {
            null
        }
    }.distinct().toList()
}

/**
 * Returns the IntRange of a leading article if present when enabled.
 */
fun String.leadingArticleRange(enabled: Boolean): IntRange? {
    if (!enabled) return null

    val trimmed = this.trim()
    val pattern = """^(the|unos|unas|uma|los|las|una|an|el|la|un|os|as|um|a|o)\s+"""
                            .toRegex(RegexOption.IGNORE_CASE)

    val match = pattern.find(trimmed) ?: return null
    return match.range
}

/**
 * Removes leading English, Spanish, or Portuguese articles.
 */
fun String.strippingLeadingArticles(shouldIgnore: Boolean): String {
    val trimmed = this.trim()
    if (!shouldIgnore) return trimmed

    val range = trimmed.leadingArticleRange(enabled = true) ?: return trimmed

    return trimmed.substring(range.last + 1).trim()
}
