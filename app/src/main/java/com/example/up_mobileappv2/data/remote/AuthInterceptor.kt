package com.example.up_mobileappv2.data.remote

import com.example.up_mobileappv2.domain.repository.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        // Используем runBlocking для получения токена из suspend-функции.
        // Внимание: это может заблокировать поток, но в интерцепторе это допустимо.
        val token = runBlocking { tokenManager.getToken() }
        val requestBuilder = original.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }
}