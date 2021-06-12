package com.example.localtrader.product.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.localtrader.R
import com.example.localtrader.YesNoDialogFragment
import com.example.localtrader.databinding.FragmentProductBottomModalBinding
import com.example.localtrader.utils.MySnackBar
import com.example.localtrader.viewmodels.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ProductBottomModalFragment(
    val listener : OnModalButtonClickListener
) : BottomSheetDialogFragment(), YesNoDialogFragment.NoticeDialogListener {

    private lateinit var binding : FragmentProductBottomModalBinding
    private val productViewModel : ProductViewModel by activityViewModels()
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage


    interface OnModalButtonClickListener{
        fun onViewProductClick(window : ProductBottomModalFragment)
        fun onShareProductClick(window : ProductBottomModalFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_bottom_modal, container, false)
        setUpListeners()
        return binding.root
    }

    

    private fun setUpListeners()
    {
        binding.showProductButton.setOnClickListener {
            listener.onViewProductClick(this)
        }

        binding.deleteProductButton.setOnClickListener {
            deleteProduct()
        }

        binding.shareProductButton.setOnClickListener {
            listener.onShareProductClick(this)
        }
    }


    private fun deleteProduct()
    {
       val dialog = YesNoDialogFragment(resources.getString(R.string.delete_confirmation), this)
        dialog.show(requireActivity().supportFragmentManager, null)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val productId = productViewModel.product.productId
        val businessId = productViewModel.product.businessId

        firestore.collection("products")
            .document(productId)
            .delete()
            .addOnSuccessListener {

                //TODO: DELETE
                storage.reference.child("products/${productId}/image")
                    .delete()
                    .addOnSuccessListener {
                        productViewModel.loadBusinessProducts(businessId)

                    }
            }
        this.dismiss()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        this.dismiss()
        return
    }


}