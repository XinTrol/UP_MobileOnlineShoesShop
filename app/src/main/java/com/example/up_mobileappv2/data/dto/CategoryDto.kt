package com.example.up_mobileappv2.data.dto

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CategoryDto(
    val id: String,
    val title: String
)