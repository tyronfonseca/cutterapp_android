package com.tf.clasificacioncutter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterSurfaceMenu
import com.tf.clasificacioncutter.ui.theme.CutterTextSecondary
import com.tf.clasificacioncutter.ui.theme.CutterTheme

@Composable
fun DialogWithTextField(
    initialText: String,
    onDismissRequest: () -> Unit,
    onUseText: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        DialogWithTextFieldContent(
            initialText = initialText,
            onDismissRequest = onDismissRequest,
            onUseText = onUseText
        )
    }
}

@Composable
fun DialogWithTextFieldContent(
    initialText: String,
    onDismissRequest: () -> Unit,
    onUseText: (String) -> Unit
) {
    var currentText by remember { mutableStateOf(initialText) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CutterSurfaceMenu
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.text_selected_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SelectionContainer {
                CutterTextField(
                    value = currentText,
                    onValueChange = { currentText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.cancel), color = CutterTextSecondary)
                }
                Button(
                    onClick = {
                        onUseText(currentText)
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = stringResource(R.string.use_this),
                        color = CutterPrimary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DialogWithTextFieldPreview() {
    CutterTheme {
        DialogWithTextFieldContent(
            initialText = "Testing",
            onDismissRequest = {},
            onUseText = {}
        )
    }
}