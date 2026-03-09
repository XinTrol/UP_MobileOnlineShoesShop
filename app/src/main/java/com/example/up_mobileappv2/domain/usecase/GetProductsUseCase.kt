package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.model.Product
import com.example.up_mobileappv2.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(categoryId: String? = null): List<Product> {
        return productRepository.getProducts(categoryId)
    }
}