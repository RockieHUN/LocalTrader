package com.example.localtrader.product.fragments

import android.os.Bundle
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
import com.example.localtrader.viewmodels.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class ProductBottomModalFragment : BottomSheetDialogFragment(), YesNoDialogFragment.NoticeDialogListener {

    private lateinit var binding : FragmentProductBottomModalBinding
    private val productViewModel : ProductViewModel by activityViewModels()
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = Firebase.firestore
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_bottom_modal, container, false)
        setUpListeners()
        return binding.root
    }


    private fun setUpListeners()
    {
        binding.showProductButton.setOnClickListener {
            findNavController().navigate(R.id.action_productBottomModalFragment_to_productProfileFragment)
        }

        binding.deleteProductButton.setOnClickListener {
            deleteProduct()
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