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
            // Если токен не пришёл – считаем, что требуется подтверждение email, но регистрация прошла
            response.accessToken?.let { tokenManager.saveToken(it) } ?: tokenManager.clearToken()
            Result.success(response.user.toDomain())
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
            // При входе токен должен быть обязательно
            if (response.accessToken == null) {
                throw Exception("Сервер не вернул токен доступа")
            }
            tokenManager.saveToken(response.accessToken)
            Result.success(response.user.toDomain())
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
    }

    override suspend fun verifyRecoveryCode(email: String, code: String): Result<User> {
        return try {
            val response = authApi.verify(VerifyRequest(type = "recovery", token = code, email = email))
            response.accessToken?.let { tokenManager.saveToken(it) }
            Result.success(response.user.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception("Неверный код"))
        } catch (e: IOException) {
            Result.failure(Exception("Проверьте подключение к интернету"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}