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
fun RegisterScreen(
    onRegisterSuccess: (isStaff: Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }

    val passwordMismatch = password.isNotEmpty() && password2.isNotEmpty() && password != password2

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess((uiState as AuthUiState.Success).user.isStaff)
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
                .padding(top = 40.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Busify", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(text = "Registro de Personal Técnico y Operadores", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(32.dp))

            Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (errorMsg != null) {
                        Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth()) {
                            Text(text = errorMsg, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    ShopTextField(value = username, onValueChange = { username = it; viewModel.clearError() }, label = "Usuario", placeholder = "mínimo 3 caracteres", enabled = !isLoading, isError = username.isNotEmpty() && username.length < 3, errorMessage = "Mínimo 3 caracteres", imeAction = ImeAction.Next)
                    Spacer(Modifier.height(14.dp))
                    ShopTextField(value = email, onValueChange = { email = it; viewModel.clearError() }, label = "Email Institucional", placeholder = "tu@daelabs.tech", enabled = !isLoading, keyboardType = KeyboardType.Email, isError = email.isNotEmpty() && !email.contains("@"), errorMessage = "Email inválido", imeAction = ImeAction.Next)
                    Spacer(Modifier.height(14.dp))
                    ShopTextField(value = password, onValueChange = { password = it; viewModel.clearError() }, label = "Contraseña", placeholder = "mínimo 8 caracteres", isPassword = true, enabled = !isLoading, keyboardType = KeyboardType.Password, isError = password.isNotEmpty() && password.length < 8, errorMessage = "Mínimo 8 caracteres", imeAction = ImeAction.Next)
                    Spacer(Modifier.height(14.dp))
                    ShopTextField(value = password2, onValueChange = { password2 = it; viewModel.clearError() }, label = "Confirmar Contraseña", placeholder = "repite la contraseña", isPassword = true, enabled = !isLoading, keyboardType = KeyboardType.Password, isError = passwordMismatch, errorMessage = "Las contraseñas no coinciden", imeAction = ImeAction.Done)
                    Spacer(Modifier.height(24.dp))

                    val canSubmit = username.length >= 3 && email.contains("@") && password.length >= 8 && !passwordMismatch && !isLoading

                    ShopButton(text = "Registrar Operador", onClick = { viewModel.register(username, email, password, password2) }, isLoading = isLoading, enabled = canSubmit)
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "¿Ya tienes un usuario? ", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToLogin) {
                    Text(text = "Inicia sesión", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}