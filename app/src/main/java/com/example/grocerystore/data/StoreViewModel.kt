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
        val currentCart = _cart.value?.toMutableList() ?: mutableListOf()
        val existingItem = currentCart.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity++
            println("Increased quantity for ${product.name} to ${existingItem.quantity}")
        } else {
            currentCart.add(CartItem(product, 1))
            println("Added new item: ${product.name}")
        }
        // Явно устанавливаем новое значение, чтобы LiveData уведомило подписчиков
        _cart.value = currentCart
        println("Current cart size: ${currentCart.size}")
    }

    fun removeFromCart(cartItem: CartItem) {
        val currentCart = _cart.value?.toMutableList() ?: return
        currentCart.remove(cartItem)
        _cart.value = currentCart
        println("Removed item. Current cart size: ${currentCart.size}")
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        val currentCart = _cart.value?.toMutableList() ?: return
        if (newQuantity <= 0) {
            currentCart.remove(cartItem)
            println("Removed item due to quantity 0")
        } else {
            cartItem.quantity = newQuantity
            println("Updated quantity for ${cartItem.product.name} to $newQuantity")
        }
        _cart.value = currentCart
    }
}