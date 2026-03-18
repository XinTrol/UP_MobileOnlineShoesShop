package com.example.up_mobileappv2.presentation.screen

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.up_mobileappv2.presentation.navigation.Screen
import com.example.up_mobileappv2.presentation.ui.components.generateBarcode
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
    val userId by viewModel.userId.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { output ->
                inputStream?.copyTo(output)
            }
            viewModel.uploadPhoto(file)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            viewModel.uploadPhoto(file)
        }
    }

    var showPhotoDialog by remember { mutableStateOf(false) }

    val barcodeBitmap = remember(userId) {
        userId?.let { generateBarcode(it, 400, 100) }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                ProfileViewModel.NavigationEvent.NavigateToLoyaltyCard -> {
                    navController.navigate(Screen.LoyaltyCard.route)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Profile" else "Профиль") },
                actions = {
                    IconButton(onClick = {
                        if (isEditing) {
                            viewModel.saveProfile()
                        } else {
                            viewModel.startEditing()
                        }
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Done else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Сохранить" else "Редактировать"
                        )
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Аватарка ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { showPhotoDialog = true },
                        contentAlignment = Alignment.Center
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
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // --- Имя и Штрих-код (Только в режиме просмотра) ---
                if (!isEditing) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    item {
                        barcodeBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Штрих-код",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clickable {
                                        userId?.let { id ->
                                            navController.navigate(Screen.LoyaltyCard.createRoute(id))
                                        }
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // --- Поля данных (Общие для обоих режимов) ---
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Имя
                        OutlinedTextField(
                            value = if (isEditing) firstName else (profile?.firstName ?: ""),
                            onValueChange = { if (isEditing) viewModel.onFirstNameChange(it) },
                            label = { Text("Имя") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            shape = RoundedCornerShape(16.dp),
                            readOnly = !isEditing
                        )

                        // Фамилия
                        OutlinedTextField(
                            value = if (isEditing) lastName else (profile?.lastName ?: ""),
                            onValueChange = { if (isEditing) viewModel.onLastNameChange(it) },
                            label = { Text("Фамилия") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            shape = RoundedCornerShape(16.dp),
                            readOnly = !isEditing
                        )

                        // Адрес
                        OutlinedTextField(
                            value = if (isEditing) address else (profile?.address ?: ""),
                            onValueChange = { if (isEditing) viewModel.onAddressChange(it) },
                            label = { Text("Адрес") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            shape = RoundedCornerShape(16.dp),
                            readOnly = !isEditing
                        )

                        // Телефон
                        OutlinedTextField(
                            value = if (isEditing) phone else (profile?.phone ?: ""),
                            onValueChange = { if (isEditing) viewModel.onPhoneChange(it) },
                            label = { Text("Телефон") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            shape = RoundedCornerShape(16.dp),
                            readOnly = !isEditing
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Диалоги
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