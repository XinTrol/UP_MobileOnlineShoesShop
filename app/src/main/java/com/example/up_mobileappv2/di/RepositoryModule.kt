package com.example.up_mobileappv2.di

import com.example.up_mobileappv2.data.repository.ActionRepositoryImpl
import com.example.up_mobileappv2.data.repository.AuthRepositoryImpl
import com.example.up_mobileappv2.data.repository.ProductRepositoryImpl
import com.example.up_mobileappv2.data.repository.TokenManagerImpl
import com.example.up_mobileappv2.domain.repository.ActionRepository
import com.example.up_mobileappv2.domain.repository.AuthRepository
import com.example.up_mobileappv2.domain.repository.ProductRepository
import com.example.up_mobileappv2.domain.repository.TokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenManager(impl: TokenManagerImpl): TokenManager

    @Binds
    @Singleton
    abstract fun bindActionRepository(impl: ActionRepositoryImpl): ActionRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

}