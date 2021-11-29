package com.stockme.home.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.stockme.R
import com.stockme.databinding.ActivityCartBinding
import com.stockme.home.ui.sales.SalesFragment
import com.stockme.home.ui.sales.SalesProductAdapter
import com.stockme.model.Product


class CartActivity : AppCompatActivity() {

    private var _binding: ActivityCartBinding? = null

    private val binding get() = _binding!!

    private lateinit var cart : List<Product>
    private var isSales: Boolean = true
    private lateinit var cartAdapter: SalesProductAdapter

    private val firestoreDB = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityCartBinding.inflate(layoutInflater)

        setContentView(binding.root)

        cart = intent.extras?.get("cart") as ArrayList<Product>
        isSales = intent.extras?.get("isSales") as Boolean

        cartAdapter = SalesProductAdapter(cart)
        binding.productList.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@CartActivity, DividerItemDecoration.VERTICAL))
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sales_cart, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var titleResId = R.string.dialog_cart_confirm_text
        var messageResId = R.string.dialog_cart_confirm_desc
        var confirmMessageResId = R.string.dialog_cart_confirm_sale_text
        var multiplier = -1

        if (!isSales) {
            titleResId = R.string.dialog_cart_stock_confirm_text
            messageResId = R.string.dialog_cart_stock_confirm_desc
            confirmMessageResId = R.string.dialog_cart_confirm_stock_text
            multiplier = 1
        }
        if (item.itemId == R.id.action_confirm) {
            MaterialDialog(this).show {
                title(titleResId)
                message(messageResId)
                positiveButton(R.string.dialog_cart_yes) {
                    val batchUpdate = firestoreDB.batch()
                    for (product in cart) {
                        val decrementQuantity: Long = (multiplier * product.currentStock).toLong()
                        val prodByIdRef = firestoreDB.collection("products")
                                .document(product.id!!)
                        batchUpdate.update(prodByIdRef, "currentStock", FieldValue.increment(decrementQuantity))
                    }
                    batchUpdate.commit().addOnCompleteListener {
                        Snackbar.make(binding.root, confirmMessageResId, Snackbar.LENGTH_LONG).show()

                        setResult(SalesFragment.CART_TRANSACTION_COMPLETE)
                        finish()
                    }
                }
                negativeButton(R.string.dialog_cart_no)
            }
            true
        }
        return super.onOptionsItemSelected(item)
    }
}