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

    @Binds @Singleton
    abstract fun bindDespachoRepository(impl: DespachoRepositoryImpl): DespachoRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds @Singleton
    abstract fun bindViajeRepository(impl: ViajeRepositoryImpl): ViajeRepository

    @Binds @Singleton
    abstract fun bindBusRepository(impl: BusRepositoryImpl): BusRepository

    @Binds @Singleton
    abstract fun bindChoferRepository(impl: ChoferRepositoryImpl): ChoferRepository
}