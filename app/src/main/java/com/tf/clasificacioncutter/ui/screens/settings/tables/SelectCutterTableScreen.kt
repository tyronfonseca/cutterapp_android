package com.tf.clasificacioncutter.ui.screens.settings.tables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.AppDatabase
import com.tf.clasificacioncutter.data.model.cuttertable.CutterTableEntity
import com.tf.clasificacioncutter.ui.screens.main.LocalMainViewModel
import com.tf.clasificacioncutter.ui.theme.CutterPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCutterTableScreen(navController: NavController) {
    val viewModel = LocalMainViewModel.current
    val uiState by viewModel.uiState.collectAsState()
    
    val db = AppDatabase.getDatabase(navController.context)
    val tables by db.cutterTableDao().getAllTables().collectAsState(initial = emptyList())
    
    var tableToDelete by remember { mutableStateOf<CutterTableEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cuttertable_nav_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back_btn)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("add_table") }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(R.string.cuttertable_add_new_table)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainer else CutterPrimary,
                    titleContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else Color.White,
                    navigationIconContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else Color.White,
                    actionIconContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Text(
                    text = stringResource(R.string.select_cutter_table_available),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }
            items(tables) { table ->
                CutterTableRow(
                    table = table,
                    isSelected = uiState.currentTableSelected?.id == table.id,
                    onClick = {
                        viewModel.selectActiveTable(table)
                        navController.popBackStack()
                    },
                    onDelete = if (!table.cannotDelete) {
                        { tableToDelete = table }
                    } else null
                )
            }
        }

        if (tableToDelete != null) {
            AlertDialog(
                onDismissRequest = { tableToDelete = null },
                title = { Text(stringResource(R.string.edit_delete_item_title)) },
                text = { Text(stringResource(R.string.edit_delete_item_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        tableToDelete?.let { viewModel.deleteTable(it) }
                        tableToDelete = null
                    }) {
                        Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { tableToDelete = null }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun CutterTableRow(
    table: CutterTableEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = table.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (table.description.isNotEmpty()) {
                Text(
                    text = table.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (isSelected) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.selected_label),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (onDelete != null) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
