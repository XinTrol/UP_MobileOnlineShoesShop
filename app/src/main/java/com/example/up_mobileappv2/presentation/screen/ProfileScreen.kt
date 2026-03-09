package com.example.up_mobileappv2.presentation.screen

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.up_mobileappv2.presentation.ui.components.BarcodeGenerator
import com.example.up_mobileappv2.presentation.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val firstName by viewModel.firstName.collectAsStateWithLifecycle()
    val lastName by viewModel.lastName.collectAsStateWithLifecycle()
    val address by viewModel.address.collectAsStateWithLifecycle()
    val phone by viewModel.phone.collectAsStateWithLifecycle()
    val photoUrl by viewModel.photoUrl.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // Launcher для выбора изображения из галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Копируем файл во временное хранилище
            val inputStream = context.contentResolver.openInputStream(it)
            val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { output ->
                inputStream?.copyTo(output)
            }
            viewModel.uploadPhoto(file)
        }
    }

    // Launcher для камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            // Сохраняем Bitmap во временный файл
            val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            viewModel.uploadPhoto(file)
        }
    }

    // Состояние диалога выбора источника фото
    var showPhotoDialog by remember { mutableStateOf(false) }

    // Генерация штрих-кода из id пользователя
    val barcodeBitmap = remember(profile?.userId) {
        profile?.userId?.let { BarcodeGenerator.generateBarcode(it, 300, 100) }
    }

    // Слушаем события навигации
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                ProfileViewModel.NavigationEvent.NavigateToLoyaltyCard -> {
                    navController.navigate("loyalty_card") // маршрут нужно добавить
                }
            }
        }
    }

    // Устанавливаем userId, когда профиль загружен (здесь нужно передать извне, пока заглушка)
    // Например, можно получить из аргументов навигации
    // Пока просто вызовем setUserId, если его нет
    // Для демо передадим "test-user-id" (позже заменим на реальный)

    LaunchedEffect(Unit) {
        // Здесь нужно получить userId, например, из TokenManager или из аргументов
        // Пока для теста передадим фиктивный
        // viewModel.setUserId("реальный-id-из-сессии")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = viewModel::startEditing) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                        }
                    } else {
                        IconButton(onClick = viewModel::saveProfile) {
                            Icon(Icons.Default.Done, contentDescription = "Сохранить")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading && profile == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Фото профиля
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { showPhotoDialog = true }
                    ) {
                        if (photoUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Фото профиля",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Добавить фото",
                                modifier = Modifier
                                    .size(100.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }

                item {
                    if (isEditing) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = viewModel::onFirstNameChange,
                            label = { Text("Имя") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = viewModel::onLastNameChange,
                            label = { Text("Фамилия") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = viewModel::onAddressChange,
                            label = { Text("Адрес") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = viewModel::onPhoneChange,
                            label = { Text("Телефон") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Отображение данных
                        profile?.let {
                            Text("Имя: ${it.firstName ?: "не указано"}")
                            Text("Фамилия: ${it.lastName ?: "не указано"}")
                            Text("Адрес: ${it.address ?: "не указано"}")
                            Text("Телефон: ${it.phone ?: "не указано"}")
                        }
                    }
                }

                item {
                    // Штрих-код
                    barcodeBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Штрих-код",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clickable { viewModel.navigateToLoyaltyCard() }
                        )
                    }
                }
            }
        }
    }

    // Диалог выбора источника фото
    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Изменить фото") },
            text = { Text("Выберите источник") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Галерея")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    cameraLauncher.launch(null)
                }) {
                    Text("Камера")
                }
            }
        )
    }

    // Диалог ошибки
    if (showErrorDialog && errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text("Ошибка") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissError) {
                    Text("OK")
                }
            }
        )
    }
}