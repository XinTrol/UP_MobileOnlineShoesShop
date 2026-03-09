package com.example.up_mobileappv2.data.remote

import com.example.up_mobileappv2.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface DatabaseApi {
    // Products
    @GET("rest/v1/products")
    suspend fun getProducts(
        @Query("select") select: String = "*",
        @Query("category_id") categoryId: String? = null,
        @Query("is_best_seller") isBestSeller: Boolean? = null,
        @Query("order") order: String = "title.asc"
    ): List<ProductDto>

    // Categories
    @GET("rest/v1/categories")
    suspend fun getCategories(): List<CategoryDto>

    // Actions – один метод
    @GET("rest/v1/actions")
    suspend fun getActions(
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<ActionDto>

    @GET("rest/v1/favourite")
    suspend fun getFavourites(
        @Query("user_id") userFilter: String,
        @Query("select") select: String = "*, products(*)"
    ): List<FavouriteDto>

    @POST("rest/v1/favourite")
    suspend fun addToFavourite(@Body favourite: FavouriteInsertDto): FavouriteDto

    @DELETE("rest/v1/favourite")
    suspend fun removeFromFavourite(
        @Query("user_id") userFilter: String,
        @Query("product_id") productFilter: String
    )

    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Query("user_id") userFilter: String
    ): List<ProfileDto>

    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Query("user_id") userFilter: String,
        @Body updates: ProfileUpdateDto
    ): Response<Unit>

    @POST("rest/v1/profiles")
    suspend fun createProfile(@Body profile: ProfileCreateDto): Response<Unit>
}