package com.example.localtrader.product.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentDeleteProductDialogBinding
import com.example.localtrader.viewmodels.ProductViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class DeleteProductDialogFragment : DialogFragment() {

    private lateinit var storage : FirebaseStorage
    private lateinit var firestore : FirebaseFirestore
    private val productViewModel : ProductViewModel by activityViewModels()

    private lateinit var binding : FragmentDeleteProductDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
        firestore = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delete_product_dialog, container, false)
        setUpListeners()

        return binding.root
    }


    private fun setUpListeners()
    {
        binding.noButton.setOnClickListener {
            this.dismiss()
        }

        binding.yesButton.setOnClickListener {
            deleteProduct()
        }
    }

    private fun deleteProduct(){
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
                        this.dismiss()
                    }
        }
    }

}