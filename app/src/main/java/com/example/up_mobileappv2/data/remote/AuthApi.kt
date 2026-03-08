package com.example.up_mobileappv2.data.remote

import com.example.up_mobileappv2.data.dto.auth.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body request: SignInRequest): AuthResponse

    @POST("auth/v1/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @POST("auth/v1/recover")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @PUT("auth/v1/user")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): AuthResponse
}