package com.tf.clasificacioncutter.ui.screens.cuttertable

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.ui.components.CutterTextField
import com.tf.clasificacioncutter.ui.screens.main.LocalMainViewModel
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val RowBadgeShape = RoundedCornerShape(8.dp)

@Immutable
sealed class CutterTableItem(val key: String) {
    data class Header(val letter: String) : CutterTableItem("header_$letter")
    data class RowItem(val cutter: CutterData) : CutterTableItem("row_${cutter.id}")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CutterTableScreen(navController: NavController) {
    val viewModel = LocalMainViewModel.current
    val uiState by viewModel.uiState.collectAsState()
    
    val items = uiState.currentCutterData
    var searchText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }

    val stateResult by produceState<Pair<Boolean, List<CutterTableItem>>>(
        initialValue = Pair(true, emptyList()),
        key1 = items,
        key2 = searchText
    ) {
        value = Pair(true, emptyList())
        val flatList = withContext(Dispatchers.Default) {
            val filtered = if (searchText.isBlank()) items else {
                items.filter { 
                    it.name.contains(searchText, ignoreCase = true) || 
                    it.code.contains(searchText, ignoreCase = true) 
                }
            }
            val grouped = filtered.groupBy { item ->
                val trimmedName = item.name.trim()
                val firstChar = trimmedName.firstOrNull()?.uppercaseChar()
                if (firstChar != null && firstChar.isLetter()) {
                    firstChar.toString()
                } else {
                    "#"
                }
            }.toSortedMap(Comparator { lhs, rhs ->
                if (lhs == "#") return@Comparator 1
                if (rhs == "#") return@Comparator -1
                lhs.compareTo(rhs)
            })

            val list = mutableListOf<CutterTableItem>()
            grouped.forEach { (header, cutters) ->
                list.add(CutterTableItem.Header(header))
                cutters.forEach { list.add(CutterTableItem.RowItem(it)) }
            }
            list
        }
        value = Pair(false, flatList)
    }

    val isProcessing = stateResult.first
    val flatItems = stateResult.second
    val isDark = isSystemInDarkTheme()
    val topBarBgColor = if (isDark) MaterialTheme.colorScheme.surfaceContainer else CutterPrimary
    val topBarContentColor = if (isDark) MaterialTheme.colorScheme.onSurface else Color.White
    val subtitleColor = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color.White.copy(alpha = 0.8f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.cutter_table))
                        Text(
                            text = uiState.currentTableSelected?.name ?: stringResource(R.string.title_sanborn),
                            style = MaterialTheme.typography.bodySmall,
                            color = subtitleColor
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.cuttertable_set_current_table)) },
                                onClick = {
                                    showMenu = false
                                    navController.navigate("select_table")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.cuttertable_add_new_table)) },
                                onClick = {
                                    showMenu = false
                                    navController.navigate("add_table")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                }
                            )
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CutterTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.cuttertable_search_prompt)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clean))
                        }
                    }
                },
                singleLine = true
            )

            if (isProcessing) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CutterPrimary)
                }
            } else if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.cuttertable_no_data))
                }
            } else if (flatItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_results))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = flatItems,
                        key = { item -> item.key },
                        contentType = { item ->
                            when (item) {
                                is CutterTableItem.Header -> "header"
                                is CutterTableItem.RowItem -> "row"
                            }
                        }
                    ) { item ->
                        when (item) {
                            is CutterTableItem.Header -> {
                                Text(
                                    text = item.letter,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            is CutterTableItem.RowItem -> {
                                CutterRow(cutter = item.cutter)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CutterRow(cutter: CutterData) {
    val badgeBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    val badgeTextColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cutter.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (cutter.bookName.isNotEmpty()) {
                Text(
                    text = cutter.bookName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = cutter.number,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = badgeTextColor,
            modifier = Modifier
                .background(badgeBgColor, RowBadgeShape)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
