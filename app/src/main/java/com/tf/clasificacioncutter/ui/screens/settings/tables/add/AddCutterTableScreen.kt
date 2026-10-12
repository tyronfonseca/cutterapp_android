package com.tf.clasificacioncutter.ui.screens.settings.tables.add

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.components.CutterTextField
import com.tf.clasificacioncutter.ui.screens.main.LocalMainViewModel
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.utils.csv.CSVHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

data class CsvPreviewRow(val name: String, val code: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCutterTableScreen(navController: NavController) {
    val viewModel = LocalMainViewModel.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hasHeaders by remember { mutableStateOf(false) }
    var setSelected by remember { mutableStateOf(false) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    
    var previewRows by remember { mutableStateOf<List<CsvPreviewRow>>(emptyList()) }
    var formatError by remember { mutableStateOf<String?>(null) }
    var isLoadingPreview by remember { mutableStateOf(false) }

    val errorMsg = stringResource(R.string.add_cutter_table_error_import_description)
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedFileUri = uri
    }

    LaunchedEffect(selectedFileUri, hasHeaders) {
        if (selectedFileUri != null) {
            isLoadingPreview = true
            formatError = null
            try {
                val rows = withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(selectedFileUri!!)
                        ?: throw Exception("Cannot open input stream")
                    BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).useLines { lines ->
                        val sequence = if (hasHeaders) lines.drop(1) else lines
                        sequence.mapNotNull { row ->
                            val fields = CSVHelper.parseCSVRow(row)
                            if (fields.size < 2) return@mapNotNull null
                            val n = fields[0]
                            val c = fields[1]
                            if (n.isEmpty() && c.isEmpty()) return@mapNotNull null
                            CsvPreviewRow(name = n, code = c)
                        }.take(5).toList()
                    }
                }
                if (rows.isEmpty()) {
                    formatError = errorMsg
                    previewRows = emptyList()
                } else {
                    previewRows = rows
                }
            } catch (e: Exception) {
                Log.e("AddCutterTableScreen", "Error parsing CSV preview: ${e.localizedMessage}", e)
                formatError = errorMsg
                previewRows = emptyList()
            } finally {
                isLoadingPreview = false
            }
        } else {
            previewRows = emptyList()
            formatError = null
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_cutter_table_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back_btn)
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
            // MARK: - Load Table Section
            Text(
                text = stringResource(R.string.add_cutter_table_load_table),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CutterTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(stringResource(R.string.add_cutter_table_name)) },
                        singleLine = true
                    )

                    CutterTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(R.string.add_cutter_table_description)) },
                        modifier = Modifier.padding(top = 12.dp),
                        singleLine = true
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.add_cutter_table_csv_headers),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Switch(
                            checked = hasHeaders,
                            onCheckedChange = { hasHeaders = it }
                        )
                    }
                }
            }

            // MARK: - Set Active Section
            Spacer(modifier = Modifier.padding(top = 16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.add_cutter_table_set_current),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = setSelected,
                        onCheckedChange = { setSelected = it }
                    )
                }
            }

            // MARK: - Select CSV Section
            Spacer(modifier = Modifier.padding(top = 16.dp))

            Button(
                onClick = { launcher.launch("*/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = name.isNotBlank()
            ) {
                Text(
                    if (selectedFileUri == null) stringResource(R.string.add_cutter_table_select_csv)
                    else stringResource(R.string.add_cutter_table_load_table)
                )
            }

            Text(
                text = stringResource(R.string.add_cutter_table_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // MARK: - Preview Section & Save Button (Below Preview)
            if (selectedFileUri != null) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(
                    text = stringResource(R.string.add_cutter_table_preview),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (isLoadingPreview) {
                            Text(text = "Loading preview...", style = MaterialTheme.typography.bodyMedium)
                        } else if (formatError != null) {
                            Text(
                                text = formatError!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.name),
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = stringResource(R.string.code),
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            HorizontalDivider()
                            previewRows.forEach { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        text = row.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = row.code,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                if (formatError == null && !isLoadingPreview && previewRows.isNotEmpty()) {
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val success = viewModel.saveImportedCSV(
                                    fileUri = selectedFileUri!!,
                                    name = name,
                                    description = description,
                                    setSelected = setSelected,
                                    hasHeaders = hasHeaders
                                )
                                if (success) {
                                    Toast.makeText(context, R.string.add_cutter_table_successful, Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, R.string.add_cutter_table_error_import, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        enabled = name.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}
