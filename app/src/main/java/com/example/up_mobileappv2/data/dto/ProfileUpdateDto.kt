package com.example.up_mobileappv2.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileUpdateDto(
    val firstname: String?,
    val lastname: String?,
    val address: String?,
    val phone: String?,
    val photo: String?
)