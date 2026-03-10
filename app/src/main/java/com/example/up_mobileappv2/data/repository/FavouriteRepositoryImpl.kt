package com.example.up_mobileappv2.data.repository

import com.example.up_mobileappv2.data.dto.FavouriteInsertDto
import com.example.up_mobileappv2.data.mapper.toDomain
import com.example.up_mobileappv2.data.remote.DatabaseApi
import com.example.up_mobileappv2.domain.model.Product
import com.example.up_mobileappv2.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteRepositoryImpl @Inject constructor(
    private val databaseApi: DatabaseApi
) : FavouriteRepository {

    private val _favouriteIds = MutableStateFlow<Set<String>>(emptySet())
    override val favouriteIds: StateFlow<Set<String>> = _favouriteIds.asStateFlow()

    override suspend fun loadFavourites(userId: String) {
        val filter = "eq.$userId"
        val response = databaseApi.getFavourites(
            userFilter = filter,
            select = "*, products!favourite_product_id_fkey(*)"
        )
        if (response.isSuccessful) {
            val ids = response.body()?.mapNotNull { it.productId }?.toSet() ?: emptySet()
            _favouriteIds.value = ids
        }
    }

    override suspend fun addToFavourite(userId: String, productId: String): Result<Unit> {
        return try {
            val insert = FavouriteInsertDto(user_id = userId, product_id = productId)
            val response = databaseApi.addToFavourite(insert)
            if (response.isSuccessful) {
                _favouriteIds.value = _favouriteIds.value + productId
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ошибка добавления: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavourite(userId: String, productId: String): Result<Unit> {
        return try {
            val response = databaseApi.removeFromFavourite(
                userFilter = "eq.$userId",
                productFilter = "eq.$productId"
            )
            if (response.isSuccessful) {
                _favouriteIds.value = _favouriteIds.value - productId
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ошибка удаления: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}