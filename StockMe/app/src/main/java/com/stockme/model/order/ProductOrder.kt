package com.stockme.model.order

import com.stockme.model.Product

data class ProductOrder  (
    val product: Product,
    val stock: Int,
)