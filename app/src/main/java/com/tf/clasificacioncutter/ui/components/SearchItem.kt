package com.tf.clasificacioncutter.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tf.clasificacioncutter.data.model.CutterData
import com.tf.clasificacioncutter.data.model.fullCallNumber
import com.tf.clasificacioncutter.data.settings.AppSettings
import com.tf.clasificacioncutter.ui.theme.CutterTheme

@Composable
fun SearchItemView(
    item: CutterData,
    settings: AppSettings,
    modifier: Modifier = Modifier,
    showAction: Boolean = true
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left Column: Search value and optional book name
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = item.searchValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .testTag("search_view_item_title")
                    .semantics {
                        val authorLabel = "Author"
                        contentDescription = "$authorLabel: ${item.searchValue}"
                    }
            )

            if (item.bookName.isNotEmpty()) {
                Text(
                    text = item.bookName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .testTag("search_view_item_subtitle")
                        .semantics {
                            val bookLabel = "Book" //stringResource(R.string.book)
                            contentDescription = "$bookLabel: ${item.bookName}"
                        }
                )
            }
        }

        // Right Row: Badges
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cutter Value Badge
            val callNumber = item.fullCallNumber(settings)
            Text(
                text = callNumber,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0056D2),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF0056D2).copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("searchitem_cutter_value")
                    .semantics {
                        contentDescription = "Cutter Value: $callNumber"
                    }
            )

            // Warning Icon Badge (if review needed)
            if (item.needsReview) {
                val reviewLabel = "Requires review" //stringResource(R.string.searchitem_requires_review)
                val reviewHint = "Hint" //stringResource(R.string.searchitem_requires_review_hint)

                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF59D).copy(alpha = 0.12f))
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .testTag("searchitem_cutter_warning")
                        .semantics {
                            contentDescription = "$reviewLabel. $reviewHint"
                        }
                )
            }
        }

        // Arrow right
        if (showAction) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchItemViewPreview() {
    val mockSettings = AppSettings(LocalContext.current)

    CutterTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                SearchItemView(
                    item = CutterData(
                        name = "Test Cutter",
                        code = "123",
                        authorName = "James",
                        _authorSurname = "Smith",
                        bookName = "The King in Yellow"
                    ),
                    settings = mockSettings
                )

                SearchItemView(
                    item = CutterData(
                        name = "Test Cutter",
                        code = "123",
                        authorName = "James",
                        _authorSurname = "Smith",
                        bookName = "The King in Yellow",
                        needsReview = true
                    ),
                    settings = mockSettings
                )
            }
        }
    }
}