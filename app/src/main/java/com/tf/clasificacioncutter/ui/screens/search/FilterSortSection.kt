package com.tf.clasificacioncutter.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.components.MenuPicker

@Composable
fun filterLabel(filter: SearchHistoryFilter): String {
    return when (filter) {
        SearchHistoryFilter.ALL -> stringResource(R.string.filter_all)
        SearchHistoryFilter.NEEDS_REVIEW -> stringResource(R.string.filter_needs_review)
        SearchHistoryFilter.REVIEWED -> stringResource(R.string.filter_reviewed)
    }
}

@Composable
fun sortLabel(sort: SearchHistorySort): String {
    return when (sort) {
        SearchHistorySort.NEWEST -> stringResource(R.string.filter_newest_first)
        SearchHistorySort.OLDEST -> stringResource(R.string.filter_oldest_first)
        SearchHistorySort.A_Z -> stringResource(R.string.filter_author)
    }
}

@Composable
fun FilterSortSection(
    filter: SearchHistoryFilter,
    onFilterChange: (SearchHistoryFilter) -> Unit,
    sort: SearchHistorySort,
    onSortChange: (SearchHistorySort) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuPicker(
            currentValue = filterLabel(filter),
            options = SearchHistoryFilter.entries,
            onOptionSelected = onFilterChange,
            optionLabel = { filterLabel(it) },
            modifier = Modifier.weight(1f),
            icon = Icons.Default.FilterAlt
        )
        MenuPicker(
            currentValue = sortLabel(sort),
            options = SearchHistorySort.entries,
            onOptionSelected = onSortChange,
            optionLabel = { sortLabel(it) },
            modifier = Modifier.weight(1f),
            icon = Icons.AutoMirrored.Filled.Sort
        )
    }
}

@Preview
@Composable
fun FilterSortSectionPreview() {
    FilterSortSection(
        filter = SearchHistoryFilter.ALL,
        onFilterChange = {},
        sort = SearchHistorySort.A_Z,
        onSortChange = {}
    )
}
