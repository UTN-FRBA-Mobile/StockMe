package com.stockme.model
import com.google.type.Date
data class OrderPurchase(val code: String,
                         val createdDate: String,
                         val admissionDate: String,
                         val supplier: String,
                         //val products: List<OrderProduct>,
)