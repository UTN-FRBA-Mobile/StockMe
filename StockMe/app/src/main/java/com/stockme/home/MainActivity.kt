package com.stockme.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.stockme.R
import com.stockme.product.ProductListActivity
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_main)
class MainActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, ProductListActivity::class.java)
        startActivity(intent)
        finish()
    }
}