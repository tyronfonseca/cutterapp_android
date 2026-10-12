package com.tf.clasificacioncutter.ui.screens.search

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.model.CutterSearchOptions
import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.data.model.cutterdata.CutterDataDao
import com.tf.clasificacioncutter.data.model.cutterdata.toDomain
import com.tf.clasificacioncutter.data.model.cutterdata.toEntity
import com.tf.clasificacioncutter.data.model.openlibrary.BookDoc
import com.tf.clasificacioncutter.data.model.openlibrary.IsbnCacheDao
import com.tf.clasificacioncutter.data.model.openlibrary.IsbnCacheEntity
import com.tf.clasificacioncutter.data.model.openlibrary.OpenLibraryResponse
import com.tf.clasificacioncutter.ui.screens.main.MainViewModel
import com.tf.clasificacioncutter.utils.CutterGetter
import com.tf.clasificacioncutter.utils.csv.CSVHelper
import com.tf.clasificacioncutter.utils.extensions.extractISBNs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
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
    val filteredHistory: List<CutterData> = emptyList(),
    val isLoadingHistory: Boolean = true,
    val filter: SearchHistoryFilter = SearchHistoryFilter.ALL,
    val sort: SearchHistorySort = SearchHistorySort.NEWEST,
    val isSelectionMode: Boolean = false,
    val selectedItemIds: Set<UUID> = emptySet(),
    val showDeleteAlert: Boolean = false,
    val isSearchingISBN: Boolean = false,
    val errorMessage: String? = null,
    val showErrorAlert: Boolean = false
)

