package com.example.up_mobileappv2.presentation.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.up_mobileappv2.presentation.ui.components.generateBarcode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyCardScreen(
    navController: NavController,
    userId: String?
) {
    // Генерируем штрих-код с хорошим качеством
    val barcodeBitmap = remember(userId) {
        if (!userId.isNullOrEmpty()) {
            generateBarcode(userId, 3000, 1600)
        } else {
            null
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .rotate(90f),
            contentAlignment = Alignment.Center
        ) {
            if (barcodeBitmap != null) {
                Image(
                    bitmap = barcodeBitmap.asImageBitmap(),
                    contentDescription = "Штрих-код карты",
                    modifier = Modifier
                        .fillMaxHeight() // Растягиваем на ВЕСЬ ЭКРАН по высоте
                        .padding(16.dp)   // Небольшие отступы от краев
                )
            } else {
                Text("Ошибка загрузки данных")
            }
        }
    }
}