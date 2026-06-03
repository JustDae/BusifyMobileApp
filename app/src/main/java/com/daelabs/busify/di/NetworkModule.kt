package com.daelabs.busify.di

import com.daelabs.busify.BuildConfig
import com.daelabs.busify.data.local.TokenDataStore
import com.daelabs.busify.data.remote.api.*
import com.daelabs.busify.data.remote.interceptor.AuthAuthenticator
import com.daelabs.busify.data.remote.interceptor.BearerTokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenDataStore: TokenDataStore,
        authAuthenticator: AuthAuthenticator,
        logging: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(BearerTokenInterceptor(tokenDataStore))
            .addInterceptor(logging)
            .authenticator(authAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideBusApi(retrofit: Retrofit): BusApi = retrofit.create(BusApi::class.java)

    @Provides
    @Singleton
    fun provideRutaApi(retrofit: Retrofit): RutaApi = retrofit.create(RutaApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideViajeApi(retrofit: Retrofit): ViajeApi = retrofit.create(ViajeApi::class.java)

    @Provides
    @Singleton
    fun provideCooperativaApi(retrofit: Retrofit): CooperativaApi = retrofit.create(CooperativaApi::class.java)

    @Provides
    @Singleton
    fun provideDespachoApi(retrofit: Retrofit): DespachoApi {
        return retrofit.create(DespachoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChoferApi(retrofit: Retrofit): ChoferApi = retrofit.create(ChoferApi::class.java)
}