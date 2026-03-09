package com.example.up_mobileappv2.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface StorageApi {
    @Multipart
    @POST("storage/v1/object/{bucket}/{path}")
    suspend fun uploadFile(
        @Path("bucket") bucket: String,
        @Path("path") path: String,
        @Part file: MultipartBody.Part
    ): Response<Unit>
}