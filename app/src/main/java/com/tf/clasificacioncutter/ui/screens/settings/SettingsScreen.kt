package com.tf.clasificacioncutter.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.data.model.TextBeforeAfterNum
import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.ui.components.SearchItemView
import com.tf.clasificacioncutter.ui.screens.main.LocalMainViewModel
import com.tf.clasificacioncutter.ui.theme.CutterPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel = LocalMainViewModel.current
    val settings = viewModel.settings
    val uiState by viewModel.uiState.collectAsState()

    val cutterExample = remember {
        CutterData(
            name = "Borg",
            code = "732",
            authorName = "Jorge Luis",
            _authorSurname = "Borges",
            bookName = "Ficciones"
        )
    }

    val cardBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
    val dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

    val isDark = isSystemInDarkTheme()
    val topBarBgColor = if (isDark) MaterialTheme.colorScheme.surfaceContainer else CutterPrimary
    val topBarContentColor = if (isDark) MaterialTheme.colorScheme.onSurface else Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
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
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // MARK: - Cutter Table Selection
            Text(
                text = stringResource(R.string.cutter_table),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                border = cardBorder,
                colors = cardColors
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("select_table") }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_cutterTable_activeTable),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = uiState.currentTableSelected?.name ?: stringResource(R.string.settings_cutterTable_defaultName),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(color = dividerColor, modifier = Modifier.padding(horizontal = 16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("add_table") }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.settings_cutterTable_add),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.settings_cutterTable_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = dividerColor)

            // MARK: - Search Behavior Settings
            Text(
                text = stringResource(R.string.settings_searchBehavior_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                border = cardBorder,
                colors = cardColors
            ) {
                Column {
                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_searchBehavior_noSurname),
                        hint = stringResource(R.string.settings_hint_no_surname),
                        checked = settings.dontSeparateName,
                        onCheckedChange = { settings.dontSeparateName = it }
                    )
                    HorizontalDivider(color = dividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_searchBehavior_ignoreArticles),
                        hint = stringResource(R.string.settings_hint_ignore_articles),
                        checked = settings.ignoreArticles,
                        onCheckedChange = { settings.ignoreArticles = it }
                    )
                    HorizontalDivider(color = dividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_searchBehavior_spellAsMac),
                        hint = stringResource(R.string.settings_hint_spell_mac),
                        checked = settings.useFormatMac,
                        onCheckedChange = { settings.useFormatMac = it }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = dividerColor)

            // MARK: - Preview Section
            Text(
                text = stringResource(R.string.settings_preview_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                border = cardBorder,
                colors = cardColors
            ) {
                SearchItemView(
                    item = cutterExample,
                    settings = settings,
                    showAction = false
                )
            }

            Text(
                text = stringResource(R.string.settings_preview_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = dividerColor)

            // MARK: - Number Formatting (Prefix & Suffix)
            Text(
                text = stringResource(R.string.settings_number_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                border = cardBorder,
                colors = cardColors
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Prefix Section
                    Text(
                        text = stringResource(R.string.settings_number_prefix),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = settings.textBeforeNum == TextBeforeAfterNum.AUTHOR_SURNAME,
                                onClick = { settings.textBeforeNum = TextBeforeAfterNum.AUTHOR_SURNAME },
                                label = { Text(stringResource(R.string.author)) }
                            )
                            FilterChip(
                                selected = settings.textBeforeNum == TextBeforeAfterNum.TITLE,
                                onClick = { settings.textBeforeNum = TextBeforeAfterNum.TITLE },
                                label = { Text(stringResource(R.string.title)) }
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (settings.charsBeforeNum > 0) settings.charsBeforeNum-- },
                                enabled = settings.charsBeforeNum > 0
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease prefix length")
                            }
                            Text(
                                text = "${settings.charsBeforeNum}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = { if (settings.charsBeforeNum < 7) settings.charsBeforeNum++ },
                                enabled = settings.charsBeforeNum < 7
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase prefix length")
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = dividerColor)

                    // Suffix Section
                    Text(
                        text = stringResource(R.string.settings_number_suffix),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = settings.textAfterNum == TextBeforeAfterNum.AUTHOR_SURNAME,
                                onClick = { settings.textAfterNum = TextBeforeAfterNum.AUTHOR_SURNAME },
                                label = { Text(stringResource(R.string.author)) }
                            )
                            FilterChip(
                                selected = settings.textAfterNum == TextBeforeAfterNum.TITLE,
                                onClick = { settings.textAfterNum = TextBeforeAfterNum.TITLE },
                                label = { Text(stringResource(R.string.title)) }
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (settings.charsAfterNum > 0) settings.charsAfterNum-- },
                                enabled = settings.charsAfterNum > 0
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease suffix length")
                            }
                            Text(
                                text = "${settings.charsAfterNum}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = { if (settings.charsAfterNum < 7) settings.charsAfterNum++ },
                                enabled = settings.charsAfterNum < 7
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase suffix length")
                            }
                        }
                    }
                }
            }

            Text(
                text = stringResource(R.string.settings_number_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = dividerColor)

            // MARK: - Export Settings
            Text(
                text = stringResource(R.string.settings_export_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                border = cardBorder,
                colors = cardColors
            ) {
                SettingsSwitchRow(
                    title = stringResource(R.string.settings_export_includeExtras),
                    hint = stringResource(R.string.settings_hint_include_extras),
                    checked = settings.includeExtrasInExport,
                    onCheckedChange = { settings.includeExtrasInExport = it }
                )
            }
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    hint: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (hint != null) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
