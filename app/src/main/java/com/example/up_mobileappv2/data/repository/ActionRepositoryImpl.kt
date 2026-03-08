package com.example.up_mobileappv2.data.repository

import com.example.up_mobileappv2.data.mapper.toDomain
import com.example.up_mobileappv2.data.remote.DatabaseApi
import com.example.up_mobileappv2.domain.model.Action
import com.example.up_mobileappv2.domain.repository.ActionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionRepositoryImpl @Inject constructor(
    private val databaseApi: DatabaseApi
) : ActionRepository {
    override suspend fun getActions(): List<Action> {
        return databaseApi.getActions().map { it.toDomain() }
    }
}