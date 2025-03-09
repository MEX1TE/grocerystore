package com.example.grocerystore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grocerystore.screens.CartScreen
import com.example.grocerystore.screens.ProductListScreen
import com.example.grocerystore.theme.GroceryStoreTheme

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
        NavHost(navController = navController, startDestination = "products") {
            composable("products") {
                ProductListScreen(navController)
            }
            composable("cart") {
                CartScreen(navController)
            }
        }
    }
}