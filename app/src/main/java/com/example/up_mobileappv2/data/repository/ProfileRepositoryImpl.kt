package com.example.up_mobileappv2.data.repository

import com.example.up_mobileappv2.data.dto.ProfileCreateDto
import com.example.up_mobileappv2.data.dto.ProfileDto
import com.example.up_mobileappv2.data.dto.ProfileUpdateDto
import com.example.up_mobileappv2.data.mapper.toDomain
import com.example.up_mobileappv2.data.remote.DatabaseApi
import com.example.up_mobileappv2.data.remote.StorageApi
import com.example.up_mobileappv2.data.remote.SupabaseConfig
import com.example.up_mobileappv2.domain.model.Profile
import com.example.up_mobileappv2.domain.repository.ProfileRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val databaseApi: DatabaseApi,
    private val storageApi: StorageApi
) : ProfileRepository {

    override suspend fun createProfile(profile: Profile): Result<Unit> {
        return try {
            val createDto = ProfileCreateDto(
                user_id = profile.userId,
                firstname = profile.firstName,
                lastname = profile.lastName,
                address = profile.address,
                phone = profile.phone,
                photo = profile.photo
            )
            val response = databaseApi.createProfile(createDto)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                println("❌ Create profile failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Ошибка создания профиля: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("💥 Exception in createProfile: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getProfile(userId: String): Profile? {
        val filter = "eq.$userId"
        val profiles = databaseApi.getProfile(userFilter = filter)
        return profiles.firstOrNull()?.toDomain()
    }

    override suspend fun updateProfile(profile: Profile): Result<Unit> {
        return try {
            println("🟡 Updating profile for userId: ${profile.userId}")
            val updateDto = ProfileUpdateDto(
                firstname = profile.firstName,
                lastname = profile.lastName,
                address = profile.address,
                phone = profile.phone,
                photo = profile.photo
            )
            println("🟡 Sending PATCH with body: $updateDto")
            val response = databaseApi.updateProfile(
                userFilter = "eq.${profile.userId}",
                updates = updateDto
            )
            println("🟡 Response code: ${response.code()}")
            if (response.isSuccessful) {
                println("✅ Update successful")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                println("❌ Update failed: ${response.code()} - $errorBody")
                Result.failure(Exception("Ошибка сервера: ${response.code()} - $errorBody"))
            }
        } catch (e: HttpException) {
            println("🔥 HTTP Exception: ${e.code()} - ${e.response()?.errorBody()?.string()}")
            Result.failure(Exception("Ошибка сервера: ${e.code()}"))
        } catch (e: IOException) {
            println("📡 Network error: ${e.message}")
            Result.failure(Exception("Проверьте подключение к интернету"))
        } catch (e: Exception) {
            println("💥 Unexpected error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePhoto(userId: String, file: File): String? {
        return try {
            println("🟡 uploadProfilePhoto: userId=$userId, file=${file.absolutePath}, size=${file.length()}")
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val path = "profiles/$userId/${file.name}"
            println("🟡 uploadProfilePhoto: bucket=avatars, path=$path")
            val response = storageApi.uploadFile(
                bucket = "avatars",
                path = path,
                file = part
            )
            println("🟡 uploadProfilePhoto: response code = ${response.code()}")
            if (response.isSuccessful) {
                val url = "${SupabaseConfig.BASE_URL}/storage/v1/object/public/avatars/$path"
                println("✅ uploadProfilePhoto success: $url")
                url
            } else {
                val error = response.errorBody()?.string()
                println("❌ uploadProfilePhoto failed: ${response.code()} - $error")
                null
            }
        } catch (e: Exception) {
            println("💥 uploadProfilePhoto exception: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}