package com.example.up_mobileappv2.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignUpRequest(
    val email: String,
    val password: String
)