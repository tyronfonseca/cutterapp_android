package com.tf.clasificacioncutter.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.theme.CutterError
import com.tf.clasificacioncutter.ui.theme.CutterText
import com.tf.clasificacioncutter.ui.theme.CutterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    count: Int,
    onClearSelection: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        title = { Text("$count ${stringResource(R.string.selected_items)}") },
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(Icons.Default.Close,
                    contentDescription = stringResource(R.string.close))
            }
        },
        actions = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete
                    , contentDescription = stringResource(R.string.delete))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CutterError,
            titleContentColor = CutterText,
            navigationIconContentColor = CutterText,
            actionIconContentColor = CutterText
        )
    )
}

@Preview
@Composable
fun SelectionTopBarPreview(){
    val count = 20
    CutterTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectionTopBar(count = count, onClearSelection = {}) { }
        }
    }
}
