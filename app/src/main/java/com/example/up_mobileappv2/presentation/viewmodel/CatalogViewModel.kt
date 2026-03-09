package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.model.Category
import com.example.up_mobileappv2.domain.model.Product
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
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

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
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cats = getCategoriesUseCase()
                _categories.value = cats
                // После загрузки категорий загружаем товары (все или по первой категории)
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
                val prods = getProductsUseCase(categoryId)
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
        if (categoryId != _selectedCategoryId.value) {
            loadProducts(categoryId)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}