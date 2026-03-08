package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.model.Action
import com.example.up_mobileappv2.domain.model.Product
import com.example.up_mobileappv2.domain.usecase.GetActionsUseCase
import com.example.up_mobileappv2.domain.usecase.GetBestSellersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getActionsUseCase: GetActionsUseCase,
    private val getBestSellersUseCase: GetBestSellersUseCase
) : ViewModel() {

    private val _actions = MutableStateFlow<List<Action>>(emptyList())
    val actions = _actions.asStateFlow()

    private val _bestSellers = MutableStateFlow<List<Product>>(emptyList())
    val bestSellers = _bestSellers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val actions = getActionsUseCase()
                val products = getBestSellersUseCase()
                _actions.value = actions
                _bestSellers.value = products
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}