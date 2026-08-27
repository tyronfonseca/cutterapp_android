package com.tf.clasificacioncutter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tf.clasificacioncutter.ui.components.CutterText
import com.tf.clasificacioncutter.ui.theme.CutterTheme
import com.tf.clasificacioncutter.ui.theme.CutterPrimary
import com.tf.clasificacioncutter.ui.theme.CutterTextSecondary
import com.tf.clasificacioncutter.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CutterTheme {
                MainScreen(
                    viewModel = viewModel,
                    onNavigateToCredits = {
                        startActivity(Intent(this, LicensesActivity::class.java))
                    },
                    onNavigateToHistory = {
                        startActivity(Intent(this, HistoryActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToCredits: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val uiState = viewModel.uiState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CutterPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Image(
                painter = painterResource(id = R.drawable.long_logo_white),
                contentDescription = null,
                modifier = Modifier.width(230.dp)
            )

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
                CutterText(
                    text = used.ifEmpty { stringResource(id = R.string.empty_text_2) },
                    fontSize = 20.sp,
                    color = CutterTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = { viewModel.onLastNameChange(it) },
                label = { Text(stringResource(id = R.string.etx_last_name), color = CutterTextSecondary) },
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
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text(stringResource(id = R.string.etx_name), color = CutterTextSecondary) },
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
                onClick = { viewModel.search() },
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
                contentDescription = "Historial",
                tint = Color.White
            )
        }
        
        // Error Snackbar
        uiState.errorMessage?.let { message ->
            Snackbar(
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK")
                    }
                },
                modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)
            ) {
                Text(message)
            }
        }
    }
}