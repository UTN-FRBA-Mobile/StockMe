package com.stockme.model.order

import com.google.type.Date

data class SaleOrder(val code: String,
                         val createdDate: Date,
                         val saleDate: Date,
                         val client: String,
                         val products: List<ProductOrder>,
)