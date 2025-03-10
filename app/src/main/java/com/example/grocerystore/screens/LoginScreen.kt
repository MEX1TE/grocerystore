package com.example.grocerystore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grocerystore.data.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: StoreViewModel,
    onLoginSuccess: (Int) -> Unit // Добавляем колбек для передачи userId
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isLoginMode) "Вход" else "Регистрация") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Имя пользователя") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (isLoginMode) {
                        viewModel.login(username, password) { success, id, error ->
                            if (success && id != null) {
                                onLoginSuccess(id) // Передаём userId
                                navController.navigate("products") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                errorMessage = error ?: "Ошибка входа"
                            }
                        }
                    } else {
                        viewModel.register(username, password) { success, error ->
                            if (success) {
                                // После регистрации сразу логинимся для получения userId
                                viewModel.login(username, password) { loginSuccess, id, loginError ->
                                    if (loginSuccess && id != null) {
                                        onLoginSuccess(id)
                                        navController.navigate("products") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        errorMessage = loginError ?: "Ошибка после регистрации"
                                    }
                                }
                            } else {
                                errorMessage = error ?: "Ошибка регистрации"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoginMode) "Войти" else "Зарегистрироваться")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { isLoginMode = !isLoginMode }) {
                Text(if (isLoginMode) "Нет аккаунта? Зарегистрируйтесь" else "Уже есть аккаунт? Войдите")
            }
        }
    }
}