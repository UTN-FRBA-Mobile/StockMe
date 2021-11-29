package com.stockme.model

import java.io.Serializable

data class Product(val image: String? = null,
                   val code: String,
                   val currentStock: Int,
                   val maxStock: Int,
                   val minStock: Int,
                   val price: String,
                   val isEnable: Boolean = true,
                   val description: String,
                   val id: String? = null) : Serializable
