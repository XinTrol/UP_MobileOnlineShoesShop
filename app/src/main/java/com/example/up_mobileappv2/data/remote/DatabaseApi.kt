package com.example.up_mobileappv2.data.remote

import com.example.up_mobileappv2.data.dto.*
import retrofit2.http.*

interface DatabaseApi {
    @GET("rest/v1/products")
    suspend fun getProducts(
        @Query("select") select: String = "*",
        @Query("category_id") categoryId: String? = null
    ): List<ProductDto>

    @GET("rest/v1/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("rest/v1/actions")
    suspend fun getActions(): List<ActionDto>

    @GET("rest/v1/favourite")
    suspend fun getFavourites(
        @Query("user_id") userId: String,
        @Query("select") select: String = "*, products(*)"
    ): List<FavouriteDto>

    @POST("rest/v1/favourite")
    suspend fun addToFavourite(@Body favourite: FavouriteInsertDto): FavouriteDto

    @DELETE("rest/v1/favourite")
    suspend fun removeFromFavourite(
        @Query("user_id") userId: String,
        @Query("product_id") productId: String
    )

    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Query("user_id") userId: String
    ): List<ProfileDto>

    @PUT("rest/v1/profiles")
    suspend fun updateProfile(
        @Query("user_id") userId: String,
        @Body profile: ProfileDto
    )
}