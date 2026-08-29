package com.tf.clasificacioncutter.ui.screens.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tf.clasificacioncutter.data.CutterSearch
import com.tf.clasificacioncutter.ui.theme.CutterAccent
import com.tf.clasificacioncutter.ui.theme.CutterAccentLight
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterText
import com.tf.clasificacioncutter.ui.theme.CutterTheme
import java.text.SimpleDateFormat
import java.util.Locale

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
        sdf.format(item.timestamp)
    }

    val containerColor = if (isSelected) {
        CutterAccentLight
    } else {
        CutterAccent
    }

    val contentColor = CutterText

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { if (isSelectionMode) onSelect() },
                onLongClick = { onSelect() }
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
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
                                checkedColor = CutterPrimary,
                                checkmarkColor = CutterText
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(
                        text = item.result,
                        color = contentColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(${item.cutterUsedText})",
                        modifier = Modifier.padding(start = 8.dp),
                        color = contentColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = dateString,
                    color =contentColor,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.originalSearch,
                color = contentColor,
                fontSize = 16.sp
            )
        }
    }
}

@Preview
@Composable
fun HistoryItemPreview(){
    val item = CutterSearch(originalSearch = "Rolland, Brian",
                                result = "R888",
                            cutterUsedText = "Rolland: 888")
    CutterTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HistoryItem(
                item = item,
                isSelected = false,
                onSelect = {},
                isSelectionMode = false
            )
            HistoryItem(
                item = item,
                isSelected = true,
                onSelect = {},
                isSelectionMode = false
            )
            HistoryItem(
                item = item,
                isSelected = false,
                onSelect = {},
                isSelectionMode = true
            )
            HistoryItem(
                item = item,
                isSelected = true,
                onSelect = {},
                isSelectionMode = true
            )
        }
    }
}