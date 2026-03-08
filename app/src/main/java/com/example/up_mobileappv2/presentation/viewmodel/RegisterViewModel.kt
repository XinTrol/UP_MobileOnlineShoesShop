package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    // Состояния полей
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible = _passwordVisible.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog = _showErrorDialog.asStateFlow()

    // События навигации
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun dismissError() {
        _showErrorDialog.value = false
        _errorMessage.value = null
    }

    fun register() {
        val email = _email.value
        val password = _password.value

        // Валидация email по заданию
        if (!isEmailValid(email)) {
            showError("Некорректный email. Должен быть формата name@domenname.ru, где имя и домен содержат только латинские буквы и цифры, а старший домен >2 символов")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = registerUseCase(email, password)
            _isLoading.value = false
            result.onSuccess {
                _navigationEvent.emit(NavigationEvent.NavigateToSignIn)
            }.onFailure { exception ->
                showError(exception.message ?: "Ошибка регистрации")
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        // Паттерн: имя@домен.старший_домен (>=2 символов)
        // Имя и домен: только маленькие латинские буквы и цифры
        val regex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")
        return regex.matches(email)
    }

    private fun showError(message: String) {
        _errorMessage.value = message
        _showErrorDialog.value = true
    }

    sealed class NavigationEvent {
        object NavigateToSignIn : NavigationEvent()
    }
}