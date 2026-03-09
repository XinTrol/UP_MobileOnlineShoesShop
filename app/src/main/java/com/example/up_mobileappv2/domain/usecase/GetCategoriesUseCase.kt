package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.model.Category
import com.example.up_mobileappv2.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): List<Category> {
        return categoryRepository.getCategories()
    }
}