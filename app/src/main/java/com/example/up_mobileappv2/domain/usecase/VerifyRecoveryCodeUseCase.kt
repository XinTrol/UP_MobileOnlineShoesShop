package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.model.User
import com.example.up_mobileappv2.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyRecoveryCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, code: String): Result<User> {
        return authRepository.verifyRecoveryCode(email, code)
    }
}