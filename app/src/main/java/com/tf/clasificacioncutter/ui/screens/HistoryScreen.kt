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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tf.clasificacioncutter.data.CutterSearch
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterTextSecondary
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
        containerColor = CutterPrimary
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
                            "No hay búsquedas guardadas" 
                        else "No hay resultados para la búsqueda",
                        color = CutterTextSecondary,
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
            title = { Text("Eliminar todo el historial") },
            text = { Text("¿Estás seguro de que deseas eliminar todas las búsquedas guardadas? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAll()
                        showDeleteAllConfirm = false
                    }
                ) {
                    Text("Eliminar todo", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllConfirm = false }) {
                    Text("Cancelar")
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
        title = { Text("Historial de Búsquedas", color = Color.White) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onDeleteAll) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menú", tint = Color.White)
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = onDismissMenu
            ) {
                DropdownMenuItem(
                    text = { Text("Eliminar todo el historial", color = Color.Red) },
                    onClick = onConfirmDeleteAll
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CutterPrimary
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
        title = { Text("$count seleccionados", color = Color.White) },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.DarkGray
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
        placeholder = { Text("Buscar por fecha, resultado o cutter...", color = CutterTextSecondary) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CutterTextSecondary) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = CutterTextSecondary)
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = CutterTextSecondary,
            cursorColor = Color.White
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { if (isSelectionMode) onSelect() },
                onLongClick = { onSelect() }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White.copy(alpha = 0.2f) 
                             else Color.White.copy(alpha = 0.1f)
        ),
        border = if (isSelected) BorderStroke(1.dp, Color.White) else null
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
                                checkedColor = Color.White,
                                checkmarkColor = CutterPrimary
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = item.result,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(${item.cutterUsedText})",
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = dateString,
                    color = CutterTextSecondary,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Búsqueda: ${item.originalSearch}",
                color = CutterTextSecondary,
                fontSize = 16.sp
            )
        }
    }
}
