package com.example.localtrader.product.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentProductProfileBinding
import com.example.localtrader.viewmodels.ProductViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ProductProfileFragment : DialogFragment() {
    private lateinit var binding : FragmentProductProfileBinding
    private lateinit var storage : FirebaseStorage

    private val productViewModel : ProductViewModel by activityViewModels()

    private var isLayerVisible : Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_profile, container, false)

        setUpVisuals()
        setUpListeners()
        setProductData()
        return binding.root
    }

    private fun setUpListeners(){
        binding.productImage.setOnClickListener {
            switchLayer()
        }
    }

    private fun setUpVisuals(){
        //binding.infoLayout.visibility = View.GONE
    }

    private fun setProductData()
    {
        val product = productViewModel.product
        storage.reference.child("products/${product.productId}/image")
            .downloadUrl
            .addOnSuccessListener { uri->
                Glide.with(requireActivity())
                    .load(uri)
                    .centerCrop()
                    .into(binding.productImage)
            }

        binding.productDescription.text = product.description
    }

    private fun switchLayer(){
        if (isLayerVisible){
            binding.infoLayout.animate()
                .alpha(0f)
                .duration = 200L

            isLayerVisible = false
        }
        else{
            binding.infoLayout.animate()
                .alpha(0.8f)
                .duration = 200L

            isLayerVisible = true
        }
    }


}