package com.example.localtrader.product.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.localtrader.R
import com.example.localtrader.databinding.FragmentProductProfileBinding
import com.example.localtrader.repositories.FavoritesRepository
import com.example.localtrader.viewmodels.NavigationViewModel
import com.example.localtrader.viewmodels.ProductViewModel
import com.example.localtrader.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ProductProfileFragment : DialogFragment() {
    private lateinit var binding : FragmentProductProfileBinding
    private lateinit var storage : FirebaseStorage
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    private lateinit var favoritesRepository : FavoritesRepository

    private val productViewModel : ProductViewModel by activityViewModels()
    private val navigationViewModel : NavigationViewModel by activityViewModels()
    private val userViewModel : UserViewModel by activityViewModels()

    private var isLayerVisible : Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage
        firestore = Firebase.firestore
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_profile, container, false)

        favoritesRepository = FavoritesRepository(context, binding.favoriteButton)

        setUpVisuals()
        setUpListeners()
        setProductData()
        return binding.root
    }

   /* override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            builder.setView(inflater.inflate(R.layout.fragment_product_profile, null))

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }*/

    private fun setUpListeners(){
        binding.productImage.setOnClickListener {
            switchLayer()
        }

        binding.orderButton.setOnClickListener {
            this.dismiss()
            when(navigationViewModel.origin){
                1 -> findNavController().navigate(R.id.action_timeLineFragment_to_createOrderFragment)
                2 -> findNavController().navigate(R.id.action_businessProfileFragment_to_createOrderFragment)
                3 -> findNavController().navigate(R.id.action_productGridFragment_to_createOrderFragment)
            }
        }

        binding.favoriteButton.setOnClickListener {
            favoritesRepository.favoritesAction(productViewModel.product)
        }
    }

    private fun setUpVisuals(){
        if (userViewModel.user.value!!.businessId == productViewModel.product.businessId){
            binding.orderButton.visibility = View.GONE
        }

        favoritesRepository.likeButtonVisual(productViewModel.product)
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
        binding.price.text = product.price.toString()
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
                .alpha(1f)
                .duration = 200L

            isLayerVisible = true
        }
    }


}