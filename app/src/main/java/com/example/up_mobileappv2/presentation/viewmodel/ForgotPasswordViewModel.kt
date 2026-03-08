package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.usecase.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog = _showErrorDialog.asStateFlow()

    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog = _showSuccessDialog.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun dismissError() {
        _showErrorDialog.value = false
        _errorMessage.value = null
    }

    fun dismissSuccess() {
        _showSuccessDialog.value = false
    }

    fun onSubmit() {
        val email = _email.value
        if (!isEmailValid(email)) {
            showError("Некорректный email")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val result = forgotPasswordUseCase(email)
            _isLoading.value = false
            result.onSuccess {
                _showSuccessDialog.value = true
            }.onFailure { exception ->
                showError(exception.message ?: "Ошибка отправки")
            }
        }
    }

    fun onDialogDismissed() {
        _showSuccessDialog.value = false
        // после закрытия диалога переходим на Verification
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToVerification)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val regex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")
        return regex.matches(email)
    }

    private fun showError(message: String) {
        _errorMessage.value = message
        _showErrorDialog.value = true
    }

    sealed class NavigationEvent {
        object NavigateToVerification : NavigationEvent()
        object NavigateBack : NavigationEvent()
    }
}