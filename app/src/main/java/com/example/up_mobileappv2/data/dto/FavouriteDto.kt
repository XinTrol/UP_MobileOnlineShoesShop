package com.example.up_mobileappv2.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class FavouriteDto(
    val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "product_id") val productId: String,
    val products: ProductDto? = null
)