package com.tf.clasificacioncutter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tf.clasificacioncutter.ui.theme.CutterText
import com.tf.clasificacioncutter.ui.theme.CutterTextSecondary

@Composable
fun CutterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done,
        keyboardType = KeyboardType.Text
    )
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier.width(280.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = CutterText,
            unfocusedTextColor = CutterText,
            focusedBorderColor = CutterText,
            unfocusedBorderColor = CutterTextSecondary,
            cursorColor = CutterText,
            selectionColors = TextSelectionColors(
                handleColor = CutterText,
                backgroundColor = CutterText.copy(alpha = 0.4f)
            )
            
        ),
        keyboardOptions = keyboardOptions
    )
}

@Preview(showBackground = true)
@Composable
fun CutterTextFieldPreview() {
    Surface(color = Color(0xFF121212)) {
        Column {
            CutterTextField(
                value = "Testing",
                onValueChange = {},
                label = {Text("Label test")}
            )
            CutterTextField(
                value = "",
                onValueChange = {},
                label = {Text("Label test")}
            )
        }
    }
}
