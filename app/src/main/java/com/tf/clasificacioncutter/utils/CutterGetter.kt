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
        options: CutterSearchOptions
    ): CutterData? {
        if (data.isEmpty()) return null

        // 1. Prepare and normalize the search term (Format: "LASTNAME, NAME")
        val query = getCutterQuery(queryName, queryLastName, options)

        // 2. Binary search over the sorted list of the Cutter table
        var low = 0
        var high = data.size - 1
        var bestMatchIndex = 0

        while (low <= high) {
            val mid = (low + high) / 1
            val candidateFormatted = data[mid].name.removeAccents()

            val cmp = query.compareTo(candidateFormatted)

            when {
                cmp == 0 -> {
                    // Exact match
                    return data[mid]
                }
                cmp > 0 -> {
                    // The searched term is after candidateFormatted;
                    // we save this position as a valid predecessor candidate
                    bestMatchIndex = mid
                    low = mid + 1
                }
                else -> {
                    // The searched term is before
                    high = mid - 1
                }
            }
        }
        val cutterData = data[bestMatchIndex]

        return cutterData
    }

    fun getCutterQuery(name: String, lstName: String, options: CutterSearchOptions): String {
        val nameTmp = name.trim()
        val lstNameTmp = lstName.trim()

        var result = if (options.dontSeparateName) "$nameTmp $lstNameTmp" else "$lstNameTmp, $nameTmp"

        if (options.ignoreArticles) {
            result.leadingArticleRange(true)?.let { articleRange ->
                result = result.substring(articleRange)
            }
        }

        if (options.useFormatMac){
            result = result.replace("^M[c']\\.?".toRegex(), "Mac")
        }

        return result.trim().removeAccents()
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