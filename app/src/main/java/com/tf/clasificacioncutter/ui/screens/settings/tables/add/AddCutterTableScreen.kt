package com.tf.clasificacioncutter.ui.screens.settings.tables.add

import android.net.Uri
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch

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
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedFileUri = uri
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
                actions = {
                    if (selectedFileUri != null && name.isNotBlank()) {
                        TextButton(onClick = {
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
                        }) {
                            Text(stringResource(R.string.save))
                        }
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

            if (selectedFileUri != null) {
                Text(
                    text = "URI: $selectedFileUri",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}
