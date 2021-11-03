package com.stockme.productdetail.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.stockme.model.Product
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap

class ProductDetailViewModel: ViewModel() {
    private val firestoreDB = Firebase.firestore
    private val storage = Firebase.storage
    private val _fetchProductLiveData = MutableLiveData<Product?>()
    private val _createProductLiveData = MutableLiveData<Boolean>()
    private val _editProductLiveData = MutableLiveData<Boolean>()
    private val _uploadProductPhotoLiveData = MutableLiveData<String?>()

    val fetchProductLiveData: MutableLiveData<Product?> get() = _fetchProductLiveData
    val createProductLiveData: MutableLiveData<Boolean> get() = _createProductLiveData
    val editProductLiveData: MutableLiveData<Boolean> get() = _editProductLiveData
    val uploadProductPhotoLiveData: MutableLiveData<String?> get() = _uploadProductPhotoLiveData

    fun fetchProduct(productId: String) {
        firestoreDB
            .collection("products")
            .document(productId)
            .get()
            .addOnCompleteListener { snapshot ->
                if (snapshot.isSuccessful) {
                    val product = snapshot.result?.let {
                        mapDataToProduct(it.data as HashMap<String, Any>)
                    }
                    _fetchProductLiveData.value = product
                } else {
                        _fetchProductLiveData.value = null
                }
        }
    }

    fun createProduct(product: Product) {
        firestoreDB
            .collection("products")
            .add(mapProductToData(product))
            .addOnSuccessListener {
                _createProductLiveData.value = true
            }
            .addOnFailureListener {
                _createProductLiveData.value = false
            }
    }

    fun uploadProductPhoto(bitmap: Bitmap) {
        val storageRef = storage.reference
        val photoUUID = UUID.randomUUID().toString()
        val productsRef = storageRef.child("${photoUUID}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = productsRef.putBytes(data)
        uploadTask
            .addOnFailureListener {
                uploadProductPhotoLiveData.value = null
            }
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener{ task ->
                    uploadProductPhotoLiveData.value = task.result.toString()
                }
            }
    }

    fun editProduct(product: Product, productId: String) {
        firestoreDB
            .collection("products")
            .document(productId)
            .update(mapProductToData(product))
            .addOnSuccessListener {
                _editProductLiveData.value = true
            }
            .addOnFailureListener {
                _editProductLiveData.value = false
            }
    }

    private fun mapDataToProduct(hashMap: HashMap<String, Any>) =
        Product(image = hashMap["image"] as String,
                code = hashMap["code"] as String,
                currentStock = (hashMap["currentStock"] as Long).toInt(),
                maxStock = (hashMap["maxStock"] as Long).toInt(),
                minStock = (hashMap["minStock"] as Long).toInt(),
                price = hashMap["price"] as String,
                isEnable = hashMap["enable"] as Boolean,
                description = hashMap["description"] as String)

    private fun mapProductToData(product: Product) = hashMapOf(
        "image" to product.image,
        "code" to product.code,
        "currentStock" to product.currentStock,
        "maxStock" to product.maxStock,
        "minStock" to product.minStock,
        "price" to product.price,
        "enable" to product.isEnable,
        "description" to product.description
    )
}