package com.stockme.model

data class Product(val image: String?,
                   val code: String,
                   val currentStock: Int,
                   val maxStock: Int,
                   val minStock: Int,
                   val price: String,
                   val isEnable: Boolean,
                   val description: String)