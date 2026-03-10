package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.model.Category
import com.example.up_mobileappv2.domain.model.Product
import com.example.up_mobileappv2.domain.repository.FavouriteRepository
import com.example.up_mobileappv2.domain.repository.TokenManager
import com.example.up_mobileappv2.domain.usecase.GetCategoriesUseCase
import com.example.up_mobileappv2.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val favouriteRepository: FavouriteRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val favouriteIds = favouriteRepository.favouriteIds

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId = _selectedCategoryId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadCategories()
        loadFavouriteIds()
    }

    private fun loadFavouriteIds() {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: return@launch
            favouriteRepository.loadFavourites(userId)
        }
    }

    fun toggleFavourite(product: Product) {
        viewModelScope.launch {
            val userId = tokenManager.getUserId() ?: return@launch
            if (favouriteIds.value.contains(product.id)) {
                favouriteRepository.removeFromFavourite(userId, product.id)
            } else {
                favouriteRepository.addToFavourite(userId, product.id)
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cats = getCategoriesUseCase()
                _categories.value = cats
                loadProducts(null)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadProducts(categoryId: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("📦 CatalogViewModel.loadProducts: categoryId = $categoryId")
                val prods = getProductsUseCase(categoryId)
                println("📦 CatalogViewModel.loadProducts: загружено ${prods.size} товаров")
                _products.value = prods
                _selectedCategoryId.value = categoryId
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(categoryId: String?) {
        viewModelScope.launch {
            _selectedCategoryId.value = categoryId
            loadProducts(categoryId)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}