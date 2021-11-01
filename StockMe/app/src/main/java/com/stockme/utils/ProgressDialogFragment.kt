package com.stockme.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.view.View
import android.widget.TextView
import com.stockme.databinding.FragmentProgressDialogBinding


class ProgressDialogFragment : androidx.fragment.app.DialogFragment() {

    private lateinit var binding: FragmentProgressDialogBinding

    companion object {
        fun newInstance(): ProgressDialogFragment {
            return ProgressDialogFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentProgressDialogBinding.inflate(LayoutInflater.from(context))
        return createProgressDialog(context) ?: super.onCreateDialog(savedInstanceState)
    }

    private fun createProgressDialog(context: Context?): Dialog? {

        context ?: return null

        val alertDialog = Dialog(context)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(binding.root)

        this.arguments?.let { bundle ->
            bundle.getString(PROGRESS_TITLE)?.let { binding.title.setDescription(it) }
            bundle.getString(PROGRESS_SUBTITLE)?.let { binding.subtitle.setDescription(it) }
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        alertDialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)

        return alertDialog
    }
}

fun TextView.setDescription(description: String) {
    text = description
    visibility = View.VISIBLE
}