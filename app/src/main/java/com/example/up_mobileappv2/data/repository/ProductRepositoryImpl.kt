package com.example.up_mobileappv2.data.repository

import com.example.up_mobileappv2.data.mapper.toDomain
import com.example.up_mobileappv2.data.remote.DatabaseApi
import com.example.up_mobileappv2.domain.model.Product
import com.example.up_mobileappv2.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val databaseApi: DatabaseApi
) : ProductRepository {

    override suspend fun getProducts(categoryId: String?): List<Product> {
        return if (categoryId != null) {
            // Добавляем оператор eq.
            databaseApi.getProducts(categoryFilter = "eq.$categoryId")
        } else {
            databaseApi.getProducts()
        }.map { it.toDomain() }
    }

    override suspend fun getBestSellers(): List<Product> {
        return databaseApi.getProducts().filter { it.isBestSeller == true }.map { it.toDomain() }
    }

    override suspend fun getProductsByIds(ids: List<String>): List<Product> {
        if (ids.isEmpty()) return emptyList()
        val filter = "in.(${ids.joinToString(",") { "\"$it\"" }})"
        return databaseApi.getProducts(idFilter = filter).map { it.toDomain() }
    }
}