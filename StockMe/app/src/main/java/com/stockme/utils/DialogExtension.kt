package com.stockme.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

const val PROGRESS_TAG = "Progress"
const val PROGRESS_TITLE = "title"
const val PROGRESS_SUBTITLE = "subtitle"

fun AppCompatActivity.showProgress(tag: String = PROGRESS_TAG) {
    if (supportFragmentManager.findFragmentByTag(tag) == null)
        ProgressDialogFragment.newInstance().show(supportFragmentManager, tag)
}

fun AppCompatActivity.showProgress(tag: String = PROGRESS_TAG, title: String, subtitle: String) {
    if (supportFragmentManager.findFragmentByTag(tag) == null) {
        ProgressDialogFragment.newInstance().apply {
            arguments = Bundle().apply {
                putString(PROGRESS_TITLE, title)
                putString(PROGRESS_SUBTITLE, subtitle)
            }
        }.show(supportFragmentManager, tag)
    }
}

fun AppCompatActivity.hideProgress(tag: String = PROGRESS_TAG) {
    val progressDialog = supportFragmentManager.findFragmentByTag(tag) as? ProgressDialogFragment?
    progressDialog?.dismiss()
}

// Fragment

fun Fragment.showProgress(tag: String = PROGRESS_TAG) {
    if (childFragmentManager.findFragmentByTag(tag) == null)
        ProgressDialogFragment.newInstance().show(childFragmentManager, tag)
}

fun Fragment.showProgress(tag: String = PROGRESS_TAG, title: String, subtitle: String) {
    if (childFragmentManager.findFragmentByTag(tag) == null) {
        ProgressDialogFragment.newInstance().apply {
            arguments = Bundle().apply {
                putString(PROGRESS_TITLE, title)
                putString(PROGRESS_SUBTITLE, subtitle)
            }
        }.show(childFragmentManager, tag)
    }
}

fun Fragment.hideProgress(tag: String = PROGRESS_TAG) {
    val progressDialog = childFragmentManager.findFragmentByTag(tag) as? ProgressDialogFragment
    progressDialog?.dismiss()
}