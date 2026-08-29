package com.tf.clasificacioncutter.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterSurfaceMenu
import com.tf.clasificacioncutter.ui.theme.CutterText
import com.tf.clasificacioncutter.ui.theme.CutterTextSecondary
import com.tf.clasificacioncutter.ui.theme.CutterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopBar(
    onBack: () -> Unit,
    onDeleteAll: () -> Unit,
    onExportCsv: () -> Unit,
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
                onDismissRequest = onDismissMenu,
                containerColor = CutterSurfaceMenu
            ) {
                val itemColors = MenuDefaults.itemColors(
                    textColor = CutterText,
                    leadingIconColor = CutterTextSecondary
                )

                // Export CSV Item
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.menu_export_csv)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onDismissMenu()
                        onExportCsv()
                    },
                    colors = itemColors
                )

                // Delete History Item
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.menu_delete_all_search_history)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onDismissMenu()
                        onConfirmDeleteAll()
                    },
                    colors = itemColors
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CutterPrimary,
            titleContentColor = CutterText,
            navigationIconContentColor = CutterText,
            actionIconContentColor = CutterText
        )
    )
}

@Preview
@Composable
fun HistoryTopBarPreview(){
    CutterTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HistoryTopBar(
                onBack = {},
                onDeleteAll = {},
                onExportCsv = {},
                showMenu = false,
                onDismissMenu = {},
                onConfirmDeleteAll = {}
            )
            HistoryTopBar(
                onBack = {},
                onDeleteAll = {},
                onExportCsv = {},
                showMenu = true,
                onDismissMenu = {},
                onConfirmDeleteAll = {}
            )
        }
    }
}