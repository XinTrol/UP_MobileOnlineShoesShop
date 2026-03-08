package com.example.up_mobileappv2.data.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyRequest(
    val type: String,   // "signup" или "recovery"
    val token: String,  // код из письма
    val email: String? = null  // иногда требуется для recovery
)