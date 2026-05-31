package com.daelabs.busify.presentation.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daelabs.busify.presentation.components.ShopButton
import com.daelabs.busify.presentation.components.ShopTextField
import com.daelabs.busify.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (isStaff: Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess((uiState as AuthUiState.Success).user.isStaff)
        }
    }

    val isLoading = uiState is AuthUiState.Loading
    val errorMsg = (uiState as? AuthUiState.Error)?.message

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Busify", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(text = "Gestión y Control de Transporte Público", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(40.dp))

            Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (errorMsg != null) {
                        Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
                            Text(text = errorMsg, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    ShopTextField(value = username, onValueChange = { username = it; viewModel.clearError() }, label = "Usuario / Email", placeholder = "dae_labs", enabled = !isLoading, imeAction = ImeAction.Next)
                    Spacer(Modifier.height(16.dp))
                    ShopTextField(value = password, onValueChange = { password = it; viewModel.clearError() }, label = "Contraseña", placeholder = "••••••••", isPassword = true, enabled = !isLoading, keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                    Spacer(Modifier.height(24.dp))

                    ShopButton(text = "Ingresar al Sistema", onClick = { viewModel.login(username, password) }, isLoading = isLoading, enabled = username.isNotBlank() && password.isNotBlank())
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "¿No eres parte de la red? ", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToRegister) {
                    Text(text = "Regístrate aquí", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}