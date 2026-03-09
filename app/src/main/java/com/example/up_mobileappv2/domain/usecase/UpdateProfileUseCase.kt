package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.model.Profile
import com.example.up_mobileappv2.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profile): Result<Unit> {
        return profileRepository.updateProfile(profile)
    }
}