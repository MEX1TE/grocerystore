package com.example.grocerystore.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class StoreViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _cart = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cart: LiveData<MutableList<CartItem>> = _cart

    private val client = OkHttpClient()

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
        } else {
            currentCart.add(CartItem(product, 1))
        }
        _cart.value = currentCart
    }

    fun removeFromCart(cartItem: CartItem) {
        val currentCart = _cart.value?.toMutableList() ?: return
        currentCart.remove(cartItem)
        _cart.value = currentCart
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        val currentCart = _cart.value?.toMutableList() ?: return
        if (newQuantity <= 0) {
            currentCart.remove(cartItem)
        } else {
            cartItem.quantity = newQuantity
        }
        _cart.value = currentCart
    }

    fun clearCart() {
        _cart.value = mutableListOf()
    }

    fun register(username: String, password: String, callback: (Boolean, String?) -> Unit) {
        val json = JSONObject().apply {
            put("username", username)
            put("password", password)
        }
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url("http://10.0.2.2:5000/register")
            .post(body)
            .build()

        println("Attempting to register at: ${request.url}")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Registration failed: ${e.message}")
                viewModelScope.launch(Dispatchers.Main) {
                    callback(false, e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                println("Registration response: $responseBody")
                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        callback(true, null)
                    } else {
                        val error = JSONObject(responseBody ?: "{}").getString("error")
                        callback(false, error)
                    }
                }
            }
        })
    }

    fun login(username: String, password: String, callback: (Boolean, Int?, String?) -> Unit) {
        val json = JSONObject().apply {
            put("username", username)
            put("password", password)
        }
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url("http://10.0.2.2:5000/login")
            .post(body)
            .build()

        println("Attempting to login at: ${request.url}")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Login failed: ${e.message}")
                viewModelScope.launch(Dispatchers.Main) {
                    callback(false, null, e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                println("Login response: $responseBody")
                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val userId = jsonResponse.getInt("user_id")
                        callback(true, userId, null)
                    } else {
                        val error = JSONObject(responseBody ?: "{}").getString("error")
                        callback(false, null, error)
                    }
                }
            }
        })
    }

    fun placeOrder(userId: Int, callback: (Boolean, String?) -> Unit) {
        val cartItems = _cart.value ?: return
        val itemsArray = JSONArray().apply {
            cartItems.forEach {
                put(JSONObject().apply {
                    put("product_name", it.product.name)
                    put("quantity", it.quantity)
                    put("price", it.product.price)
                })
            }
        }
        val json = JSONObject().apply {
            put("user_id", userId)
            put("items", itemsArray)
        }
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url("http://10.0.2.2:5000/place_order")
            .post(body)
            .build()

        println("Attempting to place order at: ${request.url}")
        println("Request body: ${json.toString()}") // Отладка отправляемых данных
        println("Request body: ${json.toString(2)}") // Форматированный вывод JSON
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Order failed: ${e.message}")
                viewModelScope.launch(Dispatchers.Main) {
                    callback(false, e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                println("Order response code: ${response.code}")
                println("Order response body: $responseBody") // Отладка полного ответа
                viewModelScope.launch(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        clearCart()
                        callback(true, null)
                    } else {
                        val error = try {
                            JSONObject(responseBody ?: "{}").getString("error")
                        } catch (e: Exception) {
                            "Invalid response: $responseBody" // Если не JSON, возвращаем сырой текст
                        }
                        callback(false, error)
                    }
                }
            }
        })
    }
}