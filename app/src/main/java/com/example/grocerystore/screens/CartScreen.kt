package com.example.grocerystore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.grocerystore.data.StoreViewModel
import com.example.grocerystore.data.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: StoreViewModel,
    userId: Int
) {
    val cartItems = viewModel.cart.observeAsState(emptyList()).value
    val total = cartItems.sumOf { it.product.price * it.quantity }
    var showOrderConfirmation by remember { mutableStateOf(false) }

    println("CartScreen: Current cart items: ${cartItems.size}") // Отладка

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Корзина") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Text("Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (cartItems.isEmpty() && !showOrderConfirmation) {
                Text("Корзина пуста", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (showOrderConfirmation) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Заказ успешно оформлен!",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        showOrderConfirmation = false
                        navController.navigate("products")
                    }) {
                        Text("Вернуться к покупкам")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            item = item,
                            onRemove = { viewModel.removeFromCart(item) },
                            onQuantityChange = { newQty -> viewModel.updateQuantity(item, newQty) }
                        )
                    }
                }
            }

            if (!showOrderConfirmation && cartItems.isNotEmpty()) {
                Column {
                    Text(
                        text = "Итого: $${"%.2f".format(total)}",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.placeOrder(userId) { success, error ->
                                if (success) {
                                    showOrderConfirmation = true
                                } else {
                                    println("Order error: $error")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = cartItems.isNotEmpty()
                    ) {
                        Text("Оформить заказ")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
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
                Text(text = item.product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "$${item.product.price}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { onQuantityChange(item.quantity - 1) }) {
                    Text("-")
                }
                Text(
                    text = item.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Button(onClick = { onQuantityChange(item.quantity + 1) }) {
                    Text("+")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onRemove) {
                    Text("Удалить")
                }
            }
        }
    }
}