package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.model.User
import com.example.up_mobileappv2.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Здесь можно добавить дополнительную бизнес-логику
        return authRepository.signUp(email, password)
    }
}