package com.example.up_mobileappv2.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActionDto(
    val id: String,
    val photo: String?,
    @Json(name = "created_at") val createdAt: String
)