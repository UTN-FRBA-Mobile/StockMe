package com.stockme.utils

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


fun AppCompatActivity.showSnackBar(rootView: View, text: Int) = Snackbar.make(rootView, text, Snackbar.LENGTH_LONG).show()