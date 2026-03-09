package com.example.up_mobileappv2.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.up_mobileappv2.domain.model.Action
import com.example.up_mobileappv2.presentation.ui.components.ProductCard
import com.example.up_mobileappv2.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val actions by viewModel.actions.collectAsStateWithLifecycle()
    val bestSellers by viewModel.bestSellers.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    if (isLoading && actions.isEmpty() && bestSellers.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Акции
            if (actions.isNotEmpty()) {
                item {
                    Text(
                        text = "Акции",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                item {
                    ActionsCarousel(actions = actions)
                }
            }

            // Хиты продаж
            if (bestSellers.isNotEmpty()) {
                item {
                    Text(
                        text = "Хиты продаж",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                items(bestSellers) { product ->
                    ProductCard(
                        product = product,
                        onClick = {
                            // Переход на детали товара (пока нет)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ActionsCarousel(actions: List<Action>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(actions) { action ->
            AsyncImage(
                model = action.photoUrl ?: "https://via.placeholder.com/300x150",
                contentDescription = null,
                modifier = Modifier
                    .width(300.dp)
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}