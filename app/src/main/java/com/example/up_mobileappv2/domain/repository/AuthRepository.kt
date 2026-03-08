package com.example.up_mobileappv2.domain.repository

import com.example.up_mobileappv2.domain.model.User

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    suspend fun logout()
}