class SearchViewModel(
    private val cutterDataDao: CutterDataDao,
    private val isbnCacheDao: IsbnCacheDao? = null
) : ViewModel() {
    private val TAG = "SearchViewModel"

    companion object {
        fun provideFactory(dao: CutterDataDao, cacheDao: IsbnCacheDao? = null): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(dao, cacheDao) as T
            }
        }
    }

    init {
        viewModelScope.launch {
            cutterDataDao.getAllSearches().collect { searches ->
                val domainHistory = searches.map { it.toDomain() }
                uiState = uiState.copy(searchHistory = domainHistory)
                updateFilteredHistory()
            }
        }
    }

    var uiState by mutableStateOf(SearchUiState())
        private set

    fun onSearchTextChange(text: String) {
        uiState = uiState.copy(searchText = text)
    }

    fun onFilterChange(filter: SearchHistoryFilter) {
        uiState = uiState.copy(filter = filter)
        updateFilteredHistory()
    }

    fun onSortChange(sort: SearchHistorySort) {
        uiState = uiState.copy(sort = sort)
        updateFilteredHistory()
    }

    private fun updateFilteredHistory() {
        viewModelScope.launch(Dispatchers.Default) {
            val history = uiState.searchHistory
            val filter = uiState.filter
            val sort = uiState.sort

            var result = when (filter) {
                SearchHistoryFilter.ALL -> history
                SearchHistoryFilter.NEEDS_REVIEW -> history.filter { it.needsReview }
                SearchHistoryFilter.REVIEWED -> history.filter { !it.needsReview }
            }

            result = when (sort) {
                SearchHistorySort.A_Z -> result.sortedBy { it.searchValue }
                SearchHistorySort.NEWEST -> result.sortedByDescending { it.timestamp }
                SearchHistorySort.OLDEST -> result.sortedBy { it.timestamp }
            }

            withContext(Dispatchers.Main) {
                uiState = uiState.copy(
                    filteredHistory = result,
                    isLoadingHistory = false
                )
            }
        }
    }

    fun addHistoryItem(item: CutterData) {
        viewModelScope.launch {
            cutterDataDao.insert(item.toEntity())
        }
        uiState = uiState.copy(
            searchText = ""
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

    fun updateItem(item: CutterData) {
        viewModelScope.launch {
            cutterDataDao.update(item.toEntity())
        }
    }

    fun deleteItem(id: UUID) {
        viewModelScope.launch {
            cutterDataDao.deleteSearches(listOf(id))
        }
    }

    fun deleteSelected() {
        val idsToDelete = uiState.selectedItemIds.toList()
        viewModelScope.launch {
            cutterDataDao.deleteSearches(idsToDelete)
        }
        uiState = uiState.copy(
            selectedItemIds = emptySet(),
            isSelectionMode = false
        )
    }

    fun deleteAll() {
        viewModelScope.launch {
            cutterDataDao.deleteAllSearches()
        }
        uiState = uiState.copy(
            selectedItemIds = emptySet(),
            isSelectionMode = false
        )
    }

    fun search(mainViewModel: MainViewModel) {
        val text = uiState.searchText.trim()
        if (text.isEmpty()) return

        val isbn = text.extractISBNs().firstOrNull()
        if (isbn != null) {
            authorByISBN(isbn, mainViewModel)
            return
        }

        val query = CutterGetter.splitText(text)
        val options = CutterSearchOptions(settings = mainViewModel.settings)

        val result = mainViewModel.search(
            name = query.first,
            lastName = query.second,
            options = options
        )

        if (result != null) {
            val finalResult = result.copy(
                id = UUID.randomUUID(),
                authorName = query.first,
                useFormatMac = options.useFormatMac,
                ignoreArticles = options.ignoreArticles,
                dontSeparateName = options.dontSeparateName
            )
            finalResult.authorSurname = query.second
            addHistoryItem(finalResult)
        } else {
            presentError("No cutter match found for '$text'.")
        }
    }

    private fun presentError(message: String) {
        uiState = uiState.copy(
            errorMessage = message,
            showErrorAlert = true
        )
    }

    private fun authorByISBN(isbn: String, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            uiState = uiState.copy(isSearchingISBN = true)
            try {
                val book = fetchBookByISBN(isbn)
                if (book != null) {
                    val title = book.title ?: "Unknown Title"
                    val author = book.authorName?.firstOrNull() ?: ""
                    val ddcList = book.ddc ?: emptyList()

                    val nameToSearch = author.ifEmpty { title }
                    val query = CutterGetter.splitText(nameToSearch)
                    val options = CutterSearchOptions(settings = mainViewModel.settings)

                    val result = mainViewModel.search(
                        name = query.first,
                        lastName = query.second,
                        options = options
                    )

                    if (result != null) {
                        val finalResult = result.copy(
                            id = UUID.randomUUID(),
                            authorName = query.first,
                            bookName = title,
                            ddcs = ddcList,
                            isbn = isbn,
                            needsReview = true,
                            ddcSelected = ddcList.firstOrNull() ?: "",
                            useFormatMac = options.useFormatMac,
                            ignoreArticles = options.ignoreArticles,
                            dontSeparateName = options.dontSeparateName
                        )
                        finalResult.authorSurname = query.second
                        addHistoryItem(finalResult)
                    } else {
                        presentError("Cutter code generation failed for '$title'.")
                    }
                } else {
                    presentError("No book found for ISBN: $isbn")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error fetching ISBN", e)
                presentError("Network error: ${e.localizedMessage}")
            } finally {
                uiState = uiState.copy(isSearchingISBN = false)
            }
        }
    }

    private suspend fun fetchBookByISBN(isbn: String): BookDoc? = withContext(Dispatchers.IO) {
        val sixMonthsMs = 180L * 24 * 60 * 60 * 1000
        if (isbnCacheDao != null) {
            val cached = isbnCacheDao.getCache(isbn)
            if (cached != null) {
                val age = System.currentTimeMillis() - cached.timestamp
                if (age < sixMonthsMs) {
                    try {
                        val response = Gson().fromJson(cached.jsonResponse, OpenLibraryResponse::class.java)
                        Log.d(TAG, "Serving ISBN $isbn from local cache (age: ${age / (1000 * 60 * 60 * 24)} days)")
                        return@withContext response.docs.firstOrNull()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse cached JSON for ISBN $isbn", e)
                    }
                }
            }
        }

        val urlString = "https://openlibrary.org/search.json?q=isbn:$isbn&fields=title,author_name,ddc"
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.setRequestProperty("User-Agent", "CutterApp/2.0.0 (Android; contactfonsecasoftware@gmail.com)")
            urlConnection.setRequestProperty("Accept", "application/json")
            urlConnection.connectTimeout = 15000
            urlConnection.readTimeout = 15000

            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = urlConnection.inputStream
                val responseString = inputStream.bufferedReader().use { it.readText() }

                isbnCacheDao?.insertCache(
                    IsbnCacheEntity(
                        isbn = isbn,
                        jsonResponse = responseString,
                        timestamp = System.currentTimeMillis()
                    )
                )

                val response = Gson().fromJson(responseString, OpenLibraryResponse::class.java)
                return@withContext response.docs.firstOrNull()
            }
        } catch (e: Exception) {
            throw e
        } finally {
            urlConnection?.disconnect()
        }
        return@withContext null
    }

    fun exportToCSV(mainViewModel: MainViewModel) {
        viewModelScope.launch {
            val context = mainViewModel.getApplication<android.app.Application>().applicationContext
            val isSuccess = CSVHelper.exportCutterDataToCSV(
                context = context,
                data = uiState.searchHistory,
                settings = mainViewModel.settings
            )

            if (isSuccess) {
                Toast.makeText(context, R.string.export_success_toast, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, R.string.export_error_toast, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Failed to export search history to CSV")
            }
        }
    }
}
