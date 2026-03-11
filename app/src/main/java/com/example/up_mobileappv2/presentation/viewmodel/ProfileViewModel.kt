package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.model.Profile
import com.example.up_mobileappv2.domain.repository.TokenManager
import com.example.up_mobileappv2.domain.usecase.GetProfileUseCase
import com.example.up_mobileappv2.domain.usecase.UpdateProfileUseCase
import com.example.up_mobileappv2.domain.usecase.CreateProfileUseCase
import com.example.up_mobileappv2.domain.usecase.UploadProfilePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val createProfileUseCase: CreateProfileUseCase,   // ← добавили
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing = _isEditing.asStateFlow()

    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _address = MutableStateFlow("")
    val address = _address.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl = _photoUrl.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog = _showErrorDialog.asStateFlow()

    private val _showPhotoDialog = MutableStateFlow(false)
    val showPhotoDialog = _showPhotoDialog.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    init {
        viewModelScope.launch {
            val uid = tokenManager.getUserId()
            _userId.value = uid
            loadProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = tokenManager.getUserId() ?: run {
                _errorMessage.value = "Пользователь не авторизован"
                _showErrorDialog.value = true
                _isLoading.value = false
                return@launch
            }
            try {
                val profile = getProfileUseCase(userId)
                if (profile != null) {
                    _profile.value = profile
                    _firstName.value = profile.firstName ?: ""
                    _lastName.value = profile.lastName ?: ""
                    _address.value = profile.address ?: ""
                    _phone.value = profile.phone ?: ""
                    _photoUrl.value = profile.photo
                } else {
                    // Профиля нет – создадим пустой
                    _profile.value = Profile(
                        id = "",
                        userId = userId,
                        firstName = "",
                        lastName = "",
                        address = "",
                        phone = "",
                        photo = null
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                 _showErrorDialog.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
        if (!_isEditing.value) {
            // При выходе из режима редактирования сбрасываем поля до исходных
            _profile.value?.let {
                _firstName.value = it.firstName ?: ""
                _lastName.value = it.lastName ?: ""
                _address.value = it.address ?: ""
                _phone.value = it.phone ?: ""
            }
        }
    }

    fun updateFields(first: String, last: String, addr: String, ph: String) {
        _firstName.value = first
        _lastName.value = last
        _address.value = addr
        _phone.value = ph
    }

    fun startEditing() {
        _isEditing.value = true
    }

    fun onFirstNameChange(newValue: String) {
        updateFields(newValue, lastName.value, address.value, phone.value)
    }

    fun onLastNameChange(newValue: String) {
        updateFields(firstName.value, newValue, address.value, phone.value)
    }

    fun onAddressChange(newValue: String) {
        updateFields(firstName.value, lastName.value, newValue, phone.value)
    }

    fun onPhoneChange(newValue: String) {
        updateFields(firstName.value, lastName.value, address.value, newValue)
    }

    fun saveProfile() {
        val currentProfile = _profile.value ?: return
        val updatedProfile = currentProfile.copy(
            firstName = _firstName.value.takeIf { it.isNotBlank() },
            lastName = _lastName.value.takeIf { it.isNotBlank() },
            address = _address.value.takeIf { it.isNotBlank() },
            phone = _phone.value.takeIf { it.isNotBlank() }
        )
        viewModelScope.launch {
            _isLoading.value = true
            val result = if (currentProfile.id.isNullOrEmpty()) {
                createProfileUseCase(updatedProfile)
            } else {
                updateProfileUseCase(updatedProfile)
            }
            _isLoading.value = false
            result.onSuccess {
                _profile.value = updatedProfile
                _isEditing.value = false
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Ошибка при сохранении"
                _showErrorDialog.value = true
            }
        }
    }

    fun uploadPhoto(file: File) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = tokenManager.getUserId() ?: return@launch
            val photoUrl = uploadProfilePhotoUseCase(userId, file)
            if (photoUrl != null) {
                // Обновляем профиль с новой ссылкой
                val currentProfile = _profile.value ?: return@launch
                val updatedProfile = currentProfile.copy(photo = photoUrl)
                val saveResult = updateProfileUseCase(updatedProfile)
                if (saveResult.isSuccess) {
                    _profile.value = updatedProfile
                    _photoUrl.value = photoUrl
                } else {
                    _errorMessage.value = "Фото загружено, но не удалось сохранить ссылку"
                    _showErrorDialog.value = true
                }
            } else {
                _errorMessage.value = "Ошибка загрузки фото"
                _showErrorDialog.value = true
            }
            _isLoading.value = false
        }
    }

    fun showPhotoSourceDialog() {
        _showPhotoDialog.value = true
    }

    fun hidePhotoDialog() {
        _showPhotoDialog.value = false
    }

    fun navigateToLoyaltyCard() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToLoyaltyCard)
        }
    }

    fun dismissError() {
        _showErrorDialog.value = false
        _errorMessage.value = null
    }

    sealed class NavigationEvent {
        object NavigateToLoyaltyCard : NavigationEvent()
    }
}