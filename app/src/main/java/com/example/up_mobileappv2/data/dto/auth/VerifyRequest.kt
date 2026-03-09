package com.example.up_mobileappv2.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyRequest(
    val type: String,
    val token: String,
    val email: String? = null
)