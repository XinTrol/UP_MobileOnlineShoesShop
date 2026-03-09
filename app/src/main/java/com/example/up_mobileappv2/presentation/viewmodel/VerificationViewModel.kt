package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.usecase.ForgotPasswordUseCase
import com.example.up_mobileappv2.domain.usecase.VerifyRecoveryCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val verifyUseCase: VerifyRecoveryCodeUseCase
) : ViewModel() {

    private val _code = MutableStateFlow("")
    val code = _code.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog = _showErrorDialog.asStateFlow()

    private val _isCodeInvalid = MutableStateFlow(false)
    val isCodeInvalid = _isCodeInvalid.asStateFlow()

    private val _timerSeconds = MutableStateFlow(60)
    val timerSeconds = _timerSeconds.asStateFlow()

    private val _canResend = MutableStateFlow(false)
    val canResend = _canResend.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        startTimer()
    }

    fun onCodeChange(newCode: String) {
        if (newCode.length <= 6) {
            _code.value = newCode
            _isCodeInvalid.value = false
        }
    }

    fun onVerifyClick(email: String) {
        if (_code.value.length != 6) {
            showError("Введите 6-значный код")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val result = verifyUseCase(email, _code.value)
            _isLoading.value = false
            result.onSuccess {
                _navigationEvent.emit(NavigationEvent.NavigateToCreatePassword)
            }.onFailure { exception ->
                _isCodeInvalid.value = true
                showError(exception.message ?: "Неверный код")
            }
        }
    }

    fun onResendClick(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = forgotPasswordUseCase(email)
            _isLoading.value = false
            result.onSuccess {
                _canResend.value = false
                _timerSeconds.value = 60
                startTimer()
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Ошибка при повторной отправке"
                _showErrorDialog.value = true
            }
        }
    }

    fun dismissError() {
        _showErrorDialog.value = false
        _errorMessage.value = null
    }

    fun resetCodeInvalid() {
        _isCodeInvalid.value = false
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_timerSeconds.value > 0) {
                delay(1000)
                _timerSeconds.value -= 1
            }
            _canResend.value = true
        }
    }

    private fun showError(message: String) {
        _errorMessage.value = message
        _showErrorDialog.value = true
    }

    sealed class NavigationEvent {
        object NavigateToCreatePassword : NavigationEvent()
        object NavigateBack : NavigationEvent()
    }
}