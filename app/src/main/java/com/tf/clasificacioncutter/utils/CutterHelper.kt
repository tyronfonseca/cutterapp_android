package com.tf.clasificacioncutter.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.tf.clasificacioncutter.data.CutterSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Text processing utilities.
 */
object TextUtils {
    /**
     * Normalizes a text by removing accents/diacritics and converting to uppercase.
     */
    fun removeAccents(str: String): String {
        val upper = str.uppercase(Locale.ROOT)
        return Normalizer.normalize(upper, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
}

/**
 * CSV Exporter utility for [CutterSearch] entities.
 */
object CsvExporter {

    private val fileNameFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    suspend fun exportSearchesToCsv(
        context: Context,
        data: List<CutterSearch>
    ): Result<File> = withContext(Dispatchers.IO) {
        if (data.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Export list cannot be empty."))
        }

        val timeStamp = fileNameFormat.format(Date())
        val fileName = "cutter_searches_$timeStamp.csv"
        val file = File(context.cacheDir, fileName)

        runCatching {
            FileWriter(file).use { writer ->
                val beanToCsv = StatefulBeanToCsvBuilder<CutterSearch>(writer)
                                    .build()
                beanToCsv.write(data)
            }
            file
        }
    }

    fun shareFile(context: Context, file: File, title: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }
}