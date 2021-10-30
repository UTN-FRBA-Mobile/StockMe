package com.stockme.product

data class Product(
    val code: String,
    val description: String,
    val image: String?,
    val price: Double,
    val active: Boolean = true,
    val quantity: Int,
    val minQuantity: Int = 0,
    val maxQuantity: Int,
)
