package com.example.up_mobileappv2.data.mapper

import com.example.up_mobileappv2.data.dto.CategoryDto
import com.example.up_mobileappv2.domain.model.Category

fun CategoryDto.toDomain(): Category {
    return Category(
        id = id,
        title = title
    )
}