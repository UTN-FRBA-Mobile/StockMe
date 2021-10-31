package com.stockme.buy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stockme.databinding.ActivityBuyBinding

class BuyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchView2.setOnClickListener {  }
    }
}