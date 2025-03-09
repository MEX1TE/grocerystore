package com.example.grocerystore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grocerystore.data.StoreViewModel
import com.example.grocerystore.theme.GroceryStoreTheme
import com.example.grocerystore.ui.screens.CartScreen
import com.example.grocerystore.ui.screens.ProductListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroceryStoreApp()
        }
    }
}

@Composable
fun GroceryStoreApp() {
    GroceryStoreTheme {
        val navController = rememberNavController()
        val viewModel: StoreViewModel = viewModel() // Создаём ViewModel здесь

        NavHost(navController = navController, startDestination = "products") {
            composable("products") {
                ProductListScreen(navController, viewModel)
            }
            composable("cart") {
                CartScreen(navController, viewModel)
            }
        }
    }
}