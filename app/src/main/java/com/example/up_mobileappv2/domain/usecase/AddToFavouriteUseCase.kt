package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.repository.FavouriteRepository
import javax.inject.Inject

class AddToFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {
    suspend operator fun invoke(userId: String, productId: String): Result<Unit> {
        return favouriteRepository.addToFavourite(userId, productId)
    }
}