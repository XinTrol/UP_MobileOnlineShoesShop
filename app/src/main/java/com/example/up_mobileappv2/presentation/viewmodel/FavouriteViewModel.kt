package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.model.Product
import com.example.up_mobileappv2.domain.repository.FavouriteRepository
import com.example.up_mobileappv2.domain.repository.ProductRepository
import com.example.up_mobileappv2.domain.repository.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val favouriteRepository: FavouriteRepository,
    private val productRepository: ProductRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val favouriteIds: StateFlow<Set<String>> = favouriteRepository.favouriteIds

    private val _favouriteProducts = MutableStateFlow<List<Product>>(emptyList())
    val favouriteProducts = _favouriteProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadFavourites()
        // Подписываемся на изменения favouriteIds и обновляем список продуктов
        viewModelScope.launch {
            favouriteIds.collect { ids ->
                loadProducts(ids)
            }
        }
    }

    fun loadFavourites() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = tokenManager.getUserId() ?: return@launch
            favouriteRepository.loadFavourites(userId)
            // продукты будут загружены автоматически через collect выше
        }
    }

    private suspend fun loadProducts(ids: Set<String>) {
        if (ids.isEmpty()) {
            _favouriteProducts.value = emptyList()
        } else {
            val products = productRepository.getProductsByIds(ids.toList())
            _favouriteProducts.value = products
        }
        _isLoading.value = false
    }

    fun toggleFavourite(product: Product) {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: return@launch
            val result = if (favouriteIds.value.contains(product.id)) {
                favouriteRepository.removeFromFavourite(userId, product.id)
            } else {
                favouriteRepository.addToFavourite(userId, product.id)
            }
            result.onFailure { exception ->
                println("❌ toggleFavourite error: ${exception.message}")
                // можно показать ошибку через Snackbar или диалог
            }
        }
    }
}