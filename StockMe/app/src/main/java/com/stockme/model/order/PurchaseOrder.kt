package com.stockme.model.order

import com.google.type.Date

data class PurchaseOrder(val code: String,
                         val createdDate: Date,
                         val admissionDate: Date,
                         val supplier: String,
                         val products: List<ProductOrder>,
        )