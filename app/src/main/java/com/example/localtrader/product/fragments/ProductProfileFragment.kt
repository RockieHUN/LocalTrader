package com.example.localtrader.product.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentProductProfileBinding
import com.example.localtrader.viewmodels.FavoritesViewModel
import com.example.localtrader.viewmodels.ProductViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProductProfileFragment : Fragment(){
    private val storage = Firebase.storage

    private val productViewModel : ProductViewModel by activityViewModels()
    private val userViewModel : UserViewModel by activityViewModels()
    private val favoritesViewModel : FavoritesViewModel by activityViewModels()

    private lateinit var binding : FragmentProductProfileBinding

   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_profile, container, false)

        setUpVisuals()
        setUpListeners()
        setProductData()
        return binding.root
    }


    private fun setUpListeners(){
        binding.orderButton.setOnClickListener {
           findNavController().navigate(R.id.action_productProfileFragment_to_createOrderFragment)
        }

        binding.favoriteButton.setOnClickListener {
            favoritesViewModel.favoritesAction(productViewModel.product, requireContext(), binding.favoriteButton)
        }
    }

    private fun setUpVisuals(){
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        if (userViewModel.user.value!!.businessId == productViewModel.product.businessId){
            binding.orderButtonHolder.visibility = View.GONE
        }

        favoritesViewModel.likeButtonVisual(productViewModel.product, binding.favoriteButton)
    }



    private fun setProductData()
    {
        val product = productViewModel.product
        storage.reference.child("products/${product.productId}/PRODUCT_IMAGE_1920")
            .downloadUrl
            .addOnSuccessListener { uri->
                Glide.with(requireActivity())
                    .load(uri)
                    .centerCrop()
                    .into(binding.productImage)
            }

        binding.productDescription .text = product.description
        binding.price.text = product.price.toString()
        binding.businessName.text = product.businessName
        binding.productName.text = product.name
    }


}