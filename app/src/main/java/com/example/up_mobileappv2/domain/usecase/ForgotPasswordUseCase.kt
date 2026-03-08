package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return authRepository.forgotPassword(email)
    }
}