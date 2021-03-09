package com.example.localtrader.main_screens.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentFavoriteItemBinding
import com.example.localtrader.product.models.Product
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class FavoriteItemFragment( val product : Product) : Fragment() {

    private lateinit var binding : FragmentFavoriteItemBinding
    private lateinit var storage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_item, container, false)
        setData()
        return binding.root
    }

    private fun setData(){
        binding.price.text = product.price.toString()
        binding.productDescription.text = product.description

        storage.reference.child("products/${product.productId}/image")
            .downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(requireActivity())
                    .load(uri)
                    .centerCrop()
                    .into(binding.productImage)
            }
    }




}