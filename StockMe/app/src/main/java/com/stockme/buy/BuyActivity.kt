package com.stockme.buy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.stockme.buy.ProductList.ProductAdapter
import com.stockme.buy.models.Product
import com.stockme.databinding.ActivityBuyBinding

class BuyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuyBinding

    private val adapter = ProductAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchView2.setOnClickListener {  }

        binding.floatingCart.setOnClickListener {  }

        binding.itemList.layoutManager = LinearLayoutManager(this)
        binding.itemList.adapter = adapter

        val sampleData = listOf(
            Product("producto 1"),
            Product("producto 2"),
            Product("producto 3"),
            Product("producto 4"),
            Product("producto 5"),
            Product("producto 6"),
            Product("producto 7"),
            Product("producto 8"),
            Product("producto 9"),
            Product("producto 10")
        )

        adapter.submitList(sampleData)
    }
}