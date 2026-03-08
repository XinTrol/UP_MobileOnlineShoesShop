package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.usecase.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewPasswordViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel() {

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible = _passwordVisible.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog = _showErrorDialog.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newConfirm: String) {
        _confirmPassword.value = newConfirm
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun onSubmit() {
        val pass = _password.value
        val confirm = _confirmPassword.value

        if (pass.length < 6) {
            showError("Пароль должен быть не менее 6 символов")
            return
        }
        if (pass != confirm) {
            showError("Пароли не совпадают")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = updatePasswordUseCase(pass)
            _isLoading.value = false
            result.onSuccess {
                _navigationEvent.emit(NavigationEvent.NavigateToSignIn)
            }.onFailure { exception ->
                showError(exception.message ?: "Ошибка при смене пароля")
            }
        }
    }

    fun dismissError() {
        _showErrorDialog.value = false
        _errorMessage.value = null
    }

    private fun showError(message: String) {
        _errorMessage.value = message
        _showErrorDialog.value = true
    }

    sealed class NavigationEvent {
        object NavigateToSignIn : NavigationEvent()
    }
}