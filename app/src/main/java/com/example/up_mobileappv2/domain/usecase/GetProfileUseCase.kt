package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.model.Profile
import com.example.up_mobileappv2.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: String): Profile? {
        return profileRepository.getProfile(userId)
    }
}