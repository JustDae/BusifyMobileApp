package com.daelabs.busify.di

import com.daelabs.busify.data.repository.*
import com.daelabs.busify.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindCooperativaRepository(impl: CooperativaRepositoryImpl): CooperativaRepository

    @Binds @Singleton
    abstract fun bindRutaRepository(impl: RutaRepositoryImpl): RutaRepository
}