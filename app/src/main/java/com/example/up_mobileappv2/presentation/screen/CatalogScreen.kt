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
import com.example.up_mobileappv2.presentation.viewmodel.FavouriteViewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun CatalogScreen(
    navController: NavController,
    viewModel: CatalogViewModel = hiltViewModel(),
    favouriteViewModel: FavouriteViewModel = hiltViewModel()
) {

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val favouriteIds by favouriteViewModel.favouriteIds.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        // HEADER
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = categories.firstOrNull { it.id == selectedCategoryId }?.title ?: "Каталог",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Категории",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

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

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            items(products.size) { index ->

                val product = products[index]

                ProductCard(
                    product = product,
                    isFavourite = product.id in favouriteIds,
                    onFavouriteClick = {
                        favouriteViewModel.toggleFavourite(product)
                    },
                    onClick = { }
                )
            }

        }
    }
}