package com.stockme

import android.app.Application
import com.google.firebase.FirebaseApp

class StockMeApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}