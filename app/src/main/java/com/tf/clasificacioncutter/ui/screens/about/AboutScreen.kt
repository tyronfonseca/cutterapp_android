package com.tf.clasificacioncutter.ui.screens.about

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.utils.extensions.rememberDrawablePainter
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val cardBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )

    val isDark = isSystemInDarkTheme()
    val topBarBgColor = if (isDark) MaterialTheme.colorScheme.surfaceContainer else CutterPrimary
    val topBarContentColor = if (isDark) MaterialTheme.colorScheme.onSurface else Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // MARK: - Header / About Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = cardBorder,
                colors = cardColors
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = rememberDrawablePainter(id = R.mipmap.ic_launcher),
                            contentDescription = stringResource(R.string.logo_description),
                            modifier = Modifier.size(48.dp)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.cutter_app),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            val appVersion = remember {
                                try {
                                    context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "2.0.0"
                                } catch (_: Exception) {
                                    "2.0.0"
                                }
                            }
                            Text(
                                text = "Version $appVersion",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.app_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // MARK: - FAQ Section
            Text(
                text = stringResource(R.string.faq_header),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            FaqItem(
                question = stringResource(R.string.about_question_functionality),
                answer = stringResource(R.string.about_functionality_description)
            )

            FaqItem(
                question = stringResource(R.string.about_question_change_table),
                answer = stringResource(R.string.about_question_change_table_answer)
            )

            FaqItem(
                question = stringResource(R.string.about_question_switch_table),
                answer = "${stringResource(R.string.about_question_switch_table_answer_1)}\n\n" +
                         "${stringResource(R.string.about_question_switch_table_answer_2)}\n" +
                         "${stringResource(R.string.about_question_switch_table_answer_3)}\n\n" +
                         stringResource(R.string.about_question_switch_table_answer_4, stringResource(R.string.cutter_table))
            )

            FaqItem(
                question = stringResource(R.string.about_question_import_table),
                answer = "${stringResource(R.string.about_question_import_table_ans_1)}\n\n" +
                         "${stringResource(R.string.about_question_import_table_ans_2)}\n\n" +
                         "${stringResource(R.string.about_question_import_table_ans_3)}\n\n" +
                         stringResource(R.string.about_question_import_table_ans_4)
            )

            FaqItem(
                question = stringResource(R.string.about_question_active_table),
                answer = stringResource(R.string.about_how_change_table, stringResource(R.string.cutter_table), stringResource(R.string.cuttertable_set_current_table))
            )

            FaqItem(
                question = stringResource(R.string.about_question_barcode),
                answer = "${stringResource(R.string.about_question_barcode_ans_1)}\n\n" +
                         "${stringResource(R.string.about_question_barcode_ans_2)}\n\n" +
                         stringResource(R.string.about_question_barcode_ans_3)
            )

            FaqItem(
                question = stringResource(R.string.about_question_customize),
                answer = "${stringResource(R.string.about_question_customize_ans_1)}\n\n" +
                         "${stringResource(R.string.about_question_customize_ans_2)}\n" +
                         "${stringResource(R.string.about_question_customize_ans_3)}\n" +
                         stringResource(R.string.about_question_customize_ans_4)
            )

            FaqItem(
                question = stringResource(R.string.about_question_articles),
                answer = "${stringResource(R.string.about_question_articles_ans_1)}\n\n" +
                         "• <b>${stringResource(R.string.english)}:</b> the, a, an.\n" +
                         "• <b>${stringResource(R.string.spanish)}:</b> el, la, los, las, un, una, unos, unas.\n" +
                         "• <b>${stringResource(R.string.portuguese)}:</b> o, a, os, as, um, uma."
            )

            FaqItem(
                question = stringResource(R.string.about_question_export),
                answer = stringResource(R.string.about_question_export_ans_1)
            )

            // MARK: - Support Section
            Text(
                text = stringResource(R.string.about_question_support),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:contactfonsecasoftware@gmail.com".toUri()
                            putExtra(Intent.EXTRA_SUBJECT, "Cutter App Support")
                        }
                        context.startActivity(Intent.createChooser(intent, "Contact Support"))
                    },
                border = cardBorder,
                colors = cardColors
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = stringResource(R.string.contact),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.contact),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "contactfonsecasoftware@gmail.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // MARK: - Open Source & Credits
            Text(
                text = stringResource(R.string.open_source),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                border = cardBorder,
                colors = cardColors
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.cutter_app_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    SelectionContainer {
                        Text(
                            text = AnnotatedString.fromHtml(
                                htmlString = stringResource(R.string.agradecimiento),
                                linkStyles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW,
                                    "https://github.com/tyronfonseca/cutterapp_android".toUri())
                                context.startActivity(intent)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = stringResource(R.string.about_question_github),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.about_question_github),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    val cardBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        border = cardBorder,
        colors = cardColors
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = AnnotatedString.fromHtml(answer),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
