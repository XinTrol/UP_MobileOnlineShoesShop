package com.example.up_mobileappv2.domain.model

data class Product(
    val id: String,
    val title: String,
    val categoryId: String?,
    val cost: Double,
    val description: String,
    val isBestSeller: Boolean = false,
    val productUrl: String?
)