package com.tf.clasificacioncutter.utils.csv

import android.content.Context
import android.util.Log
import java.io.File
import java.io.InputStream

object CSVStorageManager {
    private const val  TAG = "CSVStorageManager"

    /**
     * Copies a file input stream or external file into the app internal storage directory.
     * Overwrites any existing file with the same target filename.
     */
    fun copyToApplicationDirectory(
        context: Context,
        sourceStream: InputStream,
        targetFilename: String
    ): File? {
        val appDirectory = context.filesDir

        return try {
            if(!appDirectory.exists()){
                appDirectory.mkdir()
            }

            val destinationFile = File(appDirectory, targetFilename)

            if(destinationFile.exists()) {
                destinationFile.delete()
            }

            sourceStream.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            destinationFile
        } catch (e: Exception) {
            Log.e(TAG, "Error saving custom CSV: ${e.localizedMessage}", e)
            null
        }
    }

    /**
     * Retrieves the file handle if it exists inside the internal storage.
     */
    fun getFile(context: Context, filename: String) : File? {
        val appDirectory = context.filesDir
        val cleanFilename = File(filename).name
        val targetFile = File(appDirectory, cleanFilename)

        return if(targetFile.exists()) targetFile else null
    }

    /**
     * Deletes the specified file from internal storage if present.
     */
    fun deleteFile(context: Context, filename: String) {
        getFile(context, filename)?.delete()
    }
}