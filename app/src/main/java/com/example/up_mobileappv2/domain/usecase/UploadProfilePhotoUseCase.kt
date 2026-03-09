package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.repository.ProfileRepository
import java.io.File
import javax.inject.Inject

class UploadProfilePhotoUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: String, file: File): String? {
        return profileRepository.uploadProfilePhoto(userId, file)
    }
}