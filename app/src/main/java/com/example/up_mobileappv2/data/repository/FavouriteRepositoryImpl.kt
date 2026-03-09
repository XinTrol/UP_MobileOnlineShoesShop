package com.example.up_mobileappv2.data.repository

import com.example.up_mobileappv2.data.dto.FavouriteInsertDto
import com.example.up_mobileappv2.data.mapper.toDomain
import com.example.up_mobileappv2.data.remote.DatabaseApi
import com.example.up_mobileappv2.domain.model.Product
import com.example.up_mobileappv2.domain.repository.FavouriteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteRepositoryImpl @Inject constructor(
    private val databaseApi: DatabaseApi
) : FavouriteRepository {

    override suspend fun getFavourites(userId: String): List<Product> {
        val filter = "eq.$userId"
        val favourites = databaseApi.getFavourites(userFilter = filter, select = "*, products(*)")
        return favourites.mapNotNull { fav ->
            fav.products?.toDomain()
        }
    }

    override suspend fun addToFavourite(userId: String, productId: String) {
        val insert = FavouriteInsertDto(user_id = userId, product_id = productId)
        databaseApi.addToFavourite(insert)
    }

    override suspend fun removeFromFavourite(userId: String, productId: String) {
        databaseApi.removeFromFavourite(
            userFilter = "eq.$userId",
            productFilter = "eq.$productId"
        )
    }
}