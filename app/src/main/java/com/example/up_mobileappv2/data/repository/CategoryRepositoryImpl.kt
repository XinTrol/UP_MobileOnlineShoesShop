package com.example.up_mobileappv2.data.repository

import com.example.up_mobileappv2.data.mapper.toDomain
import com.example.up_mobileappv2.data.remote.DatabaseApi
import com.example.up_mobileappv2.domain.model.Category
import com.example.up_mobileappv2.domain.repository.CategoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val databaseApi: DatabaseApi
) : CategoryRepository {
    override suspend fun getCategories(): List<Category> {
        return databaseApi.getCategories().map { it.toDomain() }
    }
}