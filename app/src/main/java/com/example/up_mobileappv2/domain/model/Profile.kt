package com.example.up_mobileappv2.domain.model

data class Profile(
    val id: String? = null,
    val userId: String,
    val photo: String?,
    val firstName: String?,
    val lastName: String?,
    val address: String?,
    val phone: String?
)