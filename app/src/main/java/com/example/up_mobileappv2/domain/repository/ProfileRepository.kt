package com.example.up_mobileappv2.domain.repository

import com.example.up_mobileappv2.domain.model.Profile
import java.io.File

interface ProfileRepository {
    suspend fun getProfile(userId: String): Profile?
    suspend fun createProfile(profile: Profile): Result<Unit>
    suspend fun updateProfile(profile: Profile): Result<Unit>
    suspend fun uploadProfilePhoto(userId: String, file: File): String?
}