package com.example.localtrader.product.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentProductBottomModalBinding
import com.example.localtrader.viewmodels.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class ProductBottomModalFragment : BottomSheetDialogFragment() {

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
            val dialog = ProductProfileFragment()
            dialog.show(requireActivity().supportFragmentManager, null)
            this.dismiss()
        }

        binding.deleteProductButton.setOnClickListener {
            deleteProduct()
        }

    }

    private fun deleteProduct()
    {
       val dialog = DeleteProductDialogFragment()
        dialog.show(requireActivity().supportFragmentManager, null)
        this.dismiss()
    }



}