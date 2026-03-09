package com.example.up_mobileappv2.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.up_mobileappv2.domain.repository.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("auth_prefs")

@Singleton
class TokenManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenManager {

    private val tokenKey = stringPreferencesKey("jwt_token")

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    override suspend fun getToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[tokenKey] }
            .first()
    }

    override suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }

    private val userIdKey = stringPreferencesKey("user_id")

    override suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[userIdKey] = userId
        }
    }

    override suspend fun getUserId(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[userIdKey] }
            .first()
    }
}