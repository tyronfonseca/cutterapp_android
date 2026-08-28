package com.tf.clasificacioncutter.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.CutterSearch
import com.tf.clasificacioncutter.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel, onBack: () -> Unit) {
    val history by viewModel.filteredHistory.collectAsState(initial = emptyList())
    val selectedIds = viewModel.selectedIds
    val isSelectionMode = selectedIds.isNotEmpty()
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteAllConfirm by remember { mutableStateOf(false) }

    BackHandler(enabled = isSelectionMode) {
        viewModel.clearSelection()
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                SelectionTopBar(
                    count = selectedIds.size,
                    onClearSelection = { viewModel.clearSelection() },
                    onDelete = { viewModel.deleteSelected() }
                )
            } else {
                HistoryTopBar(
                    onBack = onBack,
                    onDeleteAll = { showMenu = true },
                    showMenu = showMenu,
                    onDismissMenu = { showMenu = false },
                    onConfirmDeleteAll = {
                        showMenu = false
                        showDeleteAllConfirm = true
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!isSelectionMode) {
                SearchBar(
                    query = viewModel.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) }
                )
            }

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (viewModel.searchQuery.isEmpty())
                            stringResource(R.string.empty_history)
                        else stringResource(R.string.no_results_found),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(history, key = { it.id }) { item ->
                        HistoryItem(
                            item = item,
                            isSelected = selectedIds.contains(item.id),
                            onSelect = { viewModel.toggleSelection(item.id) },
                            isSelectionMode = isSelectionMode
                        )
                    }
                }
            }
        }
    }

    if (showDeleteAllConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteAllConfirm = false },
            title = { Text(stringResource(R.string.delete_history_title)) },
            text = { Text(stringResource(R.string.delete_history_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAll()
                        showDeleteAllConfirm = false
                    }
                ) {
                    Text(stringResource(R.string.delete_history_btn_delete),
                        color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopBar(
    onBack: () -> Unit,
    onDeleteAll: () -> Unit,
    showMenu: Boolean,
    onDismissMenu: () -> Unit,
    onConfirmDeleteAll: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.search_history_title)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back_btn)
                )
            }
        },
        actions = {
            IconButton(onClick = onDeleteAll) {
                Icon(Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.menu))
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = onDismissMenu
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.menu_delete_all_search_history),
                        color = MaterialTheme.colorScheme.error) },
                    onClick = onConfirmDeleteAll
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    count: Int,
    onClearSelection: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        title = { Text("$count seleccionados") },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        },
        actions = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar")
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryItem(
    item: CutterSearch,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isSelectionMode: Boolean
) {
    val dateString = remember(item.timestamp) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { if (isSelectionMode) onSelect() },
                onLongClick = { onSelect() }
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = if (isSelected) BorderStroke(1.dp
            , MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelectionMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onSelect() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = item.result,
                        color = contentColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(${item.cutterUsedText})",
                        modifier = Modifier.padding(start = 8.dp),
                        color = if (isSelected) contentColor else MaterialTheme.colorScheme.secondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = dateString,
                    color = if (isSelected) contentColor else MaterialTheme.colorScheme.outline,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.originalSearch,
                color = if (isSelected) contentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
    }
}