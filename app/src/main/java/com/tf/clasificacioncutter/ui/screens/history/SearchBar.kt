package com.tf.clasificacioncutter.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.components.CutterTextField
import com.tf.clasificacioncutter.ui.theme.CutterText
import com.tf.clasificacioncutter.ui.theme.CutterTextSecondary
import com.tf.clasificacioncutter.ui.theme.CutterTheme

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    CutterTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.search_placeholder),
            color = CutterTextSecondary) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null,
            tint = CutterText) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close,
                        contentDescription = stringResource(R.string.clean),
                        tint = CutterText
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun SearchBarPreview(){
    val searchVal = "Testing..."
    CutterTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchBar(query = searchVal, onQueryChange = {})
            SearchBar(query = "", onQueryChange = {})
        }
    }
}
