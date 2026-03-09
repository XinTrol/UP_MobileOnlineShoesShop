package com.example.up_mobileappv2.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.up_mobileappv2.presentation.ui.components.ProductCard
import com.example.up_mobileappv2.presentation.viewmodel.CatalogViewModel

@Composable
fun CatalogScreen(
    navController: NavController,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Горизонтальный список категорий
        if (categories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Пункт "Все"
                item {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { viewModel.selectCategory(null) },
                        label = { Text("Все") }
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = category.id == selectedCategoryId,
                        onClick = { viewModel.selectCategory(category.id) },
                        label = { Text(category.title) }
                    )
                }
            }
        }

        // Список товаров
        if (isLoading && products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onClick = {
                            // Переход на детали товара (позже)
                        }
                    )
                }
            }
        }
    }

    // Обработка ошибок (можно показать Snackbar)
}