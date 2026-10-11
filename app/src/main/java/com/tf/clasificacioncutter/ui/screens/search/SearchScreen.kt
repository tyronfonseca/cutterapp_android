package com.tf.clasificacioncutter.ui.screens.search

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.AppDatabase
import com.tf.clasificacioncutter.data.model.TextBeforeAfterNum
import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.data.settings.IAppSettings
import com.tf.clasificacioncutter.ui.components.ContentUnavailable
import com.tf.clasificacioncutter.ui.components.CutterTextField
import com.tf.clasificacioncutter.ui.components.SearchItemView
import com.tf.clasificacioncutter.ui.screens.main.LocalMainViewModel
import com.tf.clasificacioncutter.ui.screens.textrecognition.TextRecognitionActivity
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterTheme
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = viewModel(
        factory = SearchViewModel.provideFactory(
            AppDatabase.getDatabase(LocalContext.current).cutterDataDao(),
            AppDatabase.getDatabase(LocalContext.current).isbnCacheDao()
        )
    )
) {
    val mainViewModel = LocalMainViewModel.current
    SearchScreenContent(
        uiState = viewModel.uiState,
        onSearchTextChange = viewModel::onSearchTextChange,
        onFilterChange = viewModel::onFilterChange,
        onSortChange = viewModel::onSortChange,
        onSearch = { viewModel.search(mainViewModel) },
        onExport = { viewModel.exportToCSV(mainViewModel) },
        onToggleSelectionMode = viewModel::toggleSelectionMode,
        onToggleItemSelection = viewModel::toggleItemSelection,
        onDeleteSelected = viewModel::deleteSelected,
        onDeleteAll = viewModel::deleteAll,
        settings = mainViewModel.settings,
        onItemClick = { item: CutterData ->
            navController.currentBackStackEntry?.savedStateHandle?.set("cutter_data", item)
            navController.navigate("edit_search_item")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    uiState: SearchUiState,
    onSearchTextChange: (String) -> Unit,
    onFilterChange: (SearchHistoryFilter) -> Unit,
    onSortChange: (SearchHistorySort) -> Unit,
    onSearch: () -> Unit,
    onExport: () -> Unit,
    onToggleSelectionMode: () -> Unit,
    onToggleItemSelection: (UUID) -> Unit,
    onDeleteSelected: () -> Unit,
    onDeleteAll: () -> Unit,
    settings: IAppSettings,
    onItemClick: (CutterData) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val topBarBgColor = if (isDark) MaterialTheme.colorScheme.surfaceContainer else CutterPrimary
    val topBarContentColor = if (isDark) MaterialTheme.colorScheme.onSurface else Color.White

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.long_logo_white),
                        contentDescription = stringResource(R.string.descripcion_imagen),
                        modifier = Modifier.width(150.dp)
                    )
                },
                navigationIcon = {
                    if (uiState.isSelectionMode) {
                        IconButton(onClick = onToggleSelectionMode) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                        }
                    }
                },
                actions = {
                    if (uiState.isSelectionMode) {
                        IconButton(
                            onClick = onDeleteSelected,
                            enabled = uiState.selectedItemIds.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                        }
                    } else {
                        IconButton(onClick = onToggleSelectionMode) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.select_items))
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_actions))
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.menu_delete_all_search_history)) },
                                    onClick = {
                                        onDeleteAll()
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarBgColor,
                    titleContentColor = topBarContentColor,
                    navigationIconContentColor = topBarContentColor,
                    actionIconContentColor = topBarContentColor
                )
            )
        },
        floatingActionButton = {
            if (uiState.searchHistory.isNotEmpty() && !uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick = onExport,
                    containerColor = CutterPrimary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = stringResource(R.string.export_to_csv)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!uiState.isSelectionMode) {
                CutterTextField(
                    value = uiState.searchText,
                    onValueChange = onSearchTextChange,
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    },
                    trailingIcon = {
                        if (uiState.searchText.isNotEmpty()) {
                            IconButton(onClick = { onSearchTextChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clean))
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearch() }
                    )
                )

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val context = LocalContext.current
                    val scanLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val recognizedText = result.data?.getStringExtra("recognized_text")
                            if (!recognizedText.isNullOrBlank()) {
                                onSearchTextChange(recognizedText)
                                onSearch()
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(context, TextRecognitionActivity::class.java)
                            scanLauncher.launch(intent)
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = CutterPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.scan))
                    }

                    if (uiState.isSearchingISBN) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        Button(
                            onClick = onSearch,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CutterPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.search_btn))
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            if (uiState.isSearchingISBN || uiState.isLoadingHistory) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = CutterPrimary)
                        if (uiState.isSearchingISBN) {
                            Text(
                                text = stringResource(R.string.search_query),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else if (uiState.filteredHistory.isNotEmpty()) {

                if (!uiState.isSelectionMode) {
                    FilterSortSection(
                        filter = uiState.filter,
                        onFilterChange = onFilterChange,
                        sort = uiState.sort,
                        onSortChange = onSortChange
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.filteredHistory,
                        key = { item -> item.id },
                        contentType = { "search_item" }
                    ) { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (uiState.isSelectionMode) {
                                        onToggleItemSelection(item.id)
                                    } else {
                                        onItemClick(item)
                                    }
                                }
                        ) {
                            if (uiState.isSelectionMode) {
                                Checkbox(
                                    checked = uiState.selectedItemIds.contains(item.id),
                                    onCheckedChange = { onToggleItemSelection(item.id) },
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            SearchItemView(
                                item = item,
                                settings = settings,
                                showAction = !uiState.isSelectionMode
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ContentUnavailable(
                        title = stringResource(R.string.search_no_entries),
                        description = stringResource(R.string.search_no_entries_description),
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        testTag = "contentUnavailable"
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchScreenPreview() {
    CutterTheme {
        SearchScreenContent(
            uiState = SearchUiState(),
            onSearchTextChange = {},
            onFilterChange = {},
            onSortChange = {},
            onSearch = {},
            onExport = {},
            onToggleSelectionMode = {},
            onToggleItemSelection = {},
            onDeleteSelected = {},
            onDeleteAll = {},
            settings = object : IAppSettings {
                override var dontSeparateName: Boolean = false
                override var ignoreArticles: Boolean = true
                override var textBeforeNum: TextBeforeAfterNum = TextBeforeAfterNum.AUTHOR_SURNAME
                override var charsBeforeNum: Int = 1
                override var textAfterNum: TextBeforeAfterNum = TextBeforeAfterNum.AUTHOR_SURNAME
                override var charsAfterNum: Int = 0
                override var includeExtrasInExport: Boolean = true
                override var useFormatMac: Boolean = true
            },
            onItemClick = {}
        )
    }
}
