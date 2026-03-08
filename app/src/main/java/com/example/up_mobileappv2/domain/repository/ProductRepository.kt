package com.example.up_mobileappv2.domain.repository

import com.example.up_mobileappv2.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(categoryId: String? = null): List<Product>
    suspend fun getBestSellers(): List<Product>
}