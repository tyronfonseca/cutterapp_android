package com.tf.clasificacioncutter.utils

import android.content.Context
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.tf.clasificacioncutter.R
import java.io.InputStreamReader

/**
 * Class responsible for processing data from .csv files and calculating the Cutter code.
 */
class CutterGetter {

    private val cutterHelper = CutterHelper()

    /**
     * Loads and parses the .csv file from raw resources.
     */
    fun getCutterList(ctx: Context): ArrayList<Array<String>> {
        val location = R.raw.cutter_normal

        ctx.resources.openRawResource(location).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                val csvReader = CSVReaderBuilder(reader)
                    .withCSVParser(CSVParserBuilder().withSeparator(';').build())
                    .build()

                return ArrayList(csvReader.readAll())
            }
        }
    }

    /**
     * Determines the exact lexicographical prefix or predecessor within the Cutter table.
     */
    fun search(
        name: String,
        lstName: String,
        csvList: ArrayList<Array<String>>
    ): Array<String> {
        if (csvList.isEmpty()) return arrayOf("", "")

        // 1. Prepare and normalize the search term (Format: "LASTNAME, NAME")
        val query = formatTarget(lstName, name)

        // 2. Binary search over the sorted list of the Cutter table
        var low = 0
        var high = csvList.size - 1
        var bestMatchIndex = 0

        while (low <= high) {
            val mid = (low + high) ushr 1
            val candidateRaw = csvList[mid][0]
            val candidateFormatted = formatTargetFromCsv(candidateRaw)

            val cmp = query.compareTo(candidateFormatted)

            when {
                cmp == 0 -> {
                    // Exact match
                    return csvList[mid]
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

        return csvList[bestMatchIndex]
    }

    /**
     * Normalization of the string entered by the user according to cataloging rules.
     */
    private fun formatTarget(lastName: String, name: String): String {
        val fullName = if (name.isBlank()) lastName.trim() else "${lastName.trim()}, ${name.trim()}"
        return cutterHelper.removeAccents(fullName)
    }

    private fun formatTargetFromCsv(csvEntry: String): String {
        return cutterHelper.removeAccents(csvEntry.trim())
    }
}