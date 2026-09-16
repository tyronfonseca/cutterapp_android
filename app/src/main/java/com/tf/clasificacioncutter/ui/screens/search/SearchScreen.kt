package com.tf.clasificacioncutter.ui.screens.search

import android.content.res.Configuration
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.settings.AppSettings
import com.tf.clasificacioncutter.ui.components.ContentUnavailable
import com.tf.clasificacioncutter.ui.components.SearchItemView
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    val appSettings = remember { AppSettings(context) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (LocalInspectionMode.current) {
                        Image(
                            painter = painterResource(id = android.R.drawable.star_on),
                            contentDescription = stringResource(R.string.descripcion_imagen),
                            modifier = Modifier.width(150.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.long_logo_white),
                            contentDescription = stringResource(R.string.descripcion_imagen),
                            modifier = Modifier.width(150.dp),
                            colorFilter = ColorFilter.tint(
                                if (isDark) Color.White else CutterPrimary
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (uiState.isSelectionMode) {
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel")
                        }
                    }
                },
                actions = {
                    if (uiState.isSelectionMode) {
                        IconButton(
                            onClick = { viewModel.deleteSelected() },
                            enabled = uiState.selectedItemIds.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Selected")
                        }
                    } else {
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Select items")
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete all") },
                                    onClick = {
                                        viewModel.deleteAll()
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
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        floatingActionButton = {
            // Share FAB (only visible when there are history items to share)
            if (uiState.searchHistory.isNotEmpty() && !uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        print("Supe")
                    },
                    containerColor = CutterPrimary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save to CSV"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!uiState.isSelectionMode) {
                TextField(
                    value = uiState.searchText,
                    onValueChange = { viewModel.onSearchTextChange(it) },
                    label = { Text("Author name or ISBN") }
                )

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.search()
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
                        Text("Scan")
                    }

                    Button(
                        onClick = {
                            viewModel.search()
                        }, colors = ButtonDefaults.buttonColors(
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
                        Text("Search")
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            // List Content or Empty State
            if (uiState.filteredHistory.isNotEmpty()) {

                if (!uiState.isSelectionMode) {
                    FilterSortSection(
                        filter = uiState.filter,
                        onFilterChange = { viewModel.onFilterChange(it) },
                        sort = uiState.sort,
                        onSortChange = { viewModel.onSortChange(it) }
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.filteredHistory,
                        key = { item -> item.id }
                    ) { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = uiState.isSelectionMode) {
                                    viewModel.toggleItemSelection(item.id)
                                }
                        ) {
                            if (uiState.isSelectionMode) {
                                Checkbox(
                                    checked = uiState.selectedItemIds.contains(item.id),
                                    onCheckedChange = { viewModel.toggleItemSelection(item.id) },
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            SearchItemView(
                                item = item,
                                settings = appSettings,
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
                        title = "No entries yet",
                        description = "Add an entry by scanning or entering an author name or ISBN",
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
        SearchScreen()
    }
}

@Composable
fun FilterSortSection(
    filter: SearchHistoryFilter,
    onFilterChange: (SearchHistoryFilter) -> Unit,
    sort: SearchHistorySort,
    onSortChange: (SearchHistorySort) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuPicker(
            currentValue = filter.name.lowercase().replaceFirstChar { it.uppercase() }
                .replace("_", " "),
            options = SearchHistoryFilter.entries,
            onOptionSelected = onFilterChange,
            modifier = Modifier.weight(1f),
            icon = Icons.Default.FilterAlt
        )
        MenuPicker(
            currentValue = sort.name.lowercase().replaceFirstChar { it.uppercase() }
                .replace("_", " "),
            options = SearchHistorySort.entries,
            onOptionSelected = onSortChange,
            modifier = Modifier.weight(1f),
            icon = Icons.AutoMirrored.Filled.Sort
        )
    }
}

@Composable
fun <T> MenuPicker(
    currentValue: String,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Filled.Sort
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = currentValue, style = MaterialTheme.typography.bodyMedium)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.45f) // Adjust width as needed
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.toString().lowercase().replaceFirstChar { it.uppercase() }
                                .replace("_", " ")
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
