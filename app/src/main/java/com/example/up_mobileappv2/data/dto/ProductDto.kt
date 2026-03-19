package com.example.up_mobileappv2.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDto(
    val id: String,
    val title: String,
    @Json(name = "category_id") val categoryId: String?,
    val cost: Double,
    val description: String,
    @Json(name = "is_best_seller") val isBestSeller: Boolean? = false,
    @Json(name = "product_url") val productUrl: String?
)