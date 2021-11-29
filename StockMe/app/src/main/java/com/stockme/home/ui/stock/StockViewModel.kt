package com.stockme.home.ui.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StockViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Acá van las órdenes de compra"
    }
    val text: LiveData<String> = _text
}