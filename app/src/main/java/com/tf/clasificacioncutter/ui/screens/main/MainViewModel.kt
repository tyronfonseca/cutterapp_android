package com.tf.clasificacioncutter.ui.screens.main

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.AppDatabase
import com.tf.clasificacioncutter.data.CutterSearch
import com.tf.clasificacioncutter.utils.CutterGetter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val cutterGetter = CutterGetter()
    private val db = AppDatabase.getDatabase(application)
    private val cutterSearchDao = db.cutterSearchDao()

    companion object {
        private const val PREFS_NAME = "sharedPref"
        private const val KEY_NUM = "numeroCutter"
        private const val KEY_CUTTER_VAL = "cutterUsado"
        private const val DB_NAME = "DB"
    }

    private val sharedPref: SharedPreferences =
        application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Local in-memory cache outside UiState to avoid memory leaks/copies
    private var cutterCacheList: ArrayList<Array<String>> = arrayListOf()

    var uiState by mutableStateOf(MainState())
        private set

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val dbType = sharedPref.getInt(DB_NAME, 0)
        val numCutter = sharedPref.getString(KEY_NUM, "").orEmpty()
        val cutterUsed = sharedPref.getString(KEY_CUTTER_VAL, "").orEmpty()

        uiState = uiState.copy(
            dbType = dbType,
            numCutterResult = numCutter,
            cutterUsed = cutterUsed,
            isLoading = true,
            isVisible = numCutter.isNotEmpty() || cutterUsed.isNotEmpty()
        )

        // Asynchronous load outside the UI thread
        viewModelScope.launch(Dispatchers.IO) {
            val list = cutterGetter.getCutterList(getApplication())
            cutterCacheList = list

            withContext(Dispatchers.Main) {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun onLastNameChange(value: String) {
        uiState = uiState.copy(lastName = value, errorMessage = null)
    }

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value, errorMessage = null)
    }

    fun searchReset(){
        uiState = uiState.copy(lastName = "", name = "", errorMessage = null)
    }

    fun search() {
        val lastName = uiState.lastName.trim()
        val name = uiState.name.trim()

        uiState = uiState.copy(isVisible = false)

        if (lastName.length < 2) {
            uiState = uiState.copy(
                errorMessage = getApplication<Application>().getString(R.string.msg_error_last_name),
                isVisible = true
            )
            return
        }

        if (cutterCacheList.isEmpty()) {
            uiState = uiState.copy(
                errorMessage = getApplication<Application>().getString(R.string.msg_error),
                isVisible = true
            )
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            val result = cutterGetter.search(name, lastName,cutterCacheList)

            if (result.size >= 2) {
                val letter = result[0].take(1)

                val cutter = letter + result[1]
                val cutterUsedText = "${result[0]}: ${result[1]}"

                // Ensure exit animation of previous result is visible
                delay(200.milliseconds)

                withContext(Dispatchers.Main) {
                    saveResult(cutter, cutterUsedText)
                }
            }
        }
    }

    private fun saveResult(cutter: String, cutterUsedText: String) {
        val originalSearch = "${uiState.lastName}, ${uiState.name}".trim().removeSuffix(",").trim()
        sharedPref.edit {
            putString(KEY_NUM, cutter)
            putString(KEY_CUTTER_VAL, cutterUsedText)
        }

        uiState = uiState.copy(
            numCutterResult = cutter,
            cutterUsed = cutterUsedText,
            errorMessage = null,
            isVisible = true
        )

        viewModelScope.launch(Dispatchers.IO) {
            cutterSearchDao.insert(
                CutterSearch(
                    originalSearch = originalSearch,
                    result = cutter,
                    cutterUsedText = cutterUsedText
                )
            )
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

/**
 * Immutable UI state.
 */
data class MainState(
    val dbType: Int = 0,
    val numCutterResult: String = "",
    val cutterUsed: String = "",
    val name: String = "",
    val lastName: String = "",
    val isLoading: Boolean = false,
    val isVisible: Boolean = false,
    val errorMessage: String? = null,
)