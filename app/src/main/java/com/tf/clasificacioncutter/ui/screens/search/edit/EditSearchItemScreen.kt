package com.tf.clasificacioncutter.ui.screens.search.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.model.CutterSearchOptions
import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.ui.components.CutterTextField
import com.tf.clasificacioncutter.ui.screens.main.LocalMainViewModel
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSearchItemScreen(
    navController: NavController,
    item: CutterData,
    onSave: (CutterData) -> Unit,
    onDelete: (UUID) -> Unit
) {
    val mainViewModel = LocalMainViewModel.current
    var currentItem by remember { mutableStateOf(item.copy()) }
    var showDeleteAlert by remember { mutableStateOf(false) }

    val cardBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
    val dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

    var isUserInteraction by remember { mutableStateOf(false) }

    LaunchedEffect(
        currentItem.authorName,
        currentItem.authorSurname,
        currentItem.dontSeparateName,
        currentItem.ignoreArticles,
        currentItem.useFormatMac
    ) {
        if (!isUserInteraction) {
            isUserInteraction = true
            return@LaunchedEffect
        }

        val options = CutterSearchOptions(
            settings = mainViewModel.settings,
            dontSeparateName = currentItem.dontSeparateName,
            ignoreArticles = currentItem.ignoreArticles,
            useFormatMac = currentItem.useFormatMac
        )

        val newCutter = mainViewModel.search(
            name = currentItem.authorName,
            lastName = currentItem.authorSurname,
            options = options
        )
        if (newCutter != null) {
            currentItem = currentItem.copy(
                code = newCutter.code,
                name = newCutter.name,
                needsReview = false
            )
        }
    }

    LaunchedEffect(currentItem) {
        onSave(currentItem)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_edit_cutter)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back_btn)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteAlert = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = Color.Red
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            if (currentItem.needsReview) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFA500).copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = stringResource(R.string.edit_review_required),
                            tint = Color(0xFFFFA500)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.edit_review_required),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.edit_review_required_message),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = { currentItem = currentItem.copy(needsReview = false) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.close),
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }

            // MARK: - Cutter Number Section
            Text(
                text = stringResource(R.string.edit_cutter_number_used),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            CutterTextField(
                value = currentItem.cutterUsed.ifEmpty { stringResource(R.string.edit_no_cutter_code) },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                singleLine = true
            )

            // MARK: - Author Section
            Text(
                text = stringResource(R.string.author),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
            )
            CutterTextField(
                value = currentItem.authorSurname,
                onValueChange = { 
                    currentItem.authorSurname = it 
                    currentItem = currentItem.copy() 
                },
                label = { Text(if (currentItem.dontSeparateName) stringResource(R.string.name) else stringResource(R.string.surname)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                singleLine = true
            )

            if (!currentItem.dontSeparateName) {
                Spacer(modifier = Modifier.height(12.dp))
                CutterTextField(
                    value = currentItem.authorName,
                    onValueChange = { currentItem = currentItem.copy(authorName = it) },
                    label = { Text(stringResource(R.string.edit_name_optional)) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    singleLine = true
                )
            }

            Text(
                text = stringResource(R.string.edit_verify_author_name),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // MARK: - Options Section
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                border = cardBorder,
                colors = cardColors
            ) {
                Column {
                    EditSwitchRow(
                        title = stringResource(R.string.edit_author_has_no_surname),
                        checked = currentItem.dontSeparateName,
                        onCheckedChange = { currentItem = currentItem.copy(dontSeparateName = it) }
                    )
                }
            }

            Text(
                text = stringResource(R.string.edit_toggle_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                border = cardBorder,
                colors = cardColors
            ) {
                Column {
                    EditSwitchRow(
                        title = stringResource(R.string.settings_searchBehavior_ignoreArticles),
                        checked = currentItem.ignoreArticles,
                        onCheckedChange = { currentItem = currentItem.copy(ignoreArticles = it) }
                    )

                    HorizontalDivider(color = dividerColor, modifier = Modifier.padding(horizontal = 16.dp))

                    EditSwitchRow(
                        title = stringResource(R.string.settings_searchBehavior_spellAsMac),
                        checked = currentItem.useFormatMac,
                        onCheckedChange = { currentItem = currentItem.copy(useFormatMac = it) }
                    )
                }
            }

            // MARK: - Book Section
            Text(
                text = stringResource(R.string.book),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
            )
            CutterTextField(
                value = currentItem.bookName,
                onValueChange = { currentItem = currentItem.copy(bookName = it) },
                label = { Text(stringResource(R.string.edit_add_or_modify_book)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                singleLine = true
            )

            if (currentItem.ddcs.isNotEmpty()) {
                var ddcExpanded by remember { mutableStateOf(false) }

                Text(
                    text = stringResource(R.string.edit_possible_ddc),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = ddcExpanded,
                    onExpandedChange = { ddcExpanded = !ddcExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    CutterTextField(
                        value = currentItem.ddcSelected.ifEmpty { currentItem.ddcs.firstOrNull() ?: "" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.edit_possible_ddc)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ddcExpanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = ddcExpanded,
                        onDismissRequest = { ddcExpanded = false }
                    ) {
                        currentItem.ddcs.forEach { ddc ->
                            DropdownMenuItem(
                                text = { Text(ddc) },
                                onClick = {
                                    currentItem = currentItem.copy(ddcSelected = ddc)
                                    ddcExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (currentItem.isbn.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.isbn),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
                )
                CutterTextField(
                    value = currentItem.isbn,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    singleLine = true
                )

                Text(
                    text = stringResource(R.string.edit_data_warning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        if (showDeleteAlert) {
            AlertDialog(
                onDismissRequest = { showDeleteAlert = false },
                title = { Text(stringResource(R.string.edit_delete_item_title)) },
                text = { Text(stringResource(R.string.edit_delete_item_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        onDelete(currentItem.id)
                        showDeleteAlert = false
                        navController.popBackStack()
                    }) {
                        Text(stringResource(R.string.delete), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAlert = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun EditSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
