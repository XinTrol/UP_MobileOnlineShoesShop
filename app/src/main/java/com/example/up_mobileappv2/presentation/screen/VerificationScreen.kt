package com.example.up_mobileappv2.presentation.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.up_mobileappv2.presentation.navigation.Screen
import com.example.up_mobileappv2.presentation.viewmodel.VerificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    navController: NavController,
    email: String,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val code by viewModel.code.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val isCodeInvalid by viewModel.isCodeInvalid.collectAsStateWithLifecycle()
    val timerSeconds by viewModel.timerSeconds.collectAsStateWithLifecycle()
    val canResend by viewModel.canResend.collectAsStateWithLifecycle()



    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                VerificationViewModel.NavigationEvent.NavigateToCreatePassword -> {
                    navController.navigate(Screen.CreateNewPassword.route) {
                        popUpTo(Screen.Verification.route) { inclusive = true }
                    }
                }
                VerificationViewModel.NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "OTP Проверка",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Пожалуйста, Проверьте Свою Электронную Почту ($email), Чтобы Увидеть Код Подтверждения",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = code,
                onValueChange = {
                    viewModel.onCodeChange(it)
                    if (it.length == 6) {
                    }
                },
                label = { Text("6-значный код") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isCodeInvalid,
                modifier = Modifier.fillMaxWidth(),
                colors = if (isCodeInvalid) {
                    TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Red,
                        unfocusedIndicatorColor = Color.Red,
                        focusedLabelColor = Color.Red,
                        unfocusedLabelColor = Color.Red
                    )
                } else {
                    TextFieldDefaults.colors()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onVerifyClick(email) },
                enabled = !isLoading && code.length == 6,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Подтвердить")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (canResend) {
                    TextButton(
                        onClick = { viewModel.onResendClick(email) }
                    ) {
                        Text("Отправить код повторно")
                    }
                } else {
                    Text("Запросить код повторно через ${timerSeconds} сек")
                }
            }
        }
    }

    if (showErrorDialog && errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text("Ошибка") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissError) { Text("OK") }
            }
        )
    }
}