package com.example.up_mobileappv2.data.repository

import com.example.up_mobileappv2.data.dto.auth.*
import com.example.up_mobileappv2.data.mapper.toDomain
import com.example.up_mobileappv2.data.remote.AuthApi
import com.example.up_mobileappv2.domain.model.User
import com.example.up_mobileappv2.domain.repository.AuthRepository
import com.example.up_mobileappv2.domain.repository.TokenManager
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun signUp(email: String, password: String): Result<User> {
        return try {
            val response = authApi.signUp(SignUpRequest(email, password))
            response.accessToken?.let { tokenManager.saveToken(it) } ?: tokenManager.clearToken()
            // Проверяем наличие user
            val user = response.user ?: throw Exception("Сервер не вернул данные пользователя")
            // Сохраняем userId
            tokenManager.saveUserId(user.id)
            Result.success(user.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception("Ошибка сервера: ${e.code()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Проверьте подключение к интернету"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val response = authApi.signIn(SignInRequest(email, password))
            if (response.accessToken == null) {
                throw Exception("Сервер не вернул токен доступа")
            }
            tokenManager.saveToken(response.accessToken)
            val user = response.user ?: throw Exception("Сервер не вернул данные пользователя")
            tokenManager.saveUserId(user.id)
            Result.success(user.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception("Неверный email или пароль"))
        } catch (e: IOException) {
            Result.failure(Exception("Проверьте подключение к интернету"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            authApi.forgotPassword(ForgotPasswordRequest(email))
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception("Ошибка сервера: ${e.code()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Проверьте подключение к интернету"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val token = tokenManager.getToken() ?: throw Exception("Не авторизован")
            authApi.updateUser("Bearer $token", UpdatePasswordRequest(newPassword))
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception("Ошибка сервера: ${e.code()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Проверьте подключение к интернету"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
        // При желании можно также очистить userId, но clearToken уже сбрасывает всё, если хранить в одном месте.
        // Если userId хранится отдельно, добавьте: tokenManager.clearUserId()
    }

    override suspend fun verifyRecoveryCode(email: String, code: String): Result<User> {
        return try {
            val response = authApi.verify(VerifyRequest(type = "recovery", token = code, email = email))
            response.accessToken?.let { tokenManager.saveToken(it) }
            val user = response.user ?: throw Exception("Сервер не вернул данные пользователя")
            tokenManager.saveUserId(user.id)
            Result.success(user.toDomain())
        } catch (e: HttpException) {
            println("Verify error: ${e.response()?.errorBody()?.string()}")
            Result.failure(Exception("Неверный код"))
        } catch (e: IOException) {
            Result.failure(Exception("Проверьте подключение к интернету"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}