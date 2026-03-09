package com.example.up_mobileappv2.domain.repository

import com.example.up_mobileappv2.domain.model.Product

interface FavouriteRepository {
    suspend fun getFavourites(userId: String): List<Product>
    suspend fun addToFavourite(userId: String, productId: String)
    suspend fun removeFromFavourite(userId: String, productId: String)
}