package com.example.grocerystore.screens

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
    viewModel: StoreViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) } // true - логин, false - регистрация
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
            // Поле для имени пользователя
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Имя пользователя") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Поле для пароля
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Сообщение об ошибке
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Кнопка действия (Войти/Зарегистрироваться)
            Button(
                onClick = {
                    if (isLoginMode) {
                        // Логин
                        if (viewModel.login(username, password)) {
                            navController.navigate("products") {
                                popUpTo("login") { inclusive = true } // Убираем экран логина из стека
                            }
                        } else {
                            errorMessage = "Неверное имя пользователя или пароль"
                        }
                    } else {
                        // Регистрация
                        if (viewModel.register(username, password)) {
                            navController.navigate("products") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            errorMessage = "Пользователь уже существует или данные некорректны"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoginMode) "Войти" else "Зарегистрироваться")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка переключения между логином и регистрацией
            TextButton(onClick = { isLoginMode = !isLoginMode }) {
                Text(if (isLoginMode) "Нет аккаунта? Зарегистрируйтесь" else "Уже есть аккаунт? Войдите")
            }
        }
    }
}