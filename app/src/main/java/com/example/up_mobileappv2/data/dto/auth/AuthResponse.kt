package com.example.up_mobileappv2.data.dto.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "access_token") val accessToken: String?,
    @Json(name = "token_type") val tokenType: String?,
    @Json(name = "expires_in") val expiresIn: Int?,
    @Json(name = "refresh_token") val refreshToken: String?,
    val user: UserDto? = null
)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val email: String,
)