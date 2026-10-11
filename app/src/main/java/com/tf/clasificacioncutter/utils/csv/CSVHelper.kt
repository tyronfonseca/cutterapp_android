package com.tf.clasificacioncutter.utils.csv

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.data.model.cutterdata.fullCallNumber
import com.tf.clasificacioncutter.data.settings.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
object CSVHelper {

    private const val TAG = "CSVHelper"

    /**
     * Parses CSV data from bundled raw resources (defaults to `R.raw.cutter_normal`).
     */
    fun getCSVData(context: Context, resource: Int = R.raw.cutter_normal): List<CutterData> {
        return try {
            context.resources.openRawResource(resource).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).useLines { lines ->
                    parseLines(lines, hasHeaders = true)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read bundled CSV raw resource ID: $resource", e)
            emptyList()
        }
    }

    /**
     * Parses CSV data directly from a local file object.
     */
    fun getCSVData(file: File, hasHeaders: Boolean = false): List<CutterData> {
        if (!file.exists()) return emptyList()
        return try {
            file.bufferedReader(StandardCharsets.UTF_8).useLines { lines ->
                parseLines(lines, hasHeaders = hasHeaders)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading CSV file at ${file.path}", e)
            emptyList()
        }
    }

    /**
     * Helper to process sequence lines into [CutterData] models lazily.
     */
    private fun parseLines(lines: Sequence<String>, hasHeaders: Boolean): List<CutterData> {
        val rows = if (hasHeaders) lines.drop(1) else lines

        return rows.mapNotNull { row ->
            val fields = parseCSVRow(row)
            if (fields.size < 2) return@mapNotNull null

            val name = fields[0]
            val code = fields[1]

            if (name.isEmpty() && code.isEmpty()) return@mapNotNull null

            CutterData(name = name, code = code)
        }.toList()
    }

    /**
     * Parses a single CSV line into trimmed field strings, preserving quoted text.
     */
    private fun parseCSVRow(line: String): List<String> {
        val fields = mutableListOf<String>()
        val currentField = StringBuilder()
        var insideQuotes = false

        for (char in line) {
            when (char) {
                '"' -> {
                    insideQuotes = !insideQuotes
                }
                ',' -> {
                    if (insideQuotes) {
                        currentField.append(char)
                    } else {
                        fields.add(currentField.toString().trim())
                        currentField.clear()
                    }
                }
                else -> currentField.append(char)
            }
        }

        fields.add(currentField.toString().trim())
        return fields
    }

    /**
     * Exports a list of [CutterData] records into a formatted CSV and opens the Android Share Sheet.
     */
    suspend fun exportCutterDataToCSV(
        context: Context,
        data: List<CutterData>,
        settings: AppSettings
    ): Boolean = withContext(Dispatchers.IO) {
        if (data.isEmpty()) return@withContext false

        try {
            val exportDir = File(context.cacheDir, "exports").apply { if (!exists()) mkdirs() }
            val csvFile = File(exportDir, "CutterApp_${System.currentTimeMillis()}.csv")
            val csvHeader = "Author,Name,Code,Number" + if (settings.includeExtrasInExport) ",ISBN,Book Name,DDC" else ""

            FileOutputStream(csvFile).use { outputStream ->
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    // Header Row
                    writer.write(csvHeader + "\n")

                    // Data Rows
                    for (item in data) {
                        val extras = listOf(
                            escapeCsvField(item.isbn),
                            escapeCsvField(item.bookName),
                            escapeCsvField(item.ddcSelected)
                        )
                        var line = listOf(
                            escapeCsvField(item.searchValue),
                            escapeCsvField(item.name),
                            escapeCsvField(item.code),
                            escapeCsvField(item.fullCallNumber(settings))
                        )

                        if (settings.includeExtrasInExport) {
                            line = line + extras
                        }

                        val row = line.joinToString(",")

                        writer.write(row)
                        writer.write("\n")
                    }
                }
            }

            shareFile(context, csvFile, "Export CSV")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export Cutter data to CSV", e)
            false
        }
    }

    /**
     * Launches the system share sheet for the specified file.
     */
    fun shareFile(context: Context, file: File, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share file: ${file.path}", e)
        }
    }

    /**
     * Safely wraps CSV cell values in double quotes if special characters are detected.
     */
    private fun escapeCsvField(value: String): String {
        if (value.contains(",") || value.contains("\"")
                || value.contains("\n") || value.contains("\r")) {
            val escaped = value.replace("\"", "\"\"")
            return "\"$escaped\""
        }
        return value
    }
}