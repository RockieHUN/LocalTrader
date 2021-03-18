package com.example.localtrader

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class NoticeDialog(
    private val message : String,
    private val dismissListener: OnDismissListener
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{

            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
                .setPositiveButton(R.string.ok){ dialog, id ->
                    dismissListener.onNoticeDismiss(this)
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }


    interface OnDismissListener {
        fun onNoticeDismiss(dialog : DialogFragment)
    }
}