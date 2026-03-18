package com.example.up_mobileappv2.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_mobileappv2.domain.model.Profile
import com.example.up_mobileappv2.domain.repository.AuthRepository
import com.example.up_mobileappv2.domain.repository.ProfileRepository
import com.example.up_mobileappv2.domain.repository.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userId = tokenManager.getUserId()
            if (userId != null) {
                val profile = profileRepository.getProfile(userId)
                _profile.value = profile
            }
        }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            val userId = tokenManager.getUserId()
            if (userId != null) {
                val profile = profileRepository.getProfile(userId)
                _profile.value = profile
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _profile.value = null
            onComplete()
        }
    }
}