package com.example.up_mobileappv2.domain.repository

interface TokenManager {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun saveUserId(userId: String)
    suspend fun getUserId(): String?
    suspend fun clearToken()
}