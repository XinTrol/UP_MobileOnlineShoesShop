package com.example.up_mobileappv2.domain.repository

import com.example.up_mobileappv2.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
}