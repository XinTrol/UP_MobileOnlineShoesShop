package com.example.up_mobileappv2.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavouriteInsertDto(
    val user_id: String,
    val product_id: String
)