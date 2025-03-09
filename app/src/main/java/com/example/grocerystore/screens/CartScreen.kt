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
import com.example.grocerystore.data.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: StoreViewModel
) {
    val cartItems = viewModel.cart.observeAsState(emptyList()).value
    val total = cartItems.sumOf { it.product.price * it.quantity }

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
        ) {
            if (cartItems.isEmpty()) {
                Text("Корзина пуста", modifier = Modifier.align(Alignment.CenterHorizontally))
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
            Text(
                text = "Итого: $${"%.2f".format(total)}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: com.example.grocerystore.data.CartItem,
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