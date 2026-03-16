package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.model.Profile
import com.example.up_mobileappv2.domain.model.User
import com.example.up_mobileappv2.domain.repository.AuthRepository
import com.example.up_mobileappv2.domain.repository.ProfileRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
private val authRepository: AuthRepository,
private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<User> {

        val result = authRepository.signUp(email, password)

        return result.onSuccess { user ->

            val profile = Profile(
                userId = user.id,
                firstName = name,
                lastName = null,
                address = null,
                phone = null,
                photo = null
            )

            val profileResult = profileRepository.createProfile(profile)

            if (profileResult.isFailure) {
                println("❌ Ошибка при создании профиля: ${profileResult.exceptionOrNull()?.message}")
            }
        }
    }
}