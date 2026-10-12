package com.tf.clasificacioncutter.utils

import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.data.model.CutterSearchOptions
import com.tf.clasificacioncutter.utils.extensions.leadingArticleRange
import com.tf.clasificacioncutter.utils.extensions.removeAccents

/**
 * Object responsible for processing data and calculating the Cutter code.
 */
object CutterGetter {
    /**
     * Determines the exact lexicographical prefix or predecessor within the Cutter table.
     */
    fun search(
        queryName: String,
        queryLastName: String,
        data: List<CutterData>,
        options: CutterSearchOptions,
        isOldAlphabet: Boolean = false
    ): CutterData? {
        if (data.isEmpty()) return null

        // 1. Prepare and normalize the search term (Format: "LASTNAME, NAME")
        val query = getCutterQuery(queryName, queryLastName, options, isOldAlphabet)

        // 2. Exact match check
        for (item in data) {
            val candidate = prepareCandidate(item.name, isOldAlphabet)
            if (query.compareTo(candidate) == 0) {
                return item
            }
        }

        // 3. Robust predecessor search (largest candidate <= query) to handle any CSV sorting anomalies
        var bestMatch: CutterData? = null
        var bestCandidate: String? = null

        for (item in data) {
            val candidate = prepareCandidate(item.name, isOldAlphabet)
            val cmp = query.compareTo(candidate)
            if (cmp > 0) {
                if (bestCandidate == null || candidate > bestCandidate) {
                    bestCandidate = candidate
                    bestMatch = item
                }
            }
        }

        return bestMatch ?: data.firstOrNull()
    }

    /*
     * Why we need normalization for the old Spanish alphabet:
     * Standard ASCII/Unicode sorting evaluates "CH" as 'C' followed by 'H' (sorting between CG and CI)
     * and "LL" as two 'L's, and places "Ñ" at the end of the alphabet. 
     * However, the old Spanish alphabet treats "CH" and "LL" as single indivisible digraph letters
     * (sorting between C-D and L-M respectively) and "Ñ" between N and O. 
     * Normalizing and mapping these digraphs/letters into unique intermediary Unicode values 
     * ensures that standard binary search correctly matches the pre-sorted old order in cutter_old.csv.
     */
    private fun prepareCandidate(text: String, isOldAlphabet: Boolean): String {
        val normalized = text.removeAccents()
        if (!isOldAlphabet) return normalized

        // Transform old Spanish digraphs/letters (CH, LL, Ñ) for old alphabet sorting order:
        // - CH/Ch mapped to '\u0081' (sorts between C and D)
        // - LL/Ll mapped to '\u0082' (sorts between L and M)
        // - Ñ/ñ mapped to '\u0083' (sorts between N and O)
        return normalized
            .replace("CH", "\u0081", ignoreCase = true)
            .replace("LL", "\u0082", ignoreCase = true)
            .replace("Ñ", "\u0083", ignoreCase = true)
    }

    fun getCutterQuery(name: String, lstName: String, options: CutterSearchOptions, isOldAlphabet: Boolean = false): String {
        val nameTmp = name.trim()
        val lstNameTmp = lstName.trim()

        var result = when {
            options.dontSeparateName -> "$nameTmp $lstNameTmp".trim()
            nameTmp.isEmpty() -> lstNameTmp
            else -> "$lstNameTmp, $nameTmp"
        }

        if (options.ignoreArticles) {
            result.leadingArticleRange(true)?.let { articleRange ->
                result = result.substring(articleRange)
            }
        }

        if (options.useFormatMac){
            result = result.replace("^M[c']\\.?".toRegex(), "Mac")
        }

        return prepareCandidate(result, isOldAlphabet)
    }

    /**
    * Splits text into author name and surname.
    */
    fun splitText(text: String): Pair<String, String> {
        val trimmed = text.trim()

        // Try "Surname, Name" or "Surname,Name" format
        val regex = """^([\p{L}\s'-]+),\s*([\p{L}\s'-]+)$""".toRegex()
        regex.find(trimmed)?.let { matchResult ->
            val name = matchResult.groups[2]?.value ?: ""
            val surname = matchResult.groups[1]?.value ?: ""
            return Pair(name, surname)
        }

        // Fallback: Split by last space if exists
        val lastSpaceIndex = trimmed.lastIndexOf(' ')
        if (lastSpaceIndex != -1) {
            val surname = trimmed.substring(lastSpaceIndex + 1).trim()
            val name = trimmed.substring(0, lastSpaceIndex).trim()
            return Pair(name, surname)
        }

        // Default: Entire string as surname
        return Pair("", trimmed)
    }
}
