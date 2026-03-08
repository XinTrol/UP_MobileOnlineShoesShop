package com.example.up_mobileappv2.domain.repository

import com.example.up_mobileappv2.domain.model.Action

interface ActionRepository {
    suspend fun getActions(): List<Action>
}