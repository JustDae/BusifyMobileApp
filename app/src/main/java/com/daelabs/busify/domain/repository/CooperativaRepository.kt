package com.daelabs.busify.domain.repository

import com.daelabs.busify.domain.model.Cooperativa

interface CooperativaRepository {
    suspend fun getCooperativas(): Result<List<Cooperativa>>
}