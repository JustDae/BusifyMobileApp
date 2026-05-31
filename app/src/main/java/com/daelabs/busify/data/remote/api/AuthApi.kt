package com.daelabs.busify.data.remote.api

import com.daelabs.busify.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login/")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponseDto>

    @POST("auth/register/")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponseDto>

    @POST("auth/token/refresh/")
    suspend fun refreshToken(@Body body: TokenRefreshRequest): Response<TokenRefreshResponseDto>

    @POST("auth/logout/")
    suspend fun logout(@Body body: LogoutRequest): Response<Unit>
}