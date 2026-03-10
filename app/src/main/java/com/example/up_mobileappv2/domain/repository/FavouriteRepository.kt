package com.example.up_mobileappv2.domain.repository

import com.example.up_mobileappv2.domain.model.Product
import kotlinx.coroutines.flow.StateFlow

interface FavouriteRepository {
    val favouriteIds: StateFlow<Set<String>>
    suspend fun loadFavourites(userId: String)
    suspend fun addToFavourite(userId: String, productId: String): Result<Unit>
    suspend fun removeFromFavourite(userId: String, productId: String): Result<Unit>
}