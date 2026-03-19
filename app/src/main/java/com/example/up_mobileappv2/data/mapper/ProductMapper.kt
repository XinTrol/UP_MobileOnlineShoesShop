package com.example.up_mobileappv2.data.mapper

import com.example.up_mobileappv2.data.dto.ProductDto
import com.example.up_mobileappv2.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        categoryId = categoryId,
        cost = cost,
        description = description,
        isBestSeller = isBestSeller ?: false,
        productUrl = productUrl
    )
}