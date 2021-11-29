package com.stockme.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.stockme.R
import com.stockme.login.LoginActivity

class LogoutDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(R.string.dialog_logout_text)
            positiveButton(R.string.dialog_logout_yes) {
                Firebase.auth.signOut()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            negativeButton(R.string.dialog_logout_no) {
                dismiss()
            }
        }
    }
}