package com.example.localtrader.orders.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.localtrader.R


class SelectDialogFragment(
    private val listener : OnSelectedListener
) : DialogFragment(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)

            builder.setItems(R.array.order_action_list) { dialog, which ->
                listener.onDialogItemSelected(which)
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    interface OnSelectedListener{
        fun onDialogItemSelected(which : Int)
    }


}