package com.example.grocerystore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grocerystore.data.Product
import com.example.grocerystore.data.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    viewModel: StoreViewModel
) {
    val products = viewModel.products.observeAsState(emptyList()).value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Продукты") },
                actions = {
                    Button(onClick = { navController.navigate("cart") }) {
                        Text("Корзина")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(products) { product ->
                ProductItem(product = product, onAddToCart = { viewModel.addToCart(product) })
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = product.description, style = MaterialTheme.typography.bodySmall)
                Text(text = "$${product.price}", style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = {
                onAddToCart()
                println("Button clicked for ${product.name}")
            }) {
                Text("Добавить")
            }
        }
    }
}