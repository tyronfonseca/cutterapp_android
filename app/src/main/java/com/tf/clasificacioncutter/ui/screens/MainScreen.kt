package com.tf.clasificacioncutter.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tf.clasificacioncutter.R
import com.tf.clasificacioncutter.ui.components.CutterText
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterTextSecondary
import com.tf.clasificacioncutter.ui.theme.CutterTheme
import com.tf.clasificacioncutter.viewmodel.MainState
import com.tf.clasificacioncutter.viewmodel.MainViewModel
import kotlin.text.ifEmpty

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToCredits: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToTextRecognition: () -> Unit
) {
    MainScreenContent(
        uiState = viewModel.uiState,
        onLastNameChange = { viewModel.onLastNameChange(it) },
        onNameChange = { viewModel.onNameChange(it) },
        onSearch = { viewModel.search() },
        onClearError = { viewModel.clearError() },
        onNavigateToCredits = onNavigateToCredits,
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToTextRecognition = onNavigateToTextRecognition
    )
}

@Composable
fun MainScreenContent(
    uiState: MainState,
    onLastNameChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearError: () -> Unit,
    onNavigateToCredits: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToTextRecognition: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            if (LocalInspectionMode.current) {
                // Preview Mode
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = stringResource(R.string.descripcion_imagen),
                    modifier = Modifier.width(230.dp)
                )
            } else {
                // Device/Emulador
                Image(
                    bitmap = ImageBitmap.imageResource(id = R.drawable.long_logo_white),
                    contentDescription = stringResource(R.string.descripcion_imagen),
                    modifier = Modifier.width(230.dp),
                    contentScale = ContentScale.Fit,
                    filterQuality = FilterQuality.High
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Crossfade(targetState = uiState.numCutterResult, label = "CutterResult") { result ->
                CutterText(
                    text = result.ifEmpty { stringResource(id = R.string.empty_text) },
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Crossfade(targetState = uiState.cutterUsed, label = "CutterUsed") { used ->
                Text(
                    text = used.ifEmpty { stringResource(id = R.string.empty_text_2) },
                    fontSize = 20.sp,
                    color = CutterTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = onLastNameChange,
                label = { Text(stringResource(id = R.string.etx_last_name)
                    , color = CutterTextSecondary) },
                modifier = Modifier.width(280.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = CutterTextSecondary
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text(stringResource(id = R.string.etx_name)
                    , color = CutterTextSecondary) },
                modifier = Modifier.width(280.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = CutterTextSecondary
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onSearch,
                modifier = Modifier.padding(horizontal = 40.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = stringResource(id = R.string.bt_search),
                    color = CutterPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            CutterText(
                text = stringResource(id = R.string.credit),
                color = CutterTextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.clickable { onNavigateToCredits() }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        IconButton(
            onClick = onNavigateToHistory,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = stringResource(R.string.btn_history_desc),
                tint = Color.White
            )
        }

        IconButton(
            onClick = onNavigateToTextRecognition,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = stringResource(R.string.btn_text_recognition_desc),
                tint = Color.White
            )
        }

        // Error Snackbar
        uiState.errorMessage?.let { message ->
            Snackbar(
                action = {
                    TextButton(onClick = onClearError) {
                        Text("OK")
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                Text(message)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CutterTheme {
        MainScreenContent(
            uiState = MainState(
                numCutterResult = "Hola,",
                cutterUsed = "aqui aparecera el cutter utilizado",
                name = "John",
                lastName = "Doe"
            ),
            onLastNameChange = {},
            onNameChange = {},
            onSearch = {},
            onClearError = {},
            onNavigateToCredits = {},
            onNavigateToHistory = {},
            onNavigateToTextRecognition = {}
        )
    }
}