package com.example.grocerystore.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StoreViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _cart = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cart: LiveData<MutableList<CartItem>> = _cart

    init {
        loadProducts()
    }

    private fun loadProducts() {
        _products.value = listOf(
            Product(1, "Молоко", 1.99, "1 литр свежего молока"),
            Product(2, "Хлеб", 0.99, "Свежий белый хлеб"),
            Product(3, "Яблоки", 2.49, "1 кг зеленых яблок"),
            Product(4, "Масло", 3.49, "200 г сливочного масла")
        )
    }

    fun addToCart(product: Product) {
        val currentCart = _cart.value ?: mutableListOf()
        val existingItem = currentCart.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentCart.add(CartItem(product, 1))
        }
        _cart.value = currentCart
    }

    fun removeFromCart(cartItem: CartItem) {
        val currentCart = _cart.value ?: return
        currentCart.remove(cartItem)
        _cart.value = currentCart
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        val currentCart = _cart.value ?: return
        if (newQuantity <= 0) {
            currentCart.remove(cartItem)
        } else {
            cartItem.quantity = newQuantity
        }
        _cart.value = currentCart
    }
}