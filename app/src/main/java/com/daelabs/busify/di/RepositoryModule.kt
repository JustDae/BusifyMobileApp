package com.daelabs.busify.di

import com.daelabs.busify.data.repository.AuthRepositoryImpl
import com.daelabs.busify.data.repository.BusRepositoryImpl
import com.daelabs.busify.data.repository.RutaRepositoryImpl
import com.daelabs.busify.domain.repository.AuthRepository
import com.daelabs.busify.domain.repository.BusRepository
import com.daelabs.busify.domain.repository.RutaRepository
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
    abstract fun bindBusRepository(impl: BusRepositoryImpl): BusRepository

    @Binds
    @Singleton
    abstract fun bindRutaRepository(impl: RutaRepositoryImpl): RutaRepository
}