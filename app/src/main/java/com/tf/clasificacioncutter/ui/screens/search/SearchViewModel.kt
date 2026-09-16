package com.tf.clasificacioncutter.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tf.clasificacioncutter.data.model.CutterData
import java.util.UUID

enum class SearchHistoryFilter {
    ALL, NEEDS_REVIEW, REVIEWED
}

enum class SearchHistorySort {
    A_Z, NEWEST, OLDEST
}

data class SearchUiState(
    val searchText: String = "",
    val searchHistory: List<CutterData> = emptyList(),
    val filter: SearchHistoryFilter = SearchHistoryFilter.ALL,
    val sort: SearchHistorySort = SearchHistorySort.NEWEST,
    val isSelectionMode: Boolean = false,
    val selectedItemIds: Set<UUID> = emptySet()
) {
    val filteredHistory: List<CutterData>
        get() {
            var result = when (filter) {
                SearchHistoryFilter.ALL -> searchHistory
                SearchHistoryFilter.NEEDS_REVIEW -> searchHistory.filter { it.needsReview }
                SearchHistoryFilter.REVIEWED -> searchHistory.filter { !it.needsReview }
            }

            result = when (sort) {
                SearchHistorySort.A_Z -> result.sortedBy { it.searchValue }
                SearchHistorySort.NEWEST -> result.sortedByDescending { it.timestamp }
                SearchHistorySort.OLDEST -> result.sortedBy { it.timestamp }
            }

            return result
        }
}

class SearchViewModel : ViewModel() {
    var uiState by mutableStateOf(SearchUiState())
        private set

    fun onSearchTextChange(text: String) {
        uiState = uiState.copy(searchText = text)
    }

    fun onFilterChange(filter: SearchHistoryFilter) {
        uiState = uiState.copy(filter = filter)
    }

    fun onSortChange(sort: SearchHistorySort) {
        uiState = uiState.copy(sort = sort)
    }

    fun addHistoryItem(item: CutterData) {
        uiState = uiState.copy(
            searchHistory = uiState.searchHistory + item
        )
    }

    fun toggleSelectionMode() {
        uiState = uiState.copy(
            isSelectionMode = !uiState.isSelectionMode,
            selectedItemIds = emptySet()
        )
    }

    fun toggleItemSelection(id: UUID) {
        val selected = uiState.selectedItemIds.toMutableSet()
        if (selected.contains(id)) {
            selected.remove(id)
        } else {
            selected.add(id)
        }
        uiState = uiState.copy(selectedItemIds = selected)
    }

    fun deleteSelected() {
        uiState = uiState.copy(
            searchHistory = uiState.searchHistory.filter { it.id !in uiState.selectedItemIds },
            selectedItemIds = emptySet(),
            isSelectionMode = false
        )
    }

    fun deleteAll() {
        uiState = uiState.copy(
            searchHistory = emptyList(),
            selectedItemIds = emptySet(),
            isSelectionMode = false
        )
    }
    
    fun search() {
        // Dummy logic
        addHistoryItem(
            CutterData(
                name = uiState.searchText.ifEmpty { "Test Cutter" },
                code = "123",
                authorName = "James",
                _authorSurname = "Smith",
                bookName = "The King in Yellow",
                needsReview = uiState.searchHistory.size % 2 == 0 // Dummy logic for needsReview
            )
        )
    }
}
