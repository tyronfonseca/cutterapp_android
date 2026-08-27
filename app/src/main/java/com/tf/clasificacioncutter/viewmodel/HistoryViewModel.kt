package com.tf.clasificacioncutter.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tf.clasificacioncutter.data.AppDatabase
import com.tf.clasificacioncutter.data.CutterSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val cutterSearchDao = db.cutterSearchDao()

    var searchQuery by mutableStateOf("")
        private set

    var selectedIds by mutableStateOf(setOf<Long>())
        private set

    private val _history = cutterSearchDao.getAllSearches()

    private val _queryFlow = MutableStateFlow("")
    
    val filteredHistory: Flow<List<CutterSearch>> = combine(_history, _queryFlow) { list, query ->
        filterHistory(list, query)
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        _queryFlow.value = newQuery
    }

    private fun filterHistory(list: List<CutterSearch>, query: String): List<CutterSearch> {
        if (query.isBlank()) return list
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        
        return list.filter { item ->
            val dateStr = sdf.format(Date(item.timestamp))
            item.result.contains(query, ignoreCase = true) ||
                    item.cutterUsedText.contains(query, ignoreCase = true) ||
                    item.originalSearch.contains(query, ignoreCase = true) ||
                    dateStr.contains(query)
        }
    }

    fun toggleSelection(id: Long) {
        selectedIds = if (selectedIds.contains(id)) {
            selectedIds - id
        } else {
            selectedIds + id
        }
    }

    fun clearSelection() {
        selectedIds = emptySet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            cutterSearchDao.deleteSearches(selectedIds.toList())
            clearSelection()
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            cutterSearchDao.deleteAllSearches()
            clearSelection()
        }
    }
}
