package com.tf.clasificacioncutter.ui.screens.main

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.AppDatabase
import com.tf.clasificacioncutter.data.model.CutterSearchOptions
import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.data.model.cuttertable.CutterTableEntity
import com.tf.clasificacioncutter.data.repository.CutterTableRepository
import com.tf.clasificacioncutter.data.settings.AppSettings
import com.tf.clasificacioncutter.utils.CutterGetter
import com.tf.clasificacioncutter.utils.csv.CSVHelper
import com.tf.clasificacioncutter.utils.csv.CSVStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class MainUiState(
    val currentTableSelected: CutterTableEntity? = null,
    val currentCutterData: List<CutterData> = emptyList(),
    val isLoading: Boolean = false,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "MainViewModel"
    private val context: Context get() = getApplication<Application>().applicationContext
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val db = AppDatabase.getDatabase(context)
    private val repository = CutterTableRepository(db.cutterTableDao())

    // User settings instance
    val settings = AppSettings(context)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        loadActiveTable()
    }

    fun loadActiveTable() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }

            val activeEntity = repository.ensureDefaultTablesExist(context)
            val filename = activeEntity?.filename

            if ((activeEntity == null) || filename.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        currentCutterData = emptyList(),
                        currentTableSelected = null,
                        isLoading = false
                    )
                }
                return@launch
            }

            val rawData: List<CutterData>

            // Try external/custom directory file
            val customFile = CSVStorageManager.getFile(context, filename)
            rawData = if (customFile != null && customFile.exists()) {
                CSVHelper.getCSVData(file = customFile, hasHeaders = activeEntity.hasHeaders)
            } else {
                // Fall back to raw resources using the filename string
                val rawResourceId = when (filename) {
                    "pha.csv" -> R.raw.pha
                    "cutter_old.csv" -> R.raw.cutter_old
                    else -> R.raw.cutter_normal
                }
                CSVHelper.getCSVData(context = context, resource = rawResourceId)
            }

            _uiState.update {
                it.copy(
                    currentCutterData = rawData,
                    currentTableSelected = activeEntity,
                    isLoading = false
                )
            }
        }
    }

    fun selectActiveTable(entity: CutterTableEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setActiveTable(entity)
            loadActiveTable()
        }
    }

    fun deleteTable(table: CutterTableEntity) {
        if (table.cannotDelete) return
        viewModelScope.launch(Dispatchers.IO) {
            if (table.filename.isNotEmpty() && table.filename != "cutter_normal.csv" && table.filename != "pha.csv" && table.filename != "cutter_old.csv") {
                CSVStorageManager.deleteFile(context, table.filename)
            }
            db.cutterTableDao().delete(table)

            if (table.isSelected || _uiState.value.currentTableSelected?.id == table.id) {
                val defaultTable = db.cutterTableDao().getAllTablesSync().firstOrNull { it.cannotDelete }
                if (defaultTable != null) {
                    selectActiveTable(defaultTable)
                } else {
                    loadActiveTable()
                }
            }
        }
    }

    fun search(
        name: String,
        lastName: String,
        options: CutterSearchOptions
    ): CutterData? {
        val activeFilename = _uiState.value.currentTableSelected?.filename ?: ""
        val isOldAlphabet = activeFilename == "cutter_old.csv"
        return CutterGetter.search(
            queryName = name,
            queryLastName = lastName,
            data = _uiState.value.currentCutterData,
            options = options,
            isOldAlphabet = isOldAlphabet
        )
    }

    suspend fun saveImportedCSV(
        fileUri: Uri,
        name: String,
        description: String,
        setSelected: Boolean,
        hasHeaders: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        // Generate unique filename on save
        val sanitizeName = name.replace(" ", "_").lowercase()
        val uniqueFilename = "${sanitizeName}_${UUID.randomUUID().toString().take(8)}.csv"

        try {
            val inputStream = context.contentResolver.openInputStream(fileUri) ?: return@withContext false

            // Copy file to App internal files directory
            val savedFile = CSVStorageManager.copyToApplicationDirectory(
                context = context,
                sourceStream = inputStream,
                targetFilename = uniqueFilename
            ) ?: return@withContext false
            Log.d(tag, "Successfully copied CSV file to ${savedFile.absolutePath}")

            // Save metadata in Repository
            val displayName = name.trim().ifEmpty { "No name" }
            val savedEntity = repository.saveCustomTableMetadata(
                name = displayName,
                description = description,
                uniqueFilename = uniqueFilename,
                hasHeaders = hasHeaders
            )

            if (setSelected) {
                selectActiveTable(savedEntity)
            }
            true
        } catch (e: Exception) {
            CSVStorageManager.deleteFile(context, uniqueFilename)
            Log.e(tag, "Error saving custom CSV: ${e.localizedMessage}", e)
            false
        }
    }
}
