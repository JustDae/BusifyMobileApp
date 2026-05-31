package com.daelabs.busify.presentation.ui.auth

import com.daelabs.busify.domain.model.LoggedUser

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val user: LoggedUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}