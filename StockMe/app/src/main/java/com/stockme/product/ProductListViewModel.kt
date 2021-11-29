package com.stockme.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stockme.model.Product

class ProductListViewModel : ViewModel() {
    private val firestoreDB = Firebase.firestore

    private val _fetchProductsLiveData = MutableLiveData<List<Product>?>()
    val fetchProductsLiveData: MutableLiveData<List<Product>?> get() = _fetchProductsLiveData

    fun fetchProducts() {
        firestoreDB
            .collection("products")
            .get()
            .addOnSuccessListener { documents ->
                val products = ArrayList<Product>()

                for (document in documents) {
                    val product = mapDataToProduct(document)
                    products.add(product)
                }

                _fetchProductsLiveData.value = products
            }
            .addOnFailureListener { exception ->
                _fetchProductsLiveData.value = null
            }
    }


    private fun mapDataToProduct(snapshot: QueryDocumentSnapshot): Product {
        val hashMap = snapshot.data as HashMap<String, Any>
        return Product(image = hashMap["image"] as String,
            code = hashMap["code"] as String,
            currentStock = (hashMap["currentStock"] as Long).toInt(),
            maxStock = (hashMap["maxStock"] as Long).toInt(),
            minStock = (hashMap["minStock"] as Long).toInt(),
            price = hashMap["price"] as String,
            isEnable = hashMap["enable"] as Boolean,
            description = hashMap["description"] as String,
            id = snapshot.id)
    }
